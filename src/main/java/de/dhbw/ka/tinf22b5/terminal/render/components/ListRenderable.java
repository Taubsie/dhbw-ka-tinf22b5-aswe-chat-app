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
    private int scrollOffset = 0;

    private int selectedIdx = 0;

    public ListRenderable() {
        this.items = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    public T getItem(int idx) {
        return items.get(idx);
    }

    public void clearItems() {
        selectedIdx = 0;
        scrollOffset = 0;
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
                selectedIdx = Math.max(0, selectedIdx - 1);
                return true;
            }
            case TerminalKey.TK_DOWN, TerminalKey.TK_S, TerminalKey.TK_s -> {
                selectedIdx = Math.min(selectedIdx + 1, items.size() - 1);
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
        if (!isVisible() || items.isEmpty())
            return;

        BaseTerminalScreen virtualScreen = renderIntoVirtualScreen();

        if(selectedIdx >= items.size())
            selectedIdx = items.size() - 1;

        checkForValidScrollOffset();

        for (int line = 0; line < size.height; line++) {
            terminalScreen.setCursorPosition(startPoint.x, startPoint.y + line);
            terminalScreen.setCharacters(virtualScreen.getLine(line + scrollOffset));
        }
    }

    private void checkForValidScrollOffset() {
        // check if selected item fits in current frame
        TerminalRenderable selectedItem = items.get(selectedIdx);
        // selected is above
        if (selectedItem.startPoint.y < scrollOffset) {
            scrollOffset = selectedItem.startPoint.y;
        } else if (selectedItem.startPoint.y >= scrollOffset + this.size.height) {
            scrollOffset = selectedItem.startPoint.y;
        } else if (selectedItem.startPoint.y != scrollOffset && selectedItem.startPoint.y + selectedItem.size.height > scrollOffset + this.size.height) {
            scrollOffset = selectedItem.startPoint.y;
        }
    }

    private BaseTerminalScreen renderIntoVirtualScreen() {
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

        return virtualScreen;
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
