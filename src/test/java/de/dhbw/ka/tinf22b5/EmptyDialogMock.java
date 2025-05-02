package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.dialog.Dialog;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class EmptyDialogMock extends Dialog {

    private final AtomicInteger timesRendered;
    private final AtomicInteger timesLayouted;

    public EmptyDialogMock() {
        this.timesRendered = new AtomicInteger(0);
        this.timesLayouted = new AtomicInteger(0);
    }

    @Override
    public boolean handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
        return false;
    }

    @Override
    public void render(TerminalScreen terminalScreen) {
        timesRendered.incrementAndGet();
    }

    @Override
    public void layout() {
        timesLayouted.incrementAndGet();
    }

    public int getTimesRendered() {
        return timesRendered.get();
    }

    public int getTimesLayouted() {
        return timesLayouted.get();
    }

    @Override
    public Dimension getPreferredSize() {
        return null;
    }

    @Override
    public Dimension getMinimumSize() {
        return null;
    }
}
