package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;

import java.awt.*;

public class ConstSingleLineStringRenderable extends TerminalRenderable {

    private final String string;

    public ConstSingleLineStringRenderable(String string) {
        this.string = string;
    }

    @Override
    public void layout() {
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        if(!isVisible())
            return;

        terminalScreen.setCursorPosition(startPoint.x, startPoint.y);

        if (size != null && size.width < string.length()) {
            int substringWidth = size.width - 3;
            if (substringWidth > 0)
                terminalScreen.addString(string.substring(0, substringWidth));
            terminalScreen.addString("...");
        } else {
            terminalScreen.addString(string);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(string.codePointCount(0, string.length()), 1);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(string.codePointCount(0, string.length()), 1);
    }
}
