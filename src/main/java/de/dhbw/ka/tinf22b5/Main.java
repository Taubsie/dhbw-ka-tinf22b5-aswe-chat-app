package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.win.WindowsTerminalHandler;

import java.util.Arrays;
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
            try {
                terminal.deinit();
            } catch (TerminalHandlerException e) {
                e.printStackTrace();
            }
            stop.set(true);
        }));

        byte[] readChars;
        while (!stop.get()) {
            readChars = terminal.getChar();
            System.out.print("Pressed chars as escaped sequence: " + Arrays.toString(readChars) + "\r\n");

            if (readChars.length >= 1) {
                switch (readChars[0]) {
                    case 'd' -> System.out.print(terminal.getSize() + "\r\n");
                    case 'q' -> stop.set(true);
                }
            }
        }

        try {
            terminal.deinit();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }
    }
}