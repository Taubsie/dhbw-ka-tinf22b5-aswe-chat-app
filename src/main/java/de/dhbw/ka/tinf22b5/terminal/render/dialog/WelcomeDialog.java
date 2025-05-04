package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.BorderRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.components.ConstSingleLineStringRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;

import java.awt.*;
import java.io.IOException;

public class WelcomeDialog extends Dialog {

    public WelcomeDialog() {
        this.layoutManager = new ListLayout(false);

        this.addComponent(new ConstSingleLineStringRenderable("Welcome to the Chat App!"));
        this.addComponent(new BorderRenderable(
                new ConstSingleLineStringRenderable("Press any of the following:"),
                BorderRenderable.BorderStyle.EMPTY, 1, BorderRenderable.BORDER_TOP));
        this.addComponent(new ConstSingleLineStringRenderable("E - Start | C - Config | Q - Quit"));
    }

    @Override
    public boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_C, TerminalKey.TK_c:
                terminal.pushDialog(new ConfigDialog());
                return true;
            case TerminalKey.TK_E, TerminalKey.TK_e:
                terminal.pushDialog(new ChatDialog(terminal));
                return true;
        }

        return false;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}