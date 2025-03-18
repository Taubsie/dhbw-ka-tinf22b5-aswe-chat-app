package de.dhbw.ka.tinf22b5.terminal;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

public class TerminalHandlerMock implements TerminalHandler {
    private final List<KeyArrayCombination> inputQueue = new LinkedList<>();
    private int index = 0;

    private static class KeyArrayCombination {
        byte[] chars;
        TerminalKeyEvent event;

        KeyArrayCombination(TerminalKeyEvent event, byte[] chars) {
            this.event = event;
            this.chars = chars;
        }
    }

    TerminalHandlerMock() {
        // enter
        inputQueue.add(new KeyArrayCombination(new TerminalKeyEvent(new byte[]{0x1b}, TerminalKeyType.TKT_ASCII, 0x1b, 0), new byte[]{0x1b}));

        // a
        inputQueue.add(new KeyArrayCombination(new TerminalKeyEvent(new byte[]{'a'}, TerminalKeyType.TKT_ASCII, TerminalKey.TK_a, 0), new byte[]{'a'}));

        // alt c
        inputQueue.add(new KeyArrayCombination(new TerminalKeyEvent(new byte[]{'c'}, TerminalKeyType.TKT_ASCII, TerminalKey.TK_c, TerminalKey.TK_MODIFIER_ALT), new byte[]{0x1b, 'c'}));
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
            return inputQueue.get(index++).chars;
        }
    }

    public TerminalKeyEvent getCorrectEvent(int index) {
        if (index >= inputQueue.size()) {
            return null;
        } else {
            return inputQueue.get(index).event;
        }
    }

    @Override
    public Dimension getSize() {
        return new Dimension(180, 30);
    }
}