package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyListener;
import de.dhbw.ka.tinf22b5.terminal.render.components.ContainerRenderable;

import java.io.IOException;

public abstract class Dialog extends ContainerRenderable implements TerminalKeyListener {

    public abstract boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException;

    @Override
    public final void keyPressed(TerminalHandler terminal, TerminalKeyEvent terminalKeyEvent) throws IOException {
        handleInput(terminal, terminalKeyEvent);
    }
}
