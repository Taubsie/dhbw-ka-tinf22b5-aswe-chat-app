package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.characters.PlainTerminalCharacter;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacter;

import java.awt.*;
import java.util.Arrays;

public class BorderRenderable extends TerminalRenderable {

    public enum BorderStyle {
        EMPTY, DASHED
    }

    public static final int BORDER_TOP = 1;
    public static final int BORDER_BOTTOM = 2;
    public static final int BORDER_LEFT = 4;
    public static final int BORDER_RIGHT = 8;

    private final TerminalRenderable renderable;

    private BorderStyle borderStyle;

    private final int borderModifier;
    private final int borderSize;

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
        Dimension prefSize = renderable.getPreferredSize();

        if (prefSize.width < 0) {
            prefSize.width = size.width;
        }

        if(prefSize.height < 0) {
            prefSize.height = size.height;
        }

        if (prefSize.width > size.width || prefSize.height > size.height) {
            renderable.setSize(size);
            renderable.setStartPoint(startPoint);
        } else {
            Point renderableStartPoint = new Point(startPoint);
            if ((borderModifier & BORDER_TOP) != 0) {
                renderableStartPoint.y += this.borderSize;
            }
            if ((borderModifier & BORDER_LEFT) != 0) {
                renderableStartPoint.x += this.borderSize;
            }

            renderable.setStartPoint(renderableStartPoint);

            Dimension remainingSize = new Dimension(size);
            Dimension borderSize = this.getBorderSize();

            remainingSize.width -= borderSize.width;
            remainingSize.height -= borderSize.height;
            renderable.setSize(remainingSize);
        }

        renderable.layout();
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        Dimension renderableMinSize = renderable.getMinimumSize();

        renderable.render(terminalScreen);
        if (size.height > renderableMinSize.height && size.width > renderableMinSize.width) {
            switch (borderStyle) {
                case EMPTY: // do nothing
                    break;
                case DASHED:
                    createDashedBorder(terminalScreen);
            }
        }

    }

    private void createDashedBorder(TerminalScreen terminalScreen) {
        boolean topBorder = (borderModifier & BORDER_TOP) != 0;
        boolean bottomBorder = (borderModifier & BORDER_BOTTOM) != 0;
        boolean leftBorder = (borderModifier & BORDER_LEFT) != 0;
        boolean rightBorder = (borderModifier & BORDER_RIGHT) != 0;

        if (topBorder) {
            terminalScreen.setCursorPosition(renderable.startPoint.x - (leftBorder ? 1 : 0), renderable.startPoint.y - 1);
            createDashedHorizontalLine(terminalScreen, leftBorder, rightBorder);
        }

        if (bottomBorder) {
            terminalScreen.setCursorPosition(renderable.startPoint.x - (leftBorder ? 1 : 0), renderable.startPoint.y + renderable.size.height);
            createDashedHorizontalLine(terminalScreen, leftBorder, rightBorder);
        }

        if (leftBorder) {
            for (int i = 0; i < renderable.size.height; i++) {
                terminalScreen.setCursorPosition(renderable.startPoint.x - 1, renderable.startPoint.y + i);
                terminalScreen.setCharacter(new PlainTerminalCharacter("|"));
            }
        }

        if (rightBorder) {
            for (int i = 0; i < renderable.size.height; i++) {
                int xOffset = Math.min(renderable.size.width, renderable.getPreferredSize().width);
                if (xOffset < 0) {
                    xOffset = renderable.size.width;
                }
                terminalScreen.setCursorPosition(renderable.startPoint.x + xOffset, renderable.startPoint.y + i);
                terminalScreen.setCharacter(new PlainTerminalCharacter("|"));
            }
        }
    }

    private void createDashedHorizontalLine(TerminalScreen terminalScreen, boolean leftBorder, boolean rightBorder) {
        int renderableSize = Math.min(renderable.size.width, renderable.getPreferredSize().width);
        if (renderableSize < 0)
            renderableSize = renderable.size.width;
        TerminalCharacter[] borderChars = new TerminalCharacter[renderableSize + (leftBorder ? 1 : 0) + (rightBorder ? 1 : 0)];
        Arrays.fill(borderChars, new PlainTerminalCharacter("-"));

        if (leftBorder)
            borderChars[0] = new PlainTerminalCharacter("+");
        if (rightBorder)
            borderChars[borderChars.length - 1] = new PlainTerminalCharacter("+");

        terminalScreen.setCharacters(borderChars);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferedSize = renderable.getPreferredSize();
        Dimension borderSize = this.getBorderSize();

        if (preferedSize.width >= 0)
            preferedSize.width += borderSize.width;
        if (preferedSize.height >= 0)
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
