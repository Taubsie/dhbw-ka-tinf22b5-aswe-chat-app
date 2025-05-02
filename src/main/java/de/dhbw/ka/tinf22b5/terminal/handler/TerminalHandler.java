package de.dhbw.ka.tinf22b5.terminal.handler;

import de.dhbw.ka.tinf22b5.terminal.render.dialog.Dialog;

import java.io.IOException;

public interface TerminalHandler {

    void changeDialog(Dialog dialog) throws IOException;

    void pushDialog(Dialog dialog) throws IOException;
    void popDialog() throws IOException;

    void quit();
}
