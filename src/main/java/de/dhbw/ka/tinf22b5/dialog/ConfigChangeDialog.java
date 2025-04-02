package de.dhbw.ka.tinf22b5.dialog;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyType;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

import java.io.IOException;

public class ConfigChangeDialog extends Dialog {
    private final ConfigurationRepository repository;
    private final ConfigurationKey configOption;
    private String newValue = "";

    public ConfigChangeDialog(ConfigurationRepository repository, ConfigurationKey configOption) {
        this.repository = repository;
        this.configOption = configOption;
    }

    @Override
    public void render(TerminalRenderingBuffer terminalRenderingBuffer) {
        terminalRenderingBuffer.setCursurVisible(true);
        terminalRenderingBuffer.addString(newValue);
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.addString("Changing: " + configOption.getDisplayName());
        terminalRenderingBuffer.nextLine();
        String value = repository.getConfigurationValue(configOption);
        if(value != null) {
            terminalRenderingBuffer.addString("Old value: " + value);
        } else {
            terminalRenderingBuffer.addString("No value set");
        }
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.addString("Enter - Save value | Esc / STRG+Q - Discard changes");
        terminalRenderingBuffer.nextLine();
    }

    @Override
    public void handleInput(TerminalKeyEvent event, TerminalHandler terminal) throws IOException {
        if(event.getKeyType() == TerminalKeyType.TKT_ASCII || event.getKeyType() == TerminalKeyType.TKT_UNICODE) {
            if(newValue.length() >= terminal.getSize().width) {
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
                }
            case TerminalKey.TK_CTRL_Q:
            case TerminalKey.TK_ESCAPE:
                terminal.changeDialog(new ConfigDialog());
        }
    }
}
