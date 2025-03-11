package de.dhbw.ka.tinf22b5.terminal;

import java.nio.charset.StandardCharsets;

public class TerminalKeyParser {

    public TerminalKeyEvent parseTerminalKeyInput(byte[] keys) {
        /* ---------------------
         * on wrong data return valid event to prevent crashes
         * ---------------------
         */
        if(keys == null || keys.length == 0)
            return new TerminalKeyEvent();

        /* ---------------------
         * single key probably valid ascii char
         * ---------------------
         */
        if(keys.length == 1)
            return parseSingleTerminalKeyInput(keys);

        /* ---------------------
         * terminal escape chars start with 27
         * ---------------------
         */
        if(keys[0] == 0x1b) {
            return new TerminalKeyEvent();
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

    private TerminalKeyEvent parseSingleTerminalKeyInput(byte[] keys) {
        switch (keys[0]) {
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

            /* ---------------------
             * <-- ascii key for forward deletion
             * ---------------------
             */
            case TerminalKey.TK_BACKSPACE:
                return new TerminalKeyEvent(keys, TerminalKeyType.TKT_SPECIAL_KEY, keys[0]);

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
                return new TerminalKeyEvent(keys, TerminalKeyType.TKT_ASCII, keys[0]);

            /* ---------------------
             * unknown / undefined char
             * ---------------------
             */
            default:
                return new TerminalKeyEvent(keys, TerminalKeyType.TKT_UNKNOWN, TerminalKey.TK_UNKNOWN);
        }
    }
}
