package de.dhbw.ka.tinf22b5.terminal;

public class TerminalKey {

    /* ---------------------
     * invalid / unknown key
     * ---------------------
     */
    public static final int TK_UNDEFINED          = 0xffffffff;

    /* ---------------------
     * ascii control chars
     * ---------------------
     */
    public static final int TK_TAB                = 0x09;
    public static final int TK_ENTER              = 0x0d;
    public static final int TK_ESCAPE             = 0x1b;

    /* ---------------------
     * ctrl + letter
     * ---------------------
     */
    public static final int TK_CTRL_SPACE         = 0x00;
    public static final int TK_CTRL_A             = 0x01;
    public static final int TK_CTRL_B             = 0x02;
    public static final int TK_CTRL_C             = 0x03;
    public static final int TK_CTRL_D             = 0x04;
    public static final int TK_CTRL_E             = 0x05;
    public static final int TK_CTRL_F             = 0x06;
    public static final int TK_CTRL_G             = 0x07;
    public static final int TK_CTRL_H             = 0x08;
    public static final int TK_CTRL_I             = 0x09;
    public static final int TK_CTRL_J             = 0x0a;
    public static final int TK_CTRL_K             = 0x0b;
    public static final int TK_CTRL_L             = 0x0c;
    public static final int TK_CTRL_M             = 0x0d;
    public static final int TK_CTRL_N             = 0x0e;
    public static final int TK_CTRL_O             = 0x0f;
    public static final int TK_CTRL_P             = 0x10;
    public static final int TK_CTRL_Q             = 0x11;
    public static final int TK_CTRL_R             = 0x12;
    public static final int TK_CTRL_S             = 0x13;
    public static final int TK_CTRL_T             = 0x14;
    public static final int TK_CTRL_U             = 0x15;
    public static final int TK_CTRL_V             = 0x16;
    public static final int TK_CTRL_W             = 0x17;
    public static final int TK_CTRL_X             = 0x18;
    public static final int TK_CTRL_Y             = 0x19;
    public static final int TK_CTRL_Z             = 0x1a;
    public static final int TK_CTRL_LEFT_BRACKET  = 0x1b;
    public static final int TK_CTRL_BACKSLASH     = 0x1c;
    public static final int TK_CTRL_RIGHT_BRACKET = 0x1d;

    /* ---------------------
     * ascii special chars
     * ---------------------
     */
    public static final int TK_SPACE              = 0x20;
    public static final int TK_EXCLAMATION_MARK   = 0x21;
    public static final int TK_DOUBLE_QUOTE       = 0x22;
    public static final int TK_HASH_TAG           = 0x23;
    public static final int TK_DOLLAR             = 0x24;
    public static final int TK_PERCENT            = 0x25;
    public static final int TK_AMPERSAND          = 0x26;
    public static final int TK_APOSTROPHE         = 0x27;
    public static final int TK_LEFT_PARENTHESES   = 0x28;
    public static final int TK_RIGHT_PARENTHESES  = 0x29;
    public static final int TK_ASTERISK           = 0x2a;
    public static final int TK_PLUS               = 0x2b;
    public static final int TK_COMMA              = 0x2c;
    public static final int TK_MINUS              = 0x2d;
    public static final int TK_PERIOD             = 0x2e;
    public static final int TK_SLASH              = 0x2f;

    /* ---------------------
     * numbers
     * ---------------------
     */
    public static final int TK_0                  = 0x30;
    public static final int TK_1                  = 0x31;
    public static final int TK_2                  = 0x32;
    public static final int TK_3                  = 0x33;
    public static final int TK_4                  = 0x34;
    public static final int TK_5                  = 0x35;
    public static final int TK_6                  = 0x36;
    public static final int TK_7                  = 0x37;
    public static final int TK_8                  = 0x38;
    public static final int TK_9                  = 0x39;

    /* ---------------------
     * ascii operators special chars
     * ---------------------
     */
    public static final int TK_COLON              = 0x3a;
    public static final int TK_SEMICOLON          = 0x3b;
    public static final int TK_LESS               = 0x3c;
    public static final int TK_EQUALS             = 0x3d;
    public static final int TK_GREATER            = 0x3e;
    public static final int TK_QUESTION_MARK      = 0x3f;
    public static final int TK_AT                 = 0x40;

