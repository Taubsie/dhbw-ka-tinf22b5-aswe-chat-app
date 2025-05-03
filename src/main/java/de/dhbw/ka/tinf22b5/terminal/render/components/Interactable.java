package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;

public interface Interactable extends Focusable {

    boolean handleInput(TerminalKeyEvent event);
}
