package de.dhbw.ka.tinf22b5.terminal.iohandler;

import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.iohandler.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.iohandler.win.WindowsTerminalHandler;
import de.dhbw.ka.tinf22b5.util.OSUtil;

public class IOTerminalFactory {

    public static IOTerminalHandler createTerminalHandler() throws TerminalHandlerException {
        IOTerminalHandler handler = switch (OSUtil.getOS()) {
            case WIN -> new WindowsTerminalHandler();
            case LINUX -> new LinuxTerminalHandler();
            default -> throw new TerminalHandlerException("Unsupported OS: " + OSUtil.getOS());
        };

        handler.attachShutdownHook();

        return handler;
    }
}
