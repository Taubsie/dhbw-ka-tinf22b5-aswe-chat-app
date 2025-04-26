package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.FileConfigurationRepository;
import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacterFactory;

import java.io.IOException;
import java.util.Optional;

public class ConfigDialog extends Dialog {
    private final ConfigurationRepository repository;

    public ConfigDialog() {
        repository = new FileConfigurationRepository();
    }

    @Override
    public void render(TerminalScreen terminalScreen) {

        terminalScreen.setCursorPosition(0, 0);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("Configuration"));

        terminalScreen.setCursorPosition(0, 1);
        int line = 1;
        for (ConfigurationKey configurationKey : ConfigurationKey.values()) {
            terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("- " + configurationKey.getDisplayName() + " -"));
            terminalScreen.setCursorPosition(0, ++line);
            Optional<String> value = repository.getConfigurationValue(configurationKey);
            terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString(value.orElse("No value set")));
            terminalScreen.setCursorPosition(0, ++line);
        }
    }

    @Override
    public void handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_UP, TerminalKey.TK_W, TerminalKey.TK_w -> {
                terminal.moveCursor(CursorDirection.UP);
                if (terminal.getCursorY() % 2 == 0) {
                    terminal.moveCursor(CursorDirection.UP);
                }
            }
            case TerminalKey.TK_DOWN, TerminalKey.TK_S, TerminalKey.TK_s -> {
                terminal.moveCursor(CursorDirection.DOWN);
                if (terminal.getCursorY() % 2 == 0) {
                    terminal.moveCursor(CursorDirection.DOWN);
                }
            }
            case TerminalKey.TK_RIGHT, TerminalKey.TK_D, TerminalKey.TK_d, TerminalKey.TK_LEFT, TerminalKey.TK_A,
                 TerminalKey.TK_a -> {
            }
            case TerminalKey.TK_ENTER -> {
                if (terminal.getCursorY() < 3) {
                    break;
                }

                // 3 -> 0; 5 -> 1; 7 -> 2; 9 -> 3
                Optional<ConfigurationKey> config = ConfigurationKey.getByOrdinal(((terminal.getCursorY() - 1) / 2) - 1);

                if (config.isPresent()) {
                    terminal.changeDialog(new ConfigChangeDialog(repository, config.get()));
                }
            }
            case TerminalKey.TK_r, TerminalKey.TK_R -> repository.loadConfiguration();
            default -> super.handleInput(terminal, event);
        }
    }
}