package de.dhbw.ka.tinf22b5.terminal.render.characters;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

public class ReversedTerminalCharacter extends DecoratedCharacter {

    public ReversedTerminalCharacter(TerminalCharacter decoratee) {
        super(decoratee);
    }

    @Override
    public void render(TerminalRenderingBuffer buffer) {
        buffer.addBytes(0x1b, '[', '7', 'm');
        decoratee.render(buffer);
        buffer.addBytes(0x1b, '[', '2', '7', 'm');
    }
}
