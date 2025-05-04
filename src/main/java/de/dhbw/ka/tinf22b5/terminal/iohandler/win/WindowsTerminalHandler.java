package de.dhbw.ka.tinf22b5.terminal.iohandler.win;

import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.iohandler.IOTerminalHandler;

import java.awt.*;
import java.io.IOException;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.util.concurrent.atomic.AtomicBoolean;

public class WindowsTerminalHandler extends IOTerminalHandler {

    /* ---------------------
     * console mode constants
     * ---------------------
     */
    private static final int INVALID_HANDLE_VALUE = -1;
    private static final int STD_INPUT_HANDLE = -10;
    private static final int STD_OUTPUT_HANDLE = -11;
    private static final int ENABLE_VIRTUAL_TERMINAL_INPUT = 512;
    private static final int ENABLE_WINDOW_INPUT = 8;
    private static final int ENABLE_PROCESSED_OUTPUT = 1;
    private static final int ENABLE_WRAP_AT_EOL_OUTPUT = 2;
    private static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;

    private static final int DWORD_SIZE = 4;

    /* ---------------------
     * console screen buffer constant
     * ---------------------
     */
    private static final int MAX_CONSOLE_SCREEN_BUFFER_INFO_SIZE = 1024;

    /* ---------------------
     * wait for single input constant
     * ---------------------
     */
    private static final int WAIT_OBJECT_0 = 0x00000000;
    private static final int WAIT_TIMEOUT = 0x00000102;
    private static final int ASYNC_TIMEOUT_MILLIS = 500;

    private MethodHandle hdlGetStdHandle;
    private MethodHandle hdlGetConsoleMode;
    private MethodHandle hdlSetConsoleMode;

    // storing original flags for restoring
    private int origIn;
    private int origOut;

    private MethodHandle hdlGetConsoleScreenBufferInfo;

    private MethodHandle hdlWaitForSingleObject;
    private Thread resizeListenerThread;
    private final AtomicBoolean resizeListenerRunning;

    public WindowsTerminalHandler() {
         resizeListenerRunning = new AtomicBoolean(false);
    }

    @Override
    public void init() throws TerminalHandlerException {
        getCFunctionHandles();
        createResizeListener();
        enableRawMode();
    }

    private void getCFunctionHandles() {
        Linker linker = Linker.nativeLinker();
        SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32", Arena.ofShared());

        /* ---------------------
         * console mode hdls
         * ---------------------
         */
        MemorySegment getStdHandleAddress = kernel32.find("GetStdHandle").orElseThrow();
        FunctionDescriptor getStdHandleDescriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
        hdlGetStdHandle = linker.downcallHandle(getStdHandleAddress, getStdHandleDescriptor);

        MemorySegment getConsoleModeAddress = kernel32.find("GetConsoleMode").orElseThrow();
        FunctionDescriptor getConsoleModeDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
        hdlGetConsoleMode = linker.downcallHandle(getConsoleModeAddress, getConsoleModeDescriptor);

        MemorySegment setConsoleModeAddress = kernel32.find("SetConsoleMode").orElseThrow();
        FunctionDescriptor setConsoleModeDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
        hdlSetConsoleMode = linker.downcallHandle(setConsoleModeAddress, setConsoleModeDescriptor);

        /* ---------------------
         * screen buffer info hdl
         * ---------------------
         */
        MemorySegment getConsoleScreenBufferInfoAddress = kernel32.find("GetConsoleScreenBufferInfo").orElseThrow();
        FunctionDescriptor getConsoleScreenBufferInfoDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
        hdlGetConsoleScreenBufferInfo = linker.downcallHandle(getConsoleScreenBufferInfoAddress, getConsoleScreenBufferInfoDescriptor);

        /* ---------------------
         * wait for single object hdl
         * ---------------------
         */
        MemorySegment waitForSingleObjectAddress = kernel32.find("WaitForSingleObject").orElseThrow();
        FunctionDescriptor waitForSingleObjectDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
        hdlWaitForSingleObject = linker.downcallHandle(waitForSingleObjectAddress, waitForSingleObjectDescriptor);
    }

    private synchronized void createResizeListener() {
        if (resizeListenerRunning.get())
            return;

        this.resizeListenerThread = new Thread(() -> {
            this.resizeListenerRunning.set(true);

            try (Arena _ = Arena.ofConfined()) {
                // get stdin hdl
                MemorySegment hdlIn = (MemorySegment) hdlGetStdHandle.invoke(STD_INPUT_HANDLE);
                if (hdlIn.address() == INVALID_HANDLE_VALUE)
                    return;

                Dimension prevDimension = this.getSize();

                while (this.resizeListenerRunning.get()) {
                    int statusHdl = (int) hdlWaitForSingleObject.invoke(hdlIn, ASYNC_TIMEOUT_MILLIS);
                    switch (statusHdl) {
                        // available to read
                        case WAIT_OBJECT_0:
                            Dimension newDimension = this.getSize();
                            if (!prevDimension.equals(newDimension)) {
                                prevDimension = newDimension;
                                reportResize();
                            }

                            break;
                        case WAIT_TIMEOUT:
                        default:
                            // ran in timeout
                    }
                }
            } catch (Throwable e) {
                // ignored
                e.printStackTrace();
            }
        });
        this.resizeListenerThread.setName("WindowsEventListener");
        this.resizeListenerThread.setDaemon(true);
        this.resizeListenerThread.start();
    }

