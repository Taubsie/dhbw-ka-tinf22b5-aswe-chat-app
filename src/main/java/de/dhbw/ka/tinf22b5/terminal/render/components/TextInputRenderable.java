package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyType;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.characters.BlinkingTerminalCharacter;
import de.dhbw.ka.tinf22b5.terminal.render.characters.PlainTerminalCharacter;
import de.dhbw.ka.tinf22b5.terminal.render.characters.ReversedTerminalCharacter;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacter;

import java.awt.*;

public class TextInputRenderable extends TerminalRenderable implements Interactable {

    private final StringBuilder text;
    private boolean holdsFocus = false;

    public TextInputRenderable(String text) {
        this.text = new StringBuilder(text);
    }

    public String getText() {
        return text.toString();
    }

    public void clearText() {
        text.setLength(0);
    }

    public boolean handleInput(TerminalKeyEvent event) {
        if (!holdsFocus)
            return false;

        switch (event.getKeyType()) {
            case TerminalKeyType.TKT_SPECIAL_KEY:
                switch (event.getTerminalKey()) {
                    case TerminalKey.TK_BACKSPACE:
                        if (!text.isEmpty())
                            text.deleteCharAt(text.length() - 1);
                        return true;
                    case TerminalKey.TK_CTRL_BACKSPACE:
                        text.setLength(0);
                        return true;
                }
                break;
            case TKT_ASCII:
            case TKT_UNICODE:
            case TKT_UNICODE_STRING:
                text.append(event.getKeyCharacter());
                return true;
        }

        return false;
    }

    @Override
    public void layout() {
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        if (!isVisible())
            return;

        terminalScreen.setCursorPosition(startPoint.x, startPoint.y);

        if (size.width >= text.length() + 1) {
            terminalScreen.addString(text.toString());
        } else {
            int substringWidth = size.width - 4;
            terminalScreen.addString("...");
            if (substringWidth > 0) {
                terminalScreen.addString(text.substring(text.length() - substringWidth));
            }
        }

        if (holdsFocus) {
            TerminalCharacter cursorIndicator = new PlainTerminalCharacter("_");
            cursorIndicator = new ReversedTerminalCharacter(cursorIndicator);
            cursorIndicator = new BlinkingTerminalCharacter(cursorIndicator);
            terminalScreen.setCharacter(cursorIndicator);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(20, 1);
    }

    // base width
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(-1, 1);
    }

    @Override
    public void setFocus(boolean focus) {
        this.holdsFocus = focus;
    }
}