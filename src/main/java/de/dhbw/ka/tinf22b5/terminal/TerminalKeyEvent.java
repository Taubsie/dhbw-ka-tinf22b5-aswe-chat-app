package de.dhbw.ka.tinf22b5.terminal;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TerminalKeyEvent {

    private final byte[] utf8Chars;
    private final TerminalKeyType keyType;
    private final int terminalKey;
    private final int modifiers;

    public TerminalKeyEvent() {
        this(new byte[0]);
    }

    public TerminalKeyEvent(byte[] keys) {
        this(keys, TerminalKeyType.TKT_UNKNOWN);
    }

    public TerminalKeyEvent(byte[] keys, TerminalKeyType keyType) {
        this(keys, keyType, TerminalKey.TK_UNKNOWN);
    }

    public TerminalKeyEvent(byte[] utf8Chars, TerminalKeyType keyType, int terminalKey) {
        this(utf8Chars, keyType, terminalKey, 0);
    }

    public TerminalKeyEvent(byte[] utf8Chars, TerminalKeyType keyType, int terminalKey, int modifiers) {
        this.utf8Chars = utf8Chars;
        this.keyType = keyType;
        this.terminalKey = terminalKey;
        this.modifiers = modifiers;
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

    public boolean isCtrlPressed() {
        return (modifiers & TerminalKey.TK_MODIFIER_CTRL) != 0;
    }

    public boolean isShiftPressed() {
        return (modifiers & TerminalKey.TK_MODIFIER_SHIFT) != 0;
    }

    public boolean isAltPressed() {
        return (modifiers & TerminalKey.TK_MODIFIER_ALT) != 0;
    }

    public String convertAllToUTF8String() {
        String prefix = "";

        if (isCtrlPressed()) {
            prefix += "Ctrl + ";
        }

        if (isShiftPressed()) {
            prefix += "Shift + ";
        }

        if (isAltPressed()) {
            prefix += "Alt + ";
        }

        if (keyType == TerminalKeyType.TKT_ASCII || keyType == TerminalKeyType.TKT_UNICODE || keyType == TerminalKeyType.TKT_UNICODE_STRING)
            return prefix + new String(utf8Chars, StandardCharsets.UTF_8);

        return prefix + TerminalKey.getKeyText(terminalKey);
    }
}
