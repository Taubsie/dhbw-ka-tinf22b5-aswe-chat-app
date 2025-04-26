package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;

import java.awt.*;

public class BorderRenderable extends TerminalRenderable {

    public enum BorderStyle {
        EMPTY
    }

    public static final int BORDER_TOP = 1;
    public static final int BORDER_BOTTOM = 2;
    public static final int BORDER_LEFT = 4;
    public static final int BORDER_RIGHT = 8;

    private TerminalRenderable renderable;

    private BorderStyle borderStyle;

    private int borderModifier;
    private int borderSize;

    public BorderRenderable(TerminalRenderable renderable, BorderStyle borderStyle, int borderSize, int borderModifier) {
        this.renderable = renderable;
        this.borderStyle = borderStyle;
        this.borderSize = borderSize;
        this.borderModifier = borderModifier;
    }

    public void setBorderStyle(BorderStyle borderStyle) {
        this.borderStyle = borderStyle;
    }

    @Override
    public void layout() {
        if (getPreferredSize().width > size.width || getPreferredSize().height > size.height) {
            renderable.setSize(size);
            renderable.setStartPoint(startPoint);
        } else {
            Point renderableStartPoint = new Point(startPoint);
            if ((borderModifier & BORDER_TOP) != 0)
                renderableStartPoint.y += this.borderSize;
            if ((borderModifier & BORDER_LEFT) != 0)
                renderableStartPoint.x += this.borderSize;

            renderable.setStartPoint(renderableStartPoint);

            Dimension remainingSize = new Dimension(size);
            Dimension borderSize = this.getBorderSize();

            remainingSize.width -= borderSize.width;
            remainingSize.height -= borderSize.height;
            renderable.setSize(remainingSize);
        }
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        Dimension renderableMinSize = renderable.getMinimumSize();

        renderable.render(terminalScreen);

        if (!renderableMinSize.equals(size)) {
            // TODO: implement other border styles
        }

    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferedSize = renderable.getPreferredSize();
        Dimension borderSize = this.getBorderSize();

        preferedSize.width += borderSize.width;
        preferedSize.height += borderSize.height;

        return preferedSize;
    }

    private Dimension getBorderSize() {
        Dimension dim = new Dimension();
        if ((borderModifier & BORDER_TOP) != 0)
            dim.height += borderSize;
        if ((borderModifier & BORDER_BOTTOM) != 0)
            dim.height += borderSize;
        if ((borderModifier & BORDER_LEFT) != 0)
            dim.width += borderSize;
        if ((borderModifier & BORDER_RIGHT) != 0)
            dim.width += borderSize;

        return dim;
    }

    @Override
    public Dimension getMinimumSize() {
        return renderable.getMinimumSize();
    }
}
