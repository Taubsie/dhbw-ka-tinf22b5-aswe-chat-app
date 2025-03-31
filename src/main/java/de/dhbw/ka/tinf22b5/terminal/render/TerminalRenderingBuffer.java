package de.dhbw.ka.tinf22b5.terminal.render;

public interface TerminalRenderingBuffer {

    TerminalRenderingBuffer clearScreen();
    TerminalRenderingBuffer scrollScreenUp();
    TerminalRenderingBuffer moveCursor(int x, int y);

    TerminalRenderingBuffer addBytes(byte... bytes);
    TerminalRenderingBuffer addBytes(int... bytes);
    TerminalRenderingBuffer addString(String string);
    TerminalRenderingBuffer nextLine();

    byte[] getBuffer();
}
