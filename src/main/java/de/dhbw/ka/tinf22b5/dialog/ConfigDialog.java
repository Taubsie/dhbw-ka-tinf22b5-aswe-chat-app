package de.dhbw.ka.tinf22b5.dialog;

import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

import java.io.IOException;

public class ConfigDialog extends Dialog {

    @Override
    public void render(TerminalRenderingBuffer terminalRenderingBuffer) {
        terminalRenderingBuffer.addString("Configuration");
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.addString("- Network Device -");
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.addString("None"); //TODO load from config
        terminalRenderingBuffer.nextLine();
    }

    @Override
    public void handleInput(TerminalKeyEvent event, TerminalHandler terminal) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_UP, TerminalKey.TK_W, TerminalKey.TK_w -> {
                terminal.moveCursor(CursorDirection.UP);
                if(terminal.getCursorY() % 2 == 0) {
                    terminal.moveCursor(CursorDirection.UP);
                }
            }
            case TerminalKey.TK_DOWN, TerminalKey.TK_S, TerminalKey.TK_s -> {
                terminal.moveCursor(CursorDirection.DOWN);
                if(terminal.getCursorY() % 2 == 0) {
                    terminal.moveCursor(CursorDirection.DOWN);
                }
            }
            case TerminalKey.TK_RIGHT, TerminalKey.TK_D, TerminalKey.TK_d, TerminalKey.TK_LEFT, TerminalKey.TK_A,
                 TerminalKey.TK_a -> {
            }
            case TerminalKey.TK_ENTER -> {
                String /*TODO make enum*/ config = switch (terminal.getCursorY()) {
                    case 3 -> "Network Device";
                    default -> null;
                };

                if(config != null) {
                    terminal.changeDialog(new ConfigChangeDialog(config));
                }
            }
            default -> super.handleInput(event, terminal);
        }
    }
}