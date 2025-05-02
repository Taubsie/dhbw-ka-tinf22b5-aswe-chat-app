package de.dhbw.ka.tinf22b5.terminal.render.characters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum DecoratedCharacterFactory {

    REVERSED(ReversedTerminalCharacter.class),
    BLINKING(BlinkingTerminalCharacter.class);

    private final Class<? extends DecoratedCharacter> characterClass;

    DecoratedCharacterFactory(Class<? extends DecoratedCharacter> characterClass) {
        this.characterClass = characterClass;
    }

    public static TerminalCharacter createWrappedTerminalCharacter(TerminalCharacter toWrap, DecoratedCharacterFactory wrapper) {

        try {
            Constructor<? extends DecoratedCharacter> constructor = wrapper.characterClass.getConstructor(TerminalCharacter.class);
            return constructor.newInstance(toWrap);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return toWrap;
        }
    }
}
