package de.dhbw.ka.tinf22b5.terminal.win;

import de.dhbw.ka.tinf22b5.terminal.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.TerminalHandlerException;

import java.awt.*;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class WindowsTerminalHandler implements TerminalHandler {
    private static final int  MAX_CONSOLE_SCREEN_BUFFER_INFO_SIZE = 1024;
    private static final int STD_OUTPUT_HANDLE = -11;

    private MethodHandle hdlGetch;

    private MethodHandle hdlGetStdHandle;
    private MethodHandle hdlGetConsoleScreenBufferInfo;

    @Override
    public void init() throws TerminalHandlerException {
        try {
            Linker linker = Linker.nativeLinker();
            SymbolLookup stdlib = linker.defaultLookup();
            SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32", Arena.ofShared());

            MemorySegment getchAddress = stdlib.find("_getch").orElseThrow();
            FunctionDescriptor getchDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT);
            hdlGetch = linker.downcallHandle(getchAddress, getchDescriptor);

            MemorySegment getStdHandleAddress = kernel32.find("GetStdHandle").orElseThrow();
            FunctionDescriptor getStdHandleDescriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
            hdlGetStdHandle = linker.downcallHandle(getStdHandleAddress, getStdHandleDescriptor);

            MemorySegment getConsoleScreenBufferInfoAddress = kernel32.find("GetConsoleScreenBufferInfo").orElseThrow();
            FunctionDescriptor getConsoleScreenBufferInfoDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            hdlGetConsoleScreenBufferInfo = linker.downcallHandle(getConsoleScreenBufferInfoAddress, getConsoleScreenBufferInfoDescriptor);
        } catch (Exception e) {
            throw new TerminalHandlerException(e.getMessage());
        }
    }

    @Override
    public int getChar() {
        try (Arena arena = Arena.ofConfined()) {
            return (int) hdlGetch.invoke();
        } catch (Throwable e) {
            return -1;
        }
    }

    @Override
    public Dimension getSize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment consoleInfo = arena.allocate(MAX_CONSOLE_SCREEN_BUFFER_INFO_SIZE);
            MemorySegment stdHandle = (MemorySegment) hdlGetStdHandle.invoke(STD_OUTPUT_HANDLE);
            hdlGetConsoleScreenBufferInfo.invoke(stdHandle, consoleInfo);

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
    public void deinit() {
        // do nothing
    }
}