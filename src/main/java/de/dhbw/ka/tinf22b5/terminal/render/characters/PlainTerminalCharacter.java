package de.dhbw.ka.tinf22b5.terminal.render.characters;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

public class PlainTerminalCharacter implements TerminalCharacter {

    private final String character;

    public PlainTerminalCharacter(String character) {
        this.character = getValidChar(character);
    }

    private String getValidChar(String character) {
        if (character == null || character.isEmpty())
            return " ";

        try {
            return Character.toString(character.codePointAt(0));
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return " ";
        }
    }

    @Override
    public void render(TerminalRenderingBuffer buffer) {
        buffer.addString(character);
    }
}
