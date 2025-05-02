package de.dhbw.ka.tinf22b5.tests;

import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyParser;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyType;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTerminalKeyParser {

    @Test
    public void testNull() {
        TerminalKeyParser parser = new TerminalKeyParser();

        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(null);

        assertArrayEquals(new byte[0], parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UNKNOWN, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_UNKNOWN, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testEmpty() {
        TerminalKeyParser parser = new TerminalKeyParser();

        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(new byte[0]);

        assertArrayEquals(new byte[0], parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UNKNOWN, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_UNKNOWN, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testSingleAsciiSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = {'k'};
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(sequence, parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_k, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_ASCII, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testSingleAsciiAltModifierSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = { 0x1b, 'O'};
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(Arrays.copyOfRange(sequence, 1, 2), parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_O, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_ASCII, parsed.getKeyType());
        assertEquals(true, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testSingleAsciiError() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = {(byte) 0xff};
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(sequence, parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UNKNOWN, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_UNKNOWN, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testMultiAsciiSpecialSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = { 0x1b, 91, 67};
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(sequence, parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_RIGHT, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_SPECIAL_KEY, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testMultiAsciiAllModifierSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = { 0x1b, 91, 49, 59, 56, 65};
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(sequence, parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UP, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_SPECIAL_KEY, parsed.getKeyType());
        assertEquals(true, parsed.isAltPressed());
        assertEquals(true, parsed.isCtrlPressed());
        assertEquals(true, parsed.isShiftPressed());
    }

    @Test
    public void testSingleUnicodeSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = "ö".getBytes(StandardCharsets.UTF_8);
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(sequence, parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UNKNOWN, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_UNICODE, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testSingleUnicodeAltModifierSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = "\u001bß".getBytes(StandardCharsets.UTF_8);
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(Arrays.copyOfRange(sequence, 1, 3), parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UNKNOWN, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_UNICODE, parsed.getKeyType());
        assertEquals(true, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }

    @Test
    public void testUnicodeStringSuccess() {
        TerminalKeyParser parser = new TerminalKeyParser();

        byte[] sequence = "abcjdeja".getBytes(StandardCharsets.UTF_8);
        TerminalKeyEvent parsed = parser.parseTerminalKeyInput(sequence);

        assertArrayEquals(sequence, parsed.getUtf8Chars());
        assertEquals(TerminalKey.TK_UNKNOWN, parsed.getTerminalKey());
        assertEquals(TerminalKeyType.TKT_UNICODE_STRING, parsed.getKeyType());
        assertEquals(false, parsed.isAltPressed());
        assertEquals(false, parsed.isCtrlPressed());
        assertEquals(false, parsed.isShiftPressed());
    }
}