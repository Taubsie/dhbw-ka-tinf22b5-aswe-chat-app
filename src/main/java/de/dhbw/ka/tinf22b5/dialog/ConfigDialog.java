package de.dhbw.ka.tinf22b5.dialog;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.FileConfigurationRepository;
import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

import java.io.IOException;
import java.util.Optional;

public class ConfigDialog extends Dialog {
    private final ConfigurationRepository repository;

    public ConfigDialog() {
        repository = new FileConfigurationRepository();
    }

    @Override
    public void render(TerminalRenderingBuffer terminalRenderingBuffer) {
        terminalRenderingBuffer.setCursorVisible(true);
        terminalRenderingBuffer.addString("Configuration");
        terminalRenderingBuffer.nextLine();
        for (ConfigurationKey configurationKey : ConfigurationKey.values()) {
            terminalRenderingBuffer.addString("- " + configurationKey.getDisplayName() + " -");
            terminalRenderingBuffer.nextLine();
            Optional<String> value = repository.getConfigurationValue(configurationKey);
            terminalRenderingBuffer.addString(value.orElse("No value set"));
            terminalRenderingBuffer.nextLine();
        }
    }

    @Override
    public void handleInput(TerminalKeyEvent event, TerminalHandler terminal) throws IOException {
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
            default -> super.handleInput(event, terminal);
        }
    }
}