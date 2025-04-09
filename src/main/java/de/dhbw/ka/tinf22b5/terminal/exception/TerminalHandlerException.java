package de.dhbw.ka.tinf22b5.terminal.exception;

public class TerminalHandlerException extends Exception {
    public TerminalHandlerException(String message) {
        super(message);
    }

    public TerminalHandlerException(Throwable e) {
        super(e);
    }
}
