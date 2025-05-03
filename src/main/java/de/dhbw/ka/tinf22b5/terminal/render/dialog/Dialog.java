package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.ContainerRenderable;

import java.io.IOException;

public abstract class Dialog extends ContainerRenderable {

    public abstract boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException;
}
