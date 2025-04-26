package de.dhbw.ka.tinf22b5.terminal.render;

import de.dhbw.ka.tinf22b5.terminal.render.layout.Layoutable;

import java.awt.*;

public abstract class TerminalRenderable implements Layoutable {

    public abstract void render(TerminalScreen terminalScreen);

    @Override
    public Dimension getPreferredSize() {
        return null;
    }

    @Override
    public Dimension getMinimumSize() {
        return null;
    }

    @Override
    public void setSize(Dimension size) {

    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public void setStartPoint(Point startPoint) {

    }

    @Override
    public Point getStartPoint() {
        return null;
    }
}
