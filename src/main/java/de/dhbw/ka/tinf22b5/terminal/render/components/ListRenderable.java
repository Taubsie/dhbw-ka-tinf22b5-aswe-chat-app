package de.dhbw.ka.tinf22b5.terminal.render.components;

import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.BaseTerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.characters.DecoratedCharacterFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ListRenderable<T extends TerminalRenderable> extends TerminalRenderable implements Focusable {

    private final List<T> items;
    private boolean holdFocus = false;
    private Dimension virtualScreenSize;

    private int selectedIdx = 0;

    public ListRenderable() {
        this.items = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    public void removeItem(T item) {
        items.remove(item);
    }

    public void removeItem(int idx) {
        items.remove(idx);
    }

    public void clearItems() {
        items.clear();
    }

    public int getSelectedIdx() {
        return selectedIdx;
    }

    public boolean handleInput(TerminalKeyEvent event) {
        if (!holdFocus)
            return false;

        switch (event.getTerminalKey()) {
            case TerminalKey.TK_UP, TerminalKey.TK_W, TerminalKey.TK_w -> {
                selectedIdx--;
                return true;
            }
            case TerminalKey.TK_DOWN, TerminalKey.TK_S, TerminalKey.TK_s -> {
                selectedIdx++;
                return true;
            }
        }

        return false;
    }

    @Override
    public void layout() {
        int maxWidth = 0;
        int cummulativeHeight = 0;

        for (TerminalRenderable item : items) {
            if (item.getPreferredSize().width < 0) {
                maxWidth = this.size.width;
            } else
                maxWidth = Math.max(maxWidth, item.getPreferredSize().width);

            if (item.getPreferredSize().height < 0) {
                cummulativeHeight += this.size.height;
            } else
                cummulativeHeight += item.getPreferredSize().height;
        }

        this.virtualScreenSize = new Dimension(maxWidth, cummulativeHeight);

        int y = 0;
        for (TerminalRenderable item : items) {
            Dimension childSize = new Dimension(maxWidth, item.getPreferredSize().height);
            if (childSize.height < 0)
                childSize.height = this.size.height;

            item.setSize(childSize);
            item.setStartPoint(new Point(0, y));
            y += childSize.height;
        }

        items.forEach(TerminalRenderable::layout);
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        if (!isVisible())
            return;

        BaseTerminalScreen virtualScreen = new BaseTerminalScreen();
        virtualScreen.doResize(virtualScreenSize);

        for (int i = 0; i < items.size(); i++) {
            if (i == selectedIdx) {
                virtualScreen.pushCharacterModifier(DecoratedCharacterFactory.REVERSED);
                if(holdFocus)
                    virtualScreen.pushCharacterModifier(DecoratedCharacterFactory.BLINKING);
            }

            items.get(i).render(virtualScreen);

            if (i == selectedIdx) {
                virtualScreen.popCharacterModifier();
                if(holdFocus)
                    virtualScreen.popCharacterModifier();
            }
        }

        for (int line = 0; line < size.height; line++) {
            terminalScreen.setCursorPosition(startPoint.x, startPoint.y + line);
            terminalScreen.setCharacters(virtualScreen.getLine(line));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int maxPrefSize = 1;

        for (T item : items) {
            maxPrefSize = Math.max(maxPrefSize, item.getPreferredSize().width);
        }

        return new Dimension(maxPrefSize, -1);
    }

    @Override
    public Dimension getMinimumSize() {
        int maxMinSize = 1;

        for (T item : items) {
            maxMinSize = Math.max(maxMinSize, item.getMinimumSize().width);
        }

        return new Dimension(maxMinSize, -1);
    }

    @Override
    public void setFocus(boolean focus) {
        this.holdFocus = focus;
    }
}
