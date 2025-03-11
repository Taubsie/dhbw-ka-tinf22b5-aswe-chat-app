package de.dhbw.ka.tinf22b5.terminal;

import java.util.Arrays;

public class TerminalKeyEvent {

    private final byte[] utf8Chars;

    private final TerminalKeyType keyType;

    private final int terminalKey;

    public TerminalKeyEvent() {
        this(new byte[0]);
    }

    public TerminalKeyEvent(byte[] keys) {
        this(keys, TerminalKeyType.TKT_UNKNOWN, TerminalKey.TK_UNKNOWN);
    }

    public TerminalKeyEvent(byte[] utf8Chars, TerminalKeyType keyType, int terminalKey) {
        this.utf8Chars = utf8Chars;
        this.keyType = keyType;
        this.terminalKey = terminalKey;
    }

    public byte[] getUtf8Chars() {
        return Arrays.copyOf(utf8Chars, utf8Chars.length);
    }

    public TerminalKeyType getKeyType() {
        return keyType;
    }

    public int getTerminalKey() {
        return terminalKey;
    }
}
