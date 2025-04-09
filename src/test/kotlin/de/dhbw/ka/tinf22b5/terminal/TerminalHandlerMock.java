package de.dhbw.ka.tinf22b5.terminal;

import de.dhbw.ka.tinf22b5.dialog.Dialog;
import de.dhbw.ka.tinf22b5.terminal.handler.BaseTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;

import java.awt.Dimension;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TerminalHandlerMock extends BaseTerminalHandler {
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

    TerminalHandlerMock(Dialog currentDialog) throws IOException {
        super(currentDialog);

        //TODO fix missing terminal reference
        /*
        // enter
        inputQueue.add(new KeyArrayCombination(new TerminalKeyEvent(new byte[]{0x1b}, TerminalKeyType.TKT_ASCII, 0x1b, 0), new byte[]{0x1b}));

        // a
        inputQueue.add(new KeyArrayCombination(new TerminalKeyEvent(new byte[]{'a'}, TerminalKeyType.TKT_ASCII, TerminalKey.TK_a, 0), new byte[]{'a'}));

        // alt c
        inputQueue.add(new KeyArrayCombination(new TerminalKeyEvent(new byte[]{'c'}, TerminalKeyType.TKT_ASCII, TerminalKey.TK_c, TerminalKey.TK_MODIFIER_ALT), new byte[]{0x1b, 'c'}));
        */
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