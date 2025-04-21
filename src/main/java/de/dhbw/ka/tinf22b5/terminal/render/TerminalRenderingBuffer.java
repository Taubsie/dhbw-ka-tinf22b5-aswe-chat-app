package de.dhbw.ka.tinf22b5.terminal.render;

public interface TerminalRenderingBuffer {

    TerminalRenderingBuffer alternateScreenEnable();
    TerminalRenderingBuffer alternateScreenDisable();

    TerminalRenderingBuffer clearScreen();
    TerminalRenderingBuffer scrollScreenUp();
    TerminalRenderingBuffer moveCursor(int x, int y);

    TerminalRenderingBuffer setCursorVisible(boolean visible);

    TerminalRenderingBuffer addBytes(byte... bytes);
    TerminalRenderingBuffer addBytes(int... bytes);
    TerminalRenderingBuffer addString(String string);
    TerminalRenderingBuffer nextLine();

    TerminalRenderingBuffer clear();

    byte[] getBuffer();
}
