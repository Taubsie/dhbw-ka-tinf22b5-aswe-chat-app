package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.render.layout.LayoutManager;

import java.awt.*;

public class EmptyPanel extends ContainerRenderable {

    public EmptyPanel(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(-1, -1);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(-1, -1);
    }
}
