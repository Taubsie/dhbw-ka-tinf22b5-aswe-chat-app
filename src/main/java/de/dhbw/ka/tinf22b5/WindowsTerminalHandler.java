package de.dhbw.ka.tinf22b5;

import java.awt.*;
import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class WindowsTerminalHandler implements TerminalHandler {
    private static int MAX_CONSOLE_SCREEN_BUFFER_INFO_SIZE = 1024;
    private static int STD_OUTPUT_HANDLE = -11;

    private MethodHandle _getch;

    private MethodHandle GetStdHandle;
    private MethodHandle GetConsoleScreenBufferInfo;

    @Override
    public void init() {
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();
        SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32", Arena.ofShared());

        MemorySegment _getchAddress = stdlib.find("_getch").orElseThrow();
        FunctionDescriptor _getchDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT);
        _getch = linker.downcallHandle(_getchAddress, _getchDescriptor);

        MemorySegment getStdHandleAddress = kernel32.find("GetStdHandle").orElseThrow();
        FunctionDescriptor getStdHandleDescriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
        GetStdHandle = linker.downcallHandle(getStdHandleAddress, getStdHandleDescriptor);

        MemorySegment getConsoleScreenBufferInfoAddress = kernel32.find("GetConsoleScreenBufferInfo").orElseThrow();
        FunctionDescriptor getConsoleScreenBufferInfoDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
        GetConsoleScreenBufferInfo = linker.downcallHandle(getConsoleScreenBufferInfoAddress, getConsoleScreenBufferInfoDescriptor);
    }

    @Override
    public int getChar() {
        try (Arena arena = Arena.ofConfined()) {
            return (int) _getch.invoke();
        } catch (Throwable e) {
            return -1;
        }
    }

    @Override
    public Dimension getSize() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment consoleInfo = arena.allocate(MAX_CONSOLE_SCREEN_BUFFER_INFO_SIZE);
            MemorySegment stdHandle = (MemorySegment) GetStdHandle.invoke(STD_OUTPUT_HANDLE);
            GetConsoleScreenBufferInfo.invoke(stdHandle, consoleInfo);

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