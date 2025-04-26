package de.dhbw.ka.tinf22b5.terminal.render.layout;

import de.dhbw.ka.tinf22b5.terminal.render.components.ContainerRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.components.TerminalRenderable;

import java.awt.*;

public class ListLayout implements LayoutManager {

    private final boolean keepOnTop;

    public ListLayout(boolean keepOnTop) {
        this.keepOnTop = keepOnTop;
    }

    @Override
    public void layoutContainer(ContainerRenderable container) {
        Dimension size = container.getSize();
        Point startPoint = container.getStartPoint();

        TerminalRenderable[] children = container.getComponents();

        int preferedWidth = 0;
        int preferedHeight = 0;

        int minimumWidth = 0;
        int minimumHeight = 0;

        for (TerminalRenderable child : children) {
            preferedWidth = Math.max(preferedWidth, child.getPreferredSize().width);
            preferedHeight += child.getPreferredSize().height;

            minimumWidth = Math.max(minimumWidth, child.getMinimumSize().width);
            minimumHeight += child.getMinimumSize().height;
        }

        int width = Math.min(size.width, preferedWidth);

        if (preferedWidth <= size.width && preferedHeight <= size.height) {
            // can layout as wanted
            int y = startPoint.y;
            for (TerminalRenderable child : children) {
                child.setVisible(true);
                child.setSize(child.getPreferredSize());
                child.setStartPoint(new Point(startPoint.x, y));
                y += child.getPreferredSize().height;
            }
        } else if (minimumWidth <= size.width && minimumHeight <= size.height) {
            int y = startPoint.y;
            for (TerminalRenderable child : children) {
                child.setVisible(true);
                child.setSize(child.getMinimumSize());
                child.setStartPoint(new Point(startPoint.x, y));
                y += child.getMinimumSize().height;
            }
        } else {
            if (!keepOnTop) {
                int remainingHeight = size.height;
                for (int i = children.length - 1; i >= 0; i--) {
                    TerminalRenderable child = children[i];

                    Dimension childSize = child.getMinimumSize();

                    if (childSize.height > remainingHeight) {
                       child.setVisible(false);
                    } else {
                        child.setVisible(true);
                        child.setSize(new Dimension(width, childSize.height));
                        child.setStartPoint(new Point(startPoint.x, startPoint.y + remainingHeight - 1));
                        remainingHeight -= childSize.height;
                    }
                }
            }
        }

        for (TerminalRenderable child : children) {
            child.layout();
        }
    }
}
