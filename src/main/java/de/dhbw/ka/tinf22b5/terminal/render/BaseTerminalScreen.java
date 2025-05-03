package de.dhbw.ka.tinf22b5.terminal.render;

import de.dhbw.ka.tinf22b5.terminal.render.characters.DecoratedCharacterFactory;
import de.dhbw.ka.tinf22b5.terminal.render.characters.PlainTerminalCharacter;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacter;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacterFactory;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BaseTerminalScreen implements TerminalScreen {

    private int width;
    private int height;
    private TerminalCharacter[] characters;
    private final Queue<DecoratedCharacterFactory> characterModifiers;

    private int cursorX;
    private int cursorY;

    public BaseTerminalScreen() {
        characterModifiers = new LinkedList<>();
    }

    @Override
    public void doResize(Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;

        clear();

        this.cursorX = 0;
        this.cursorY = 0;
    }

    @Override
    public void setCursorPosition(int x, int y) {
        this.cursorX = x;
        this.cursorY = y;
    }

    @Override
    public TerminalScreen setCharacter(TerminalCharacter character) {
        if (cursorX < 0 || cursorX >= width || cursorY < 0 || cursorY >= height)
            return this;

        for (DecoratedCharacterFactory factory : characterModifiers) {
            character = DecoratedCharacterFactory.createWrappedTerminalCharacter(character, factory);
        }

        characters[cursorY * this.width + cursorX] = character;
        // dont test, its done above and prevents overwriting of the same char
        cursorX++;

        return this;
    }

    @Override
    public TerminalScreen setCharacters(TerminalCharacter[] characters) {
        for (TerminalCharacter character : characters) {
            setCharacter(character);
        }

        return this;
    }

    @Override
    public TerminalScreen addString(String s) {
        setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString(s));
        return this;
    }

    @Override
    public TerminalScreen pushCharacterModifier(DecoratedCharacterFactory fac) {
        characterModifiers.add(fac);
        return this;
    }

    @Override
    public TerminalScreen popCharacterModifier() {
        characterModifiers.remove();
        return this;
    }

    @Override
    public void clear() {
        this.characters = new TerminalCharacter[this.width * this.height];
        Arrays.fill(this.characters, new PlainTerminalCharacter(" "));

        this.cursorX = 0;
        this.cursorY = 0;

        characterModifiers.clear();
    }

    @Override
    public TerminalCharacter[] getLine(int idx) {
        if(idx < 0 || idx >= this.height)
            return new PlainTerminalCharacter[0];

        return Arrays.copyOfRange(this.characters, idx * this.width, (idx + 1) * this.width);
    }

    @Override
    public void renderIntoBuffer(TerminalRenderingBuffer buffer) {
        for (int line = 0; line < this.height; line++) {

            for (int column = 0; column < this.width; column++) {
                characters[line * this.width + column].render(buffer);
            }

            if (line != this.height - 1)
                buffer.nextLine();
        }
    }
}
