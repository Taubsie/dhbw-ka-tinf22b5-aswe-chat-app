package de.dhbw.ka.tinf22b5.terminal.render.characters;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

public class BlinkingTerminalCharacter implements TerminalCharacter {

    private TerminalCharacter decoratee;

    public BlinkingTerminalCharacter(TerminalCharacter decoratee) {
        this.decoratee = decoratee;
    }

    @Override
    public void render(TerminalRenderingBuffer buffer) {
        buffer.addBytes(0x1b, '[', '5', 'm');
        decoratee.render(buffer);
        buffer.addBytes(0x1b, '[', '2', '5', 'm');
    }
}
