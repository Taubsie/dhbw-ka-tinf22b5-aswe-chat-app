package de.dhbw.ka.tinf22b5;

import java.awt.*;
import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class LinuxTerminalHandler implements TerminalHandler {

    private static final int MAX_TERMIOS_SIZE = 1024;
    private static final int ECHO = 8;
    private static final int ICANON = 2;
    private static final int TCSAFLUSH = 2;
    private static final int STDIN_FILENO = 0;

    private static final int TIOCGWINSZ = 21523;

    private int originalLFlag;

    private MethodHandle tcgetattr;
    private MethodHandle tcsetattr;

    private MethodHandle ioctl;

    @Override
    public void init() {
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();

        MemorySegment tcgetattrAddress = stdlib.find("tcgetattr").orElseThrow();
        FunctionDescriptor tcgetattrDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        tcgetattr = linker.downcallHandle(tcgetattrAddress, tcgetattrDescriptor);

        MemorySegment tcsetattrAddress = stdlib.find("tcsetattr").orElseThrow();
        FunctionDescriptor tcsetattrDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        tcsetattr = linker.downcallHandle(tcsetattrAddress, tcsetattrDescriptor);

        MemorySegment ioctlAddress = stdlib.find("ioctl").orElseThrow();
        FunctionDescriptor ioctlDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS.withoutName());
        ioctl = linker.downcallHandle(ioctlAddress, ioctlDescriptor);

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment termios = arena.allocate(MAX_TERMIOS_SIZE);
            tcgetattr.invoke(STDIN_FILENO, termios);

            originalLFlag = termios.getAtIndex(ValueLayout.JAVA_INT, 3);
            int clflag = originalLFlag & ~(ECHO | ICANON);
            termios.setAtIndex(ValueLayout.JAVA_INT, 3, clflag);

            tcsetattr.invoke(STDIN_FILENO, TCSAFLUSH, termios);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getChar() {
        try {
            return System.in.read();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public Dimension getSize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sizeStruct = arena.allocate(MAX_TERMIOS_SIZE);

            ioctl.invoke(0, TIOCGWINSZ, sizeStruct);

            return new Dimension(sizeStruct.getAtIndex(ValueLayout.JAVA_SHORT, 1), sizeStruct.getAtIndex(ValueLayout.JAVA_SHORT, 0));
        } catch (Throwable e) {
            return new Dimension(-1, -1);
        }
    }

    @Override
    public void deinit() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment termios = arena.allocate(MAX_TERMIOS_SIZE);
            tcgetattr.invoke(STDIN_FILENO, termios);

            termios.setAtIndex(ValueLayout.JAVA_INT, 3, originalLFlag);

            tcsetattr.invoke(STDIN_FILENO, TCSAFLUSH, termios);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}