package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.layout.LayoutManager;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerRenderable extends TerminalRenderable {

    protected final List<TerminalRenderable> components;
    protected LayoutManager layoutManager;

    public ContainerRenderable() {
        this.components = new ArrayList<>();
    }

    @Override
    public void layout() {
        if (layoutManager != null) {
            layoutManager.layoutContainer(this);
        }
    }

    public void addComponent(TerminalRenderable component) {
        this.components.add(component);
    }

    public void removeComponent(TerminalRenderable component) {
        this.components.remove(component);
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        if (!isVisible())
            return;

        components.forEach(c -> c.render(terminalScreen));
    }

    public TerminalRenderable[] getComponents() {
        return components.toArray(new TerminalRenderable[0]);
    }
}
