package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.*;
import de.dhbw.ka.tinf22b5.terminal.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.win.WindowsTerminalHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws IOException {

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
            try {
                terminal.deinit();
            } catch (TerminalHandlerException e) {
                e.printStackTrace();
            }
            stop.set(true);
        }));

        TerminalKeyParser terminalKeyParser = new TerminalKeyParser();
        while (!stop.get()) {
            TerminalKeyEvent event = terminalKeyParser.parseTerminalKeyInput(terminal.getChar());
            System.out.print("Pressed char: " + TerminalKey.getKeyText(event.getTerminalKey()) + " " + event.getKeyType() + "\r\n");

            switch (event.getTerminalKey()) {
                case TerminalKey.TK_d, TerminalKey.TK_D -> System.out.print(terminal.getSize() + "\r\n");
                case TerminalKey.TK_q, TerminalKey.TK_Q -> stop.set(true);
                case TerminalKey.TK_g, TerminalKey.TK_G -> System.out.write(new byte[] {0x1b, '[', '6', 'n' });
            }
        }

        try {
            terminal.deinit();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }
    }
}