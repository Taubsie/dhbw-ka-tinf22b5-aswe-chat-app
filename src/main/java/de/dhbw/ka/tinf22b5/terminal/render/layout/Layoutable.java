package de.dhbw.ka.tinf22b5.terminal.render.layout;

import java.awt.*;

public interface Layoutable {

    Dimension getPreferredSize();
    Dimension getMinimumSize();

    void setSize(Dimension size);
    Dimension getSize();

    void setStartPoint(Point startPoint);
    Point getStartPoint();
}
