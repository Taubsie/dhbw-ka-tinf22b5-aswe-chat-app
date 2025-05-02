package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.layout.Layoutable;

import java.awt.*;

public abstract class TerminalRenderable implements Layoutable {

    protected Dimension size;
    protected Point startPoint;

    protected boolean visible = true;

    public abstract void layout();
    public abstract void render(TerminalScreen terminalScreen);

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public void setSize(Dimension size) {
        this.size = size;
    }

    @Override
    public Point getStartPoint() {
        return startPoint;
    }

    @Override
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
