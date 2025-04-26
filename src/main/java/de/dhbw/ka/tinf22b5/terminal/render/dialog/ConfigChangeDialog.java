package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.handler.BaseTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyType;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacterFactory;

import java.io.IOException;
import java.util.Optional;

public class ConfigChangeDialog extends Dialog {
    private final ConfigurationRepository repository;
    private final ConfigurationKey configOption;
    private String newValue = "";

    public ConfigChangeDialog(ConfigurationRepository repository, ConfigurationKey configOption) {
        this.repository = repository;
        this.configOption = configOption;
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        terminalScreen.setCursorPosition(0, 0);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString(newValue));

        terminalScreen.setCursorPosition(0, 2);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("Changing: " + configOption.getDisplayName()));

        terminalScreen.setCursorPosition(0, 3);
        Optional<String> value = repository.getConfigurationValue(configOption);
        if(value.isPresent()) {
            terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("Old value: " + value.get()));
        } else {
            terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("No value set"));
        }

        terminalScreen.setCursorPosition(0, 4);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("Enter - Save value | Esc / STRG+Q - Discard changes"));
    }

    @Override
    public void handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
        if(event.getKeyType() == TerminalKeyType.TKT_ASCII || event.getKeyType() == TerminalKeyType.TKT_UNICODE) {
            // TODO: extract bounds checking to rendering class
            if(newValue.length() >= ((BaseTerminalHandler)terminal).getSize().width) {
                return;
            }

            newValue += event.getKeyCharacter();
            terminal.moveCursor(CursorDirection.RIGHT);
        }

        switch (event.getTerminalKey()) {
            case TerminalKey.TK_BACKSPACE:
                newValue = newValue.substring(0, Math.max(newValue.length() - 1, 0));
                terminal.moveCursor(CursorDirection.LEFT);
                break;

            case TerminalKey.TK_ENTER:
                if(!newValue.isBlank()) {
                    repository.setConfigurationValue(configOption, newValue);
                } else {
                    repository.removeConfigurationValue(configOption);
                }
            case TerminalKey.TK_CTRL_Q:
            case TerminalKey.TK_ESCAPE:
                terminal.changeDialog(new ConfigDialog());
        }
    }
}
