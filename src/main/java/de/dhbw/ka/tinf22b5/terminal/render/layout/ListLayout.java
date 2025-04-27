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

        boolean preferedMaxWidth = false;
        boolean preferedMaxHeight = false;

        for (TerminalRenderable child : children) {
            if (child.getPreferredSize().width == -1)
                preferedMaxWidth = true;

            if (child.getPreferredSize().height == -1)
                preferedMaxHeight = true;

            preferedWidth = Math.max(preferedWidth, child.getPreferredSize().width);
            preferedHeight += child.getPreferredSize().height;

            minimumWidth = Math.max(minimumWidth, child.getMinimumSize().width);
            minimumHeight += child.getMinimumSize().height;
        }

        int width = Math.min(size.width, preferedMaxWidth ? size.width : preferedWidth);

        if (preferedMaxHeight)
            preferedHeight = Math.max(preferedHeight, size.height);

        if (preferedWidth <= size.width && preferedHeight <= size.height && !preferedMaxHeight) {
            // can layout as wanted
            int y = startPoint.y;
            for (TerminalRenderable child : children) {
                child.setVisible(true);

                Dimension childrenPrefSize = child.getPreferredSize();
                if (childrenPrefSize.width < 0)
                    childrenPrefSize.width = width;
                if (childrenPrefSize.height < 0)
                    childrenPrefSize.height = size.height - (y - startPoint.y);

                child.setSize(childrenPrefSize);
                child.setStartPoint(new Point(startPoint.x, y));
                y += childrenPrefSize.height;
            }
        } else if (minimumWidth <= size.width && minimumHeight <= size.height  && !preferedMaxHeight) {
            int y = startPoint.y;
            for (TerminalRenderable child : children) {
                child.setVisible(true);

                Dimension childrenMinSize = child.getMinimumSize();
                if (childrenMinSize.width < 0)
                    childrenMinSize.width = width;
                if (childrenMinSize.height < 0)
                    childrenMinSize.height = size.height - (y - startPoint.y);

                child.setSize(childrenMinSize);
                child.setStartPoint(new Point(startPoint.x, y));
                y += childrenMinSize.height;
            }
        } else {
            if (!keepOnTop) {
                int remainingHeight = size.height;
                for (int i = children.length - 1; i >= 0; i--) {
                    TerminalRenderable child = children[i];

                    Dimension childSize = child.getPreferredSize();
                    if (childSize.height < 0)
                        childSize.height = remainingHeight;

                    if (childSize.height > remainingHeight) {
                        child.setVisible(false);
                    } else {
                        child.setVisible(true);
                        child.setSize(new Dimension(width, childSize.height));
                        child.setStartPoint(new Point(startPoint.x, startPoint.y + remainingHeight - childSize.height));
                        remainingHeight -= childSize.height;
                    }
                }
            } else {
                int remainingHeight = size.height;
                int y = startPoint.y;
                for (int i = 0; i < children.length; i++) {
                    TerminalRenderable child = children[i];

                    Dimension childSize = child.getPreferredSize();
                    if (childSize.height < 0)
                        childSize.height = remainingHeight;

                    if (childSize.height > remainingHeight) {
                        child.setVisible(false);
                    } else {
                        child.setVisible(true);
                        child.setSize(new Dimension(width, childSize.height));
                        child.setStartPoint(new Point(startPoint.x, y++));
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
