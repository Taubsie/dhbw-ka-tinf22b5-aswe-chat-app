package de.dhbw.ka.tinf22b5.terminal.lin;

import de.dhbw.ka.tinf22b5.terminal.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.TerminalHandlerException;

import java.awt.*;
import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class LinuxTerminalHandler implements TerminalHandler {

    /* ---------------------
     * TERMIOS constants
     * ---------------------
     */
    private static final int MAX_TERMIOS_SIZE = 1024;

    // iflags
    private static final int TIO_BRKINT = 2;
    private static final int TIO_ICRNL = 256;
    private static final int TIO_INPCK = 16;
    private static final int TIO_ISTRIP = 32;
    private static final int TIO_IXON = 1024;

    // oflags
    private static final int TIO_OPOST = 1;

    // cflags
    private static final int TIO_CS8 = 48;

    // lflags
    private static final int TIO_ECHO = 8;
    private static final int TIO_ICANON = 2;
    private static final int TIO_IEXTEN = 32768;
    private static final int TIO_ISIG = 1;

    /* ---------------------
     * tcattr constants
     * ---------------------
     */
    private static final int STDIN_FILENO = 0;
    private static final int TCSAFLUSH = 2;

    /* ---------------------
     * ioctl constants
     * ---------------------
     */
    private static final int TIOCGWINSZ = 21523;


    private MethodHandle hdlTcgetattr;
    private MethodHandle hdlTcsetattr;

    // storing original flags for restoring
    private int origIFlag;
    private int origOFlag;
    private int origCFlag;
    private int origLFlag;

    private MethodHandle hdlIoctl;

    @Override
    public void init() throws TerminalHandlerException {
        getCFunctionHandles();
        enableRawMode();
    }

    private void getCFunctionHandles() {
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();

        /* ---------------------
         * tcattr method hdls
         * ---------------------
         */
        MemorySegment tcgetattrAddress = stdlib.find("tcgetattr").orElseThrow();
        FunctionDescriptor tcgetattrDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        hdlTcgetattr = linker.downcallHandle(tcgetattrAddress, tcgetattrDescriptor);

        MemorySegment tcsetattrAddress = stdlib.find("tcsetattr").orElseThrow();
        FunctionDescriptor tcsetattrDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        hdlTcsetattr = linker.downcallHandle(tcsetattrAddress, tcsetattrDescriptor);

        /* ---------------------
         * ioctl method hdl
         * ---------------------
         */
        MemorySegment ioctlAddress = stdlib.find("ioctl").orElseThrow();
        FunctionDescriptor ioctlDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS);
        hdlIoctl = linker.downcallHandle(ioctlAddress, ioctlDescriptor);
    }

    private void enableRawMode() throws TerminalHandlerException {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment termios = arena.allocate(MAX_TERMIOS_SIZE);
            int err = (int) hdlTcgetattr.invoke(STDIN_FILENO, termios);
            if (err == -1)
                throw new TerminalHandlerException("Couldn't get termios attributes");

            int iflag = termios.getAtIndex(ValueLayout.JAVA_INT, 0);
            int oflag = termios.getAtIndex(ValueLayout.JAVA_INT, 1);
            int cflag = termios.getAtIndex(ValueLayout.JAVA_INT, 2);
            int lflag = termios.getAtIndex(ValueLayout.JAVA_INT, 3);

            origIFlag = iflag;
            origOFlag = oflag;
            origCFlag = cflag;
            origLFlag = lflag;

            iflag &= ~(TIO_BRKINT | TIO_ICRNL | TIO_INPCK | TIO_ISTRIP | TIO_IXON);
            oflag &= ~(TIO_OPOST);
            cflag |= (TIO_CS8);
            lflag &= ~(TIO_ECHO | TIO_ICANON | TIO_IEXTEN | TIO_ISIG);

            setTermiosVals(termios, iflag, oflag, cflag, lflag);

            // dont wrap itself
        } catch (TerminalHandlerException e) {
            throw e;
        } catch (Throwable e) {
            throw new TerminalHandlerException(e);
        }
    }

    private void disableRawMode() throws TerminalHandlerException {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment termios = arena.allocate(MAX_TERMIOS_SIZE);
            int err = (int) hdlTcgetattr.invoke(STDIN_FILENO, termios);
            if (err == -1)
                throw new TerminalHandlerException("Couldn't get termios attributes");

            setTermiosVals(termios, origIFlag, origOFlag, origCFlag, origLFlag);

            // dont wrap itself
        } catch (TerminalHandlerException e) {
            throw e;
        } catch (Throwable e) {
            throw new TerminalHandlerException(e);
        }
    }

    private void setTermiosVals(MemorySegment termios, int iflag, int oflag, int cflag, int lflag) throws Throwable {
        int err;
        termios.setAtIndex(ValueLayout.JAVA_INT, 0, iflag);
        termios.setAtIndex(ValueLayout.JAVA_INT, 1, oflag);
        termios.setAtIndex(ValueLayout.JAVA_INT, 2, cflag);
        termios.setAtIndex(ValueLayout.JAVA_INT, 3, lflag);

        err = (int) hdlTcsetattr.invoke(STDIN_FILENO, TCSAFLUSH, termios);
        if (err == -1)
            throw new TerminalHandlerException("Couldn't set termios attributes");
    }

    @Override
    public byte[] getChar() {
        byte[] buffer = new byte[BUFFER_SIZE];

        int numRead = 0;
        try {
            numRead = System.in.read(buffer);
        } catch (IOException e) {
            return new byte[0];
        }

        // probably eof signal or kill
        if(numRead <= -1)
            return new byte[0];

        byte[] ret = new byte[numRead];
        System.arraycopy(buffer, 0, ret, 0, numRead);
        return ret;
    }

    @Override
    public Dimension getSize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sizeStruct = arena.allocate(MAX_TERMIOS_SIZE);

            int err = (int) hdlIoctl.invoke(0, TIOCGWINSZ, sizeStruct);
            if (err == -1)
                throw new TerminalHandlerException("Coudln't get window size with ioctl");

            return new Dimension(sizeStruct.getAtIndex(ValueLayout.JAVA_SHORT, 1), sizeStruct.getAtIndex(ValueLayout.JAVA_SHORT, 0));
        } catch (Throwable e) {
            return new Dimension(-1, -1);
        }
    }

    @Override
    public void deinit() throws TerminalHandlerException {
        disableRawMode();
    }
}