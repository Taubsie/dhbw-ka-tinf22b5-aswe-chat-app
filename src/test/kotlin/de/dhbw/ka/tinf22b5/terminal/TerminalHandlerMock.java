package de.dhbw.ka.tinf22b5.terminal;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class TerminalHandlerMock implements TerminalHandler {
    private final Queue<byte[]> inputQueue = new LinkedList<>();

    TerminalHandlerMock() {
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { 0x1b });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
        inputQueue.add(new byte[] { TerminalKey.TK_0 });
    }

    @Override
    public void init() {
    }

    @Override
    public void deinit() {
    }

    @Override
    public byte[] getChar() {
        if (inputQueue.isEmpty()) {
            return null;
        } else {
            return inputQueue.remove();
        }
    }

    @Override
    public Dimension getSize() {
        return new Dimension(180, 30);
    }
}