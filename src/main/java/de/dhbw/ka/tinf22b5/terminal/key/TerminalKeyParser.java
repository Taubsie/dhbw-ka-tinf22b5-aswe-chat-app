package de.dhbw.ka.tinf22b5.terminal.key;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TerminalKeyParser {

    public TerminalKeyEvent parseTerminalKeyInput(byte[] keys) {
        /* ---------------------
         * on wrong data return valid event to prevent crashes
         * ---------------------
         */
        if (keys == null || keys.length == 0)
            return new TerminalKeyEvent();

        /* ---------------------
         * single key probably valid ascii char
         * ---------------------
         */
        if (keys.length == 1)
            return parseSingleTerminalKeyInput(keys[0]);

        /* ---------------------
         * terminal escape chars start with 27
         * ---------------------
         */
        if (keys[0] == 0x1b) {
            /* ---------------------
             * alt + char is escape sequence + char, strip escape sequence form byte array
             * ---------------------
             */
            if (keys.length == 2) {
                TerminalKeyEvent event = parseSingleTerminalKeyInput(keys[1]);
                return new TerminalKeyEvent(event.getUtf8Chars(), event.getKeyType(), event.getTerminalKey(), TerminalKey.TK_MODIFIER_ALT);
            }

            /* ---------------------
             * probably alt + escape sequence
             * ---------------------
             */
            if (keys[1] == 0x1b) {
                TerminalKeyEvent event = parseEscapeSequence(Arrays.copyOfRange(keys, 1, keys.length));
                if (event != null)
                    return new TerminalKeyEvent(event.getUtf8Chars(), event.getKeyType(), event.getTerminalKey(), TerminalKey.TK_MODIFIER_ALT);

                return new TerminalKeyEvent(keys);
            }

            /* ---------------------
             * try to parse escape sequence
             * ---------------------
             */
            TerminalKeyEvent event = parseEscapeSequence(keys);
            if (event != null)
                return event;

            /* ---------------------
             * probably alt + utf-8 sequence
             * ---------------------
             */
            return new TerminalKeyEvent(Arrays.copyOfRange(keys, 1, keys.length), TerminalKeyType.TKT_UNICODE, TerminalKey.TK_UNKNOWN, TerminalKey.TK_MODIFIER_ALT);
        } else {
            /* ---------------------
             * probably utf-8 sequence
             * ---------------------
             */
            String tmpStr = new String(keys, StandardCharsets.UTF_8);
            int utf8CharCount = tmpStr.codePointCount(0, tmpStr.length());
            return new TerminalKeyEvent(keys, utf8CharCount <= 1 ? TerminalKeyType.TKT_UNICODE : TerminalKeyType.TKT_UNICODE_STRING);
        }
    }

    private TerminalKeyEvent parseSingleTerminalKeyInput(byte key) {
        switch (key) {
            /* ---------------------
             * ascii control chars
             * ---------------------
             */
            // already done on ctrl + keys
            // case TerminalKey.TK_TAB:
            // case TerminalKey.TK_ENTER:
            // case TerminalKey.TK_ESCAPE:

            /* ---------------------
             * ctrl + letter
             * ---------------------
             */
            case TerminalKey.TK_CTRL_SPACE:
            case TerminalKey.TK_CTRL_A:
            case TerminalKey.TK_CTRL_B:
            case TerminalKey.TK_CTRL_C:
            case TerminalKey.TK_CTRL_D:
            case TerminalKey.TK_CTRL_E:
            case TerminalKey.TK_CTRL_F:
            case TerminalKey.TK_CTRL_G:
            case TerminalKey.TK_CTRL_H:
            case TerminalKey.TK_CTRL_I:
            case TerminalKey.TK_CTRL_J:
            case TerminalKey.TK_CTRL_K:
            case TerminalKey.TK_CTRL_L:
            case TerminalKey.TK_CTRL_M:
            case TerminalKey.TK_CTRL_N:
            case TerminalKey.TK_CTRL_O:
            case TerminalKey.TK_CTRL_P:
            case TerminalKey.TK_CTRL_Q:
            case TerminalKey.TK_CTRL_R:
            case TerminalKey.TK_CTRL_S:
            case TerminalKey.TK_CTRL_T:
            case TerminalKey.TK_CTRL_U:
            case TerminalKey.TK_CTRL_V:
            case TerminalKey.TK_CTRL_W:
            case TerminalKey.TK_CTRL_X:
            case TerminalKey.TK_CTRL_Y:
            case TerminalKey.TK_CTRL_Z:
            case TerminalKey.TK_CTRL_LEFT_BRACKET:
            case TerminalKey.TK_CTRL_BACKSLASH:
            case TerminalKey.TK_CTRL_RIGHT_BRACKET:
            case TerminalKey.TK_CTRL_6:
            case TerminalKey.TK_CTRL_7:

            /* ---------------------
             * <-- ascii key for forward deletion
             * ---------------------
             */
            case TerminalKey.TK_BACKSPACE:
                return new TerminalKeyEvent(new byte[]{key}, TerminalKeyType.TKT_SPECIAL_KEY, key);

            /* ---------------------
             * ascii special chars
             * ---------------------
             */
            case TerminalKey.TK_SPACE:
            case TerminalKey.TK_EXCLAMATION_MARK:
            case TerminalKey.TK_DOUBLE_QUOTE:
            case TerminalKey.TK_HASH_TAG:
            case TerminalKey.TK_DOLLAR:
            case TerminalKey.TK_PERCENT:
            case TerminalKey.TK_AMPERSAND:
            case TerminalKey.TK_APOSTROPHE:
            case TerminalKey.TK_LEFT_PARENTHESES:
            case TerminalKey.TK_RIGHT_PARENTHESES:
            case TerminalKey.TK_ASTERISK:
            case TerminalKey.TK_PLUS:
            case TerminalKey.TK_COMMA:
            case TerminalKey.TK_MINUS:
            case TerminalKey.TK_PERIOD:
            case TerminalKey.TK_SLASH:

            /* ---------------------
             * numbers
             * ---------------------
             */
            case TerminalKey.TK_0:
            case TerminalKey.TK_1:
            case TerminalKey.TK_2:
            case TerminalKey.TK_3:
            case TerminalKey.TK_4:
            case TerminalKey.TK_5:
            case TerminalKey.TK_6:
            case TerminalKey.TK_7:
            case TerminalKey.TK_8:
            case TerminalKey.TK_9:

            /* ---------------------
             * ascii operators special chars
             * ---------------------
             */
            case TerminalKey.TK_COLON:
            case TerminalKey.TK_SEMICOLON:
            case TerminalKey.TK_LESS:
            case TerminalKey.TK_EQUALS:
            case TerminalKey.TK_GREATER:
            case TerminalKey.TK_QUESTION_MARK:
            case TerminalKey.TK_AT:

            /* ---------------------
             * upper case letters
             * ---------------------
             */
            case TerminalKey.TK_A:
            case TerminalKey.TK_B:
            case TerminalKey.TK_C:
            case TerminalKey.TK_D:
            case TerminalKey.TK_E:
            case TerminalKey.TK_F:
            case TerminalKey.TK_G:
            case TerminalKey.TK_H:
            case TerminalKey.TK_I:
            case TerminalKey.TK_J:
            case TerminalKey.TK_K:
            case TerminalKey.TK_L:
            case TerminalKey.TK_M:
            case TerminalKey.TK_N:
            case TerminalKey.TK_O:
            case TerminalKey.TK_P:
            case TerminalKey.TK_Q:
            case TerminalKey.TK_R:
            case TerminalKey.TK_S:
            case TerminalKey.TK_T:
            case TerminalKey.TK_U:
            case TerminalKey.TK_V:
            case TerminalKey.TK_W:
            case TerminalKey.TK_X:
            case TerminalKey.TK_Y:
            case TerminalKey.TK_Z:

            /* ---------------------
             * ascii special chars
             * ---------------------
             */
            case TerminalKey.TK_LEFT_BRACKET:
            case TerminalKey.TK_BACKSLASH:
            case TerminalKey.TK_RIGHT_BRACKET:
            case TerminalKey.TK_CARET:
            case TerminalKey.TK_UNDER_SCORE:
            case TerminalKey.TK_BACKTICK:

            /* ---------------------
             * lower case letters
             * ---------------------
             */
            case TerminalKey.TK_a:
            case TerminalKey.TK_b:
            case TerminalKey.TK_c:
            case TerminalKey.TK_d:
            case TerminalKey.TK_e:
            case TerminalKey.TK_f:
            case TerminalKey.TK_g:
            case TerminalKey.TK_h:
            case TerminalKey.TK_i:
            case TerminalKey.TK_j:
            case TerminalKey.TK_k:
            case TerminalKey.TK_l:
            case TerminalKey.TK_m:
            case TerminalKey.TK_n:
            case TerminalKey.TK_o:
            case TerminalKey.TK_p:
            case TerminalKey.TK_q:
            case TerminalKey.TK_r:
            case TerminalKey.TK_s:
            case TerminalKey.TK_t:
            case TerminalKey.TK_u:
            case TerminalKey.TK_v:
            case TerminalKey.TK_w:
            case TerminalKey.TK_x:
            case TerminalKey.TK_y:
            case TerminalKey.TK_z:

            /* ---------------------
             * ascii special chars
             * ---------------------
             */
            case TerminalKey.TK_LEFT_CURLY_BRACE:
            case TerminalKey.TK_PIPE:
            case TerminalKey.TK_RIGHT_CURLY_BRACE:
            case TerminalKey.TK_TILDE:
                return new TerminalKeyEvent(new byte[]{key}, TerminalKeyType.TKT_ASCII, key);

            /* ---------------------
             * unknown / undefined char
             * ---------------------
             */
            default:
                // in case you're on a Windows and UTF-8 characters aren't correctly used in the console, you can either try the following setting:
                // https://stackoverflow.com/questions/57131654/using-utf-8-encoding-chcp-65001-in-command-prompt-windows-powershell-window
                // or you have to use this return statement instead:
                // return new TerminalKeyEvent(keys, TerminalKeyType.TKT_ASCII, keys[0]);
                return new TerminalKeyEvent(new byte[]{key}, TerminalKeyType.TKT_UNKNOWN, TerminalKey.TK_UNKNOWN);
        }
    }

    private TerminalKeyEvent parseEscapeSequence(byte[] keys) {

        if (keys[1] == '[') {
            if (keys.length == 3) {
                switch (keys[2]) {
                    /* ---------------------
                     * normal mode cursor, pos1 and end keys
                     * ---------------------
                     */
                    case 'A':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_UP);
                    case 'B':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_DOWN);
                    case 'C':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_RIGHT);
                    case 'D':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_LEFT);
                    case 'H':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_POS1);
                    case 'F':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_END);
                }
            } else if (keys.length == 4 && keys[3] == '~') {
                switch (keys[2]) {
                    /* ---------------------
                     * insert, delete and page keys
                     * ---------------------
                     */
                    case '2':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_INSERT);
                    case '3':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_DELETE);
                    case '5':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_PAGE_UP);
                    case '6':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_PAGE_DOWN);
                }
            } else if (keys.length == 5 && keys[4] == '~') {
                /* ---------------------
                 * F keys
                 * ---------------------
                 */
                if (keys[2] == '1') {
                    switch (keys[3]) {
                        case '5':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F5);
                        case '7':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F6);
                        case '8':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F7);
                        case '9':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F8);
                    }
                } else if (keys[2] == '2') {
                    switch (keys[3]) {
                        case '0':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F9);
                        case '1':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F10);
                        case '3':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F11);
                        case '4':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F12);
                    }
                }
            } else if (keys.length == 6 && keys[3] == ';') {
                int modifier = convertModifierByte(keys[4]);
                if (keys[2] == '1') {
                    switch (keys[5]) {
                        /* ---------------------
                         * cursor keys
                         * ---------------------
                         */
                        case 'A':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_UP, modifier);
                        case 'B':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_DOWN, modifier);
                        case 'C':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_RIGHT, modifier);
                        case 'D':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_LEFT, modifier);

                        /* ---------------------
                         * pos1 and end keys
                         * ---------------------
                         */
                        case 'H':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_POS1, modifier);
                        case 'F':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_END, modifier);

                        /* ---------------------
                         * F1, F2, F3, F4
                         * ---------------------
                         */
                        case 'P':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F1, modifier);
                        case 'Q':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F2, modifier);
                        case 'R':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F3, modifier);
                        case 'S':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F4, modifier);
                    }
                } else if (keys[5] == '~') {
                    switch (keys[2]) {
                        /* ---------------------
                         * insert, delete and page keys
                         * ---------------------
                         */
                        case '2':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_INSERT, modifier);
                        case '3':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_DELETE, modifier);
                        case '5':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_PAGE_UP, modifier);
                        case '6':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_PAGE_DOWN, modifier);
                    }
                }
            } else if (keys.length == 7 && keys[4] == ';' && keys[6] == '~') {
                int modifier = convertModifierByte(keys[5]);

                if (keys[2] == '1') {
                    switch (keys[3]) {
                        /* ---------------------
                         * F5, F6, F7, F8
                         * ---------------------
                         */
                        case '5':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F5, modifier);
                        case '7':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F6, modifier);
                        case '8':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F7, modifier);
                        case '9':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F8, modifier);
                    }
                } else if (keys[2] == '2') {
                    switch (keys[3]) {
                        /* ---------------------
                         * F9, F10, F11, F12
                         * ---------------------
                         */
                        case '0':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F9, modifier);
                        case '1':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F10, modifier);
                        case '3':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F11, modifier);
                        case '4':
                            return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F12, modifier);
                    }
                }
            }
        } else if (keys[1] == 'O') {
            if (keys.length == 3) {
                switch (keys[2]) {
                    /* ---------------------
                     * normal mode cursor, pos1 and end keys
                     * ---------------------
                     */
                    case 'A':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_UP);
                    case 'B':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_DOWN);
                    case 'C':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_RIGHT);
                    case 'D':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_LEFT);
                    case 'H':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_POS1);
                    case 'F':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_END);

                    /* ---------------------
                     * F1, F2, F3, F4
                     * ---------------------
                     */
                    case 'P':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F1);
                    case 'Q':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F2);
                    case 'R':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F3);
                    case 'S':
                        return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, TerminalKey.TK_F4);
                }
            }
        }

        return null;
    }

    private int convertModifierByte(byte mod) {
        switch (mod) {
            case '2':
                return TerminalKey.TK_MODIFIER_SHIFT;
            case '3':
                return TerminalKey.TK_MODIFIER_ALT;
            case '4':
                return TerminalKey.TK_MODIFIER_SHIFT | TerminalKey.TK_MODIFIER_ALT;
            case '5':
                return TerminalKey.TK_MODIFIER_CTRL;
            case '6':
                return TerminalKey.TK_MODIFIER_CTRL | TerminalKey.TK_MODIFIER_SHIFT;
            case '7':
                return TerminalKey.TK_MODIFIER_CTRL | TerminalKey.TK_MODIFIER_ALT;
            case '8':
                return TerminalKey.TK_MODIFIER_CTRL | TerminalKey.TK_MODIFIER_SHIFT | TerminalKey.TK_MODIFIER_ALT;
            default:
                return 0;
        }
    }
}