    private void enableRawMode() throws TerminalHandlerException {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment pInt = arena.allocate(DWORD_SIZE);

            /* ---------------------
             * set input hdl
             * ---------------------
             */
            MemorySegment hdlIn = (MemorySegment) hdlGetStdHandle.invoke(STD_INPUT_HANDLE);
            if (hdlIn.address() == INVALID_HANDLE_VALUE)
                throw new TerminalHandlerException("Couldn't get stdin handle");

            int err = (int) hdlGetConsoleMode.invoke(hdlIn, pInt);
            if (err == 0)
                throw new TerminalHandlerException("Couldn't get current stdin console mode");

            int stdinMode = pInt.get(ValueLayout.JAVA_INT, 0);
            origIn = stdinMode;
            stdinMode = ENABLE_VIRTUAL_TERMINAL_INPUT | ENABLE_WINDOW_INPUT;

            err = (int) hdlSetConsoleMode.invoke(hdlIn, stdinMode);
            if (err == 0)
                throw new TerminalHandlerException("Couldn't set current stdin console mode");

            /* ---------------------
             * set output hdl
             * ---------------------
             */
            MemorySegment hdlOut = (MemorySegment) hdlGetStdHandle.invoke(STD_OUTPUT_HANDLE);
            if (hdlOut.address() == INVALID_HANDLE_VALUE)
                throw new TerminalHandlerException("Couldn't get stdout handle");

            err = (int) hdlGetConsoleMode.invoke(hdlOut, pInt);
            if (err == 0)
                throw new TerminalHandlerException("Couldn't get current stdout console mode");

            int stdoutMode = pInt.get(ValueLayout.JAVA_INT, 0);
            origOut = stdoutMode;
            stdoutMode = ENABLE_PROCESSED_OUTPUT | ENABLE_WRAP_AT_EOL_OUTPUT | ENABLE_VIRTUAL_TERMINAL_PROCESSING;

            err = (int) hdlSetConsoleMode.invoke(hdlOut, stdoutMode);
            if (err == 0)
                throw new TerminalHandlerException("Couldn't set current stdout console mode");

            // dont wrap itself
        } catch (TerminalHandlerException e) {
            throw e;
        } catch (Throwable e) {
            throw new TerminalHandlerException(e);
        }
    }

    private void disableRawMode() throws TerminalHandlerException {
        try (Arena arena = Arena.ofConfined()) {
            /* ---------------------
             * set input hdl
             * ---------------------
             */
            MemorySegment hdlIn = (MemorySegment) hdlGetStdHandle.invoke(STD_INPUT_HANDLE);
            if (hdlIn.address() == INVALID_HANDLE_VALUE)
                throw new TerminalHandlerException("Couldn't get stdin handle");

            int err = (int) hdlSetConsoleMode.invoke(hdlIn, origIn);
            if (err == 0)
                throw new TerminalHandlerException("Couldn't set current stdin console mode");

            /* ---------------------
             * set output hdl
             * ---------------------
             */
            MemorySegment hdlOut = (MemorySegment) hdlGetStdHandle.invoke(STD_OUTPUT_HANDLE);
            if (hdlOut.address() == INVALID_HANDLE_VALUE)
                throw new TerminalHandlerException("Couldn't get stdout handle");

            err = (int) hdlSetConsoleMode.invoke(hdlOut, origOut);
            if (err == 0)
                throw new TerminalHandlerException("Couldn't set current stdout console mode");

            // dont wrap itself
        } catch (TerminalHandlerException e) {
            throw e;
        } catch (Throwable e) {
            throw new TerminalHandlerException(e);
        }
    }

    @Override
    public Dimension getSize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment consoleInfo = arena.allocate(MAX_CONSOLE_SCREEN_BUFFER_INFO_SIZE);
            MemorySegment stdHandle = (MemorySegment) hdlGetStdHandle.invoke(STD_OUTPUT_HANDLE);
            if (stdHandle.address() == INVALID_HANDLE_VALUE)
                throw new TerminalHandlerException("Coudln't get stdout hdl");

            hdlGetConsoleScreenBufferInfo.invoke(stdHandle, consoleInfo);

            /* ---------------------
             * extract values from struct
             * ---------------------
             */
            int left = consoleInfo.getAtIndex(ValueLayout.JAVA_SHORT, 5);
            int top = consoleInfo.getAtIndex(ValueLayout.JAVA_SHORT, 6);
            int right = consoleInfo.getAtIndex(ValueLayout.JAVA_SHORT, 7);
            int bottom = consoleInfo.getAtIndex(ValueLayout.JAVA_SHORT, 8);

            return new Dimension(right - left + 1, bottom - top + 1);
        } catch (Throwable e) {
            return new Dimension(-1, -1);
        }
    }

    @Override
    public void deinit() throws TerminalHandlerException {
        disableRawMode();

        synchronized (this) {
            this.resizeListenerRunning.set(false);
            try {
                if (this.resizeListenerThread != null)
                    this.resizeListenerThread.join();
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }
}