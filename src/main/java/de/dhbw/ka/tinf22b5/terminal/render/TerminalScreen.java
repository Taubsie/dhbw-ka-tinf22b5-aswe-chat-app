package de.dhbw.ka.tinf22b5.terminal.render;

import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.render.characters.DecoratedCharacterFactory;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacter;

import java.awt.*;

public interface TerminalScreen {

    void doResize(Dimension dimension);

    void setCursorPosition(int x, int y);
    void moveCursor(CursorDirection direction, int amount);

    TerminalScreen setCharacter(TerminalCharacter character);
    TerminalScreen setCharacters(TerminalCharacter[] characters);
    TerminalScreen addString(String s);

    TerminalScreen pushCharacterModifier(DecoratedCharacterFactory fac);
    TerminalScreen popCharacterModifier();

    void clear();
    TerminalCharacter[] getLine(int idx);
    void renderIntoBuffer(TerminalRenderingBuffer buffer);
}
