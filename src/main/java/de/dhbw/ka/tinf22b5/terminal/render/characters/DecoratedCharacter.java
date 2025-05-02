package de.dhbw.ka.tinf22b5.terminal.render.characters;

public abstract class DecoratedCharacter implements TerminalCharacter {

    protected final TerminalCharacter decoratee;

    public DecoratedCharacter(TerminalCharacter decoratee) {
        this.decoratee = decoratee;
    }

}
