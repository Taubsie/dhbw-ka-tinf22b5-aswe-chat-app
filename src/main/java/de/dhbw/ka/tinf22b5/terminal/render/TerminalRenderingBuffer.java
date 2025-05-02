package de.dhbw.ka.tinf22b5.terminal.render;

public interface TerminalRenderingBuffer {

    TerminalRenderingBuffer alternateScreenEnable();
    TerminalRenderingBuffer alternateScreenDisable();

    TerminalRenderingBuffer scrollScreenUp();

    TerminalRenderingBuffer moveCursorToHome();
    TerminalRenderingBuffer moveCursor(int x, int y);

    TerminalRenderingBuffer setCursorVisible(boolean visible);
    TerminalRenderingBuffer resetGraphicsModes();

    TerminalRenderingBuffer addBytes(byte... bytes);
    TerminalRenderingBuffer addBytes(int... bytes);
    TerminalRenderingBuffer addString(String string);
    TerminalRenderingBuffer nextLine();

    TerminalRenderingBuffer clear();

    byte[] getBuffer();
}
