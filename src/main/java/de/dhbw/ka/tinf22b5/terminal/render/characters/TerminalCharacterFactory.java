package de.dhbw.ka.tinf22b5.terminal.render.characters;

public class TerminalCharacterFactory {

    public static <T extends TerminalCharacter> TerminalCharacter[] createTerminalCharactersFromString(String str) {
        if (str == null)
            return new TerminalCharacter[0];

        return str.codePoints()
                .mapToObj(Character::toString)
                .map(PlainTerminalCharacter::new)
                .toArray(TerminalCharacter[]::new);
    }
}
