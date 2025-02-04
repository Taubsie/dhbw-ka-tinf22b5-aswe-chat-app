package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.win.WindowsTerminalHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        TerminalHandler terminal;
        // nicht schön aber funktioniert
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            terminal = new WindowsTerminalHandler();
        } else {
            terminal = new LinuxTerminalHandler();
        }

        try {
            terminal.init();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }

        AtomicBoolean stop = new AtomicBoolean(false);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            terminal.deinit();
            stop.set(true);
        }));

        int c = -1;
        while (c != 'q' && !stop.get()) {
            c = terminal.getChar();
            System.out.println(c + " " + (char) c);

            if(c == 'd') {
                System.out.println(terminal.getSize());
            }
        }

        terminal.deinit();
    }
}