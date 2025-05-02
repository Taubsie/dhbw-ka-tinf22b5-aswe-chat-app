package de.dhbw.ka.tinf22b5.terminal.render.layout;

import de.dhbw.ka.tinf22b5.terminal.render.components.ContainerRenderable;
import de.dhbw.ka.tinf22b5.terminal.render.components.TerminalRenderable;

import java.awt.*;

public class VerticalSplitLayout implements LayoutManager {

    private final float leftPercent;

    public VerticalSplitLayout(float leftPercent) {
        this.leftPercent = leftPercent;
    }

    @Override
    public void layoutContainer(ContainerRenderable container) {
        // only layout first 2 components
        int componentCount = container.getComponents().length;

        if (componentCount == 1) {
            container.getComponents()[0].setStartPoint(container.getStartPoint());
            container.getComponents()[0].setSize(container.getSize());
        } else if (componentCount >= 2) {
            int leftWidth = (int) (container.getSize().width * leftPercent);

            container.getComponents()[0].setStartPoint(container.getStartPoint());
            container.getComponents()[0].setSize(new Dimension(leftWidth, container.getSize().height));
            container.getComponents()[0].setVisible(true);

            container.getComponents()[1].setStartPoint(new Point(leftWidth + container.getStartPoint().x, container.getStartPoint().y));
            container.getComponents()[1].setSize(new Dimension(container.getSize().width - leftWidth, container.getSize().height));
            container.getComponents()[1].setVisible(true);

            for (int i = 2; i < componentCount; i++) {
                container.getComponents()[1].setVisible(false);
            }
        }

        for (TerminalRenderable component : container.getComponents()) {
            component.layout();
        }
    }
}
