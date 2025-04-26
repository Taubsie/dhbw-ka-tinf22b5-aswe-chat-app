package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.ConstSingleLineStringRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.components.TextInputRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class ConfigChangeDialog extends Dialog {
    private final ConfigurationRepository repository;
    private final ConfigurationKey configOption;

    private final TextInputRenderable textInput;

    public ConfigChangeDialog(ConfigurationRepository repository, ConfigurationKey configOption) {
        this.repository = repository;
        this.configOption = configOption;

        this.layoutManager = new ListLayout(true);
        this.textInput = new TextInputRenderable("");
        this.addComponent(textInput);

        // empty line
        this.addComponent(new ConstSingleLineStringRenderable(""));
        this.addComponent(new ConstSingleLineStringRenderable("Changing: " + configOption.getDisplayName()));


        Optional<String> value = repository.getConfigurationValue(configOption);
        if (value.isPresent()) {
            this.addComponent(new ConstSingleLineStringRenderable("Old value: " + value.get()));
        } else {
            this.addComponent(new ConstSingleLineStringRenderable("No value set"));
        }

        this.addComponent(new ConstSingleLineStringRenderable("Enter - Save value | Esc / STRG+Q - Discard changes"));
    }

    @Override
    public void handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
            switch (event.getTerminalKey()) {
                case TerminalKey.TK_ENTER:
                    if (!textInput.getText().isBlank()) {
                        repository.setConfigurationValue(configOption, textInput.getText());
                    } else {
                        repository.removeConfigurationValue(configOption);
                    }
                case TerminalKey.TK_CTRL_Q:
                case TerminalKey.TK_ESCAPE:
                    terminal.changeDialog(new ConfigDialog());
                    return;
            }

            textInput.handleInput(event);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }
}
