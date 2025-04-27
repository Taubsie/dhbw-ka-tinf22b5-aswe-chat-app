package de.dhbw.ka.tinf22b5.terminal.render.characters;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

public class BlinkingTerminalCharacter extends DecoratedCharacter {

    public BlinkingTerminalCharacter(TerminalCharacter decoratee) {
        super(decoratee);
    }

    @Override
    public void render(TerminalRenderingBuffer buffer) {
        buffer.addBytes(0x1b, '[', '5', 'm');
        decoratee.render(buffer);
        buffer.addBytes(0x1b, '[', '2', '5', 'm');
    }
}