    /* ---------------------
     * upper case letters
     * ---------------------
     */
    public static final int TK_A                  = 0x41;
    public static final int TK_B                  = 0x42;
    public static final int TK_C                  = 0x43;
    public static final int TK_D                  = 0x44;
    public static final int TK_E                  = 0x45;
    public static final int TK_F                  = 0x46;
    public static final int TK_G                  = 0x47;
    public static final int TK_H                  = 0x48;
    public static final int TK_I                  = 0x49;
    public static final int TK_J                  = 0x4a;
    public static final int TK_K                  = 0x4b;
    public static final int TK_L                  = 0x4c;
    public static final int TK_M                  = 0x4d;
    public static final int TK_N                  = 0x4e;
    public static final int TK_O                  = 0x4f;
    public static final int TK_P                  = 0x50;
    public static final int TK_Q                  = 0x51;
    public static final int TK_R                  = 0x52;
    public static final int TK_S                  = 0x53;
    public static final int TK_T                  = 0x54;
    public static final int TK_U                  = 0x55;
    public static final int TK_V                  = 0x56;
    public static final int TK_W                  = 0x57;
    public static final int TK_X                  = 0x58;
    public static final int TK_Y                  = 0x59;
    public static final int TK_Z                  = 0x5a;

    /* ---------------------
     * ascii special chars
     * ---------------------
     */
    public static final int TK_LEFT_BRACKET       = 0x5b;
    public static final int TK_BACKSLASH          = 0x5c;
    public static final int TK_RIGHT_BRACKET      = 0x5d;
    public static final int TK_CARET              = 0x5e;
    public static final int TK_UNDER_SCORE        = 0x5f;
    public static final int TK_BACKTICK           = 0x60;

    /* ---------------------
     * lower case letters
     * ---------------------
     */
    public static final int TK_a                  = 0x61;
    public static final int TK_b                  = 0x62;
    public static final int TK_c                  = 0x63;
    public static final int TK_d                  = 0x64;
    public static final int TK_e                  = 0x65;
    public static final int TK_f                  = 0x66;
    public static final int TK_g                  = 0x67;
    public static final int TK_h                  = 0x68;
    public static final int TK_i                  = 0x69;
    public static final int TK_j                  = 0x6a;
    public static final int TK_k                  = 0x6b;
    public static final int TK_l                  = 0x6c;
    public static final int TK_m                  = 0x6d;
    public static final int TK_n                  = 0x6e;
    public static final int TK_o                  = 0x6f;
    public static final int TK_p                  = 0x70;
    public static final int TK_q                  = 0x71;
    public static final int TK_r                  = 0x72;
    public static final int TK_s                  = 0x73;
    public static final int TK_t                  = 0x74;
    public static final int TK_u                  = 0x75;
    public static final int TK_v                  = 0x76;
    public static final int TK_w                  = 0x77;
    public static final int TK_x                  = 0x78;
    public static final int TK_y                  = 0x79;
    public static final int TK_z                  = 0x7a;

    /* ---------------------
     * ascii special chars
     * ---------------------
     */
    public static final int TK_LEFT_CURLY_BRACE   = 0x7b;
    public static final int TK_PIPE               = 0x7c;
    public static final int TK_RIGHT_CURLY_BRACE  = 0x7d;
    public static final int TK_TILDE              = 0x7e;

    /* ---------------------
     * <-- ascii key for forward deletion
     * ---------------------
     */
    public static final int TK_BACKSPACE          = 0x7f;

    /* ---------------------
     * special control keys
     * ---------------------
     */
    public static final int TK_INSERT             = 0x100100;
    public static final int TK_DELETE             = 0x100200;
    public static final int TK_POS1               = 0x100300;
    public static final int TK_END                = 0x100400;
    public static final int TK_PAGE_UP            = 0x100500;
    public static final int TK_PAGE_DOWN          = 0x100600;

    /* ---------------------
     * arrow keys keys
     * ---------------------
     */
    public static final int TK_UP                 = 0x110100;
    public static final int TK_DOWN               = 0x110200;
    public static final int TK_RIGHT              = 0x110300;
    public static final int TK_LEFT               = 0x110400;

    /* ---------------------
     * f keys
     * ---------------------
     */
    public static final int TK_F1                 = 0xf00100;
    public static final int TK_F2                 = 0xf00200;
    public static final int TK_F3                 = 0xf00300;
    public static final int TK_F4                 = 0xf00400;
    public static final int TK_F5                 = 0xf00500;
    public static final int TK_F6                 = 0xf00600;
    public static final int TK_F7                 = 0xf00700;
    public static final int TK_F8                 = 0xf00800;
    public static final int TK_F9                 = 0xf00900;
    public static final int TK_F10                = 0xf00a00;
    public static final int TK_F11                = 0xf00b00;
    public static final int TK_F12                = 0xf00c00;
    public static final int TK_F13                = 0xf00d00;
    public static final int TK_F14                = 0xf00e00;
    public static final int TK_F15                = 0xf00f00;
    public static final int TK_F16                = 0xf01000;
    public static final int TK_F17                = 0xf01100;
    public static final int TK_F18                = 0xf01200;
    public static final int TK_F19                = 0xf01300;
    public static final int TK_F20                = 0xf01400;
    public static final int TK_F21                = 0xf01500;
    public static final int TK_F22                = 0xf01600;
    public static final int TK_F23                = 0xf01700;
    public static final int TK_F24                = 0xf01800;

}
