package de.dhbw.ka.tinf22b5.terminal.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BaseTerminalRenderingBuffer implements TerminalRenderingBuffer {

    private final List<Byte> screenBuffer;

    public BaseTerminalRenderingBuffer() {
        screenBuffer = new ArrayList<>();
    }

    @Override
    public TerminalRenderingBuffer alternateScreenEnable() {
        addBytes(0x1b, '[', '?', '1', '0', '4', '9', 'h', 0x1b, '[', '2', '2', ';', '0', ';', '0', 't');
        return this;
    }

    @Override
    public TerminalRenderingBuffer alternateScreenDisable() {
        addBytes(0x1b, '[', '?', '1', '0', '4', '9', 'l', 0x1b, '[', '2', '3', ';', '0', ';', '0', 't');
        return this;
    }

    @Override
    public TerminalRenderingBuffer moveCursorToHome() {
        addBytes(0x1b, '[', 'H');
        return this;
    }

    @Override
    public TerminalRenderingBuffer moveCursor(int x, int y) {
        String escapeSequence = "\u001b[%d;%dH".formatted(y, x);
        addBytes(escapeSequence.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public TerminalRenderingBuffer scrollScreenUp() {
        addBytes(0x1b, '[', '2', 'J');
        moveCursorToHome();
        return this;
    }

    @Override
    public TerminalRenderingBuffer setCursorVisible(boolean visible) {
        addBytes(0x1b, '[', '?', '2', '5', visible ? 'h' : 'l');
        return this;
    }

    @Override
    public TerminalRenderingBuffer resetGraphicsModes() {
        addBytes(0x1b, '[', 'm');
        return this;
    }

    @Override
    public TerminalRenderingBuffer addBytes(byte... bytes) {
        for(byte b : bytes) {
            screenBuffer.add(b);
        }

        return this;
    }

    @Override
    public TerminalRenderingBuffer addBytes(int... bytes) {
        for (int b : bytes) {
            screenBuffer.add((byte) b);
        }

        return this;
    }

    @Override
    public TerminalRenderingBuffer addString(String string) {
        if(string == null) return this;
        addBytes(string.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public TerminalRenderingBuffer nextLine() {
        addString("\r\n");
        return this;
    }

    @Override
    public TerminalRenderingBuffer clear() {
        screenBuffer.clear();
        return this;
    }

    @Override
    public byte[] getBuffer() {
        int screenBufferSize = screenBuffer.size();
        byte[] buffer = new byte[screenBufferSize];

        for (int i = 0; i < screenBufferSize; i++) {
            buffer[i] = screenBuffer.get(i);
        }

        return buffer;
    }
}
