package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.FileConfigurationRepository;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.components.ConstSingleLineStringRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.components.ContainerRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.components.ListRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.layout.ListLayout;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class ConfigDialog extends Dialog {
    private final ConfigurationRepository repository;

    private final ListRenderable<ConfigListItem> configList;

    public ConfigDialog() {
        repository = new FileConfigurationRepository();

        this.layoutManager = new ListLayout(true);

        this.addComponent(new ConstSingleLineStringRenderable("Configuration"));
        this.addComponent(new ConstSingleLineStringRenderable(""));

        this.configList = new ListRenderable<>();
        this.configList.setFocus(true);
        this.addComponent(configList);

        for (ConfigurationKey configurationKey : ConfigurationKey.values()) {
            Optional<String> value = repository.getConfigurationValue(configurationKey);
            this.configList.addItem(new ConfigListItem(configurationKey, value.orElse("No value set")));
        }
    }

    @Override
    public void layout() {
        updateConfigs();
        super.layout();
    }

    private void updateConfigs() {
        for (int i = 0; i < ConfigurationKey.values().length; i++) {
            this.configList.getItem(i).valueText.setText(repository.getConfigurationValue(ConfigurationKey.values()[i]).orElse("No value set"));
        }
    }

    @Override
    public boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
        if (this.configList.handleInput(event))
            return true;

        switch (event.getTerminalKey()) {
            case TerminalKey.TK_ENTER:
                Optional<ConfigurationKey> config = ConfigurationKey.getByOrdinal(this.configList.getSelectedIdx());

                if (config.isPresent()) {
                    terminal.pushDialog(new ConfigChangeDialog(repository, config.get()));
                }

                return true;
            case TerminalKey.TK_ESCAPE:
                terminal.popDialog();
                return true;
        }

        return false;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(0, 0);
    }

    private static class ConfigListItem extends ContainerRenderable {

        final ConstSingleLineStringRenderable keyText;
        final ConstSingleLineStringRenderable valueText;

        ConfigListItem(ConfigurationKey key, String value) {
            this.layoutManager = new ListLayout(true);

            this.keyText = new ConstSingleLineStringRenderable("-" + key.getDisplayName() + "-");
            this.addComponent(this.keyText);
            this.valueText = new ConstSingleLineStringRenderable(value);
            this.addComponent(this.valueText);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Math.max(this.keyText.getPreferredSize().width, this.valueText.getPreferredSize().width), 2);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(Math.max(this.keyText.getMinimumSize().width, this.valueText.getMinimumSize().width), 2);
        }
    }
}