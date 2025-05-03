package de.dhbw.ka.tinf22b5.tests;

import de.dhbw.ka.tinf22b5.EmptyDialogMock;
import de.dhbw.ka.tinf22b5.IOTerminalHandlerMock;
import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.handler.BaseTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.iohandler.IOTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.render.dialog.Dialog;
import de.dhbw.ka.tinf22b5.terminal.render.dialog.WelcomeDialog;
import de.dhbw.ka.tinf22b5.util.FieldSwitcher;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class TestBaseTerminalHandler {

    private static Thread startBaseTerminalHandlerQueue(BaseTerminalHandler handler, IOTerminalHandler ioHandler, OutputStream out) {
        assertNotNull(handler);
        assertNotNull(ioHandler);
        assertNotNull(out);
        assertTrue(FieldSwitcher.switchField(handler, "ioTerminalHandler", ioHandler));
        assertTrue(FieldSwitcher.switchField(handler, "outputStream", out));

        Thread executionThread = new Thread(() -> {
            try {
                handler.run();
            } catch (IOException | TerminalHandlerException e) {
                throw new RuntimeException(e);
            }
        });

        executionThread.setName("TestBaseTerminalHandlerExecutionThread");
        executionThread.setDaemon(true);

        return executionThread;
    }

    @Test
    public void testInitWithQExit() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();
        mock.addToQueue(new byte[] {'q'});

        BaseTerminalHandler handler = null;
        try {
            handler = new BaseTerminalHandler(new EmptyDialogMock());
        } catch (TerminalHandlerException e) {
        }

        Thread executionThread = startBaseTerminalHandlerQueue(handler, mock, new ByteArrayOutputStream());
        executionThread.start();
        try {
            executionThread.join(1000);
        } catch (InterruptedException e) {
        }

        assertTrue(mock.isInitialized());
        assertTrue(mock.isDeinitialized());
    }

    @Test
    public void testInitWithCtrlCExit() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();
        mock.addToQueue(new byte[] {TerminalKey.TK_CTRL_C});

        BaseTerminalHandler handler = null;
        try {
            handler = new BaseTerminalHandler(new EmptyDialogMock());
        } catch (TerminalHandlerException e) {
        }

        Thread executionThread = startBaseTerminalHandlerQueue(handler, mock, new ByteArrayOutputStream());
        executionThread.start();
        try {
            executionThread.join(1000);
        } catch (InterruptedException e) {
        }

        assertTrue(mock.isInitialized());
        assertTrue(mock.isDeinitialized());
    }

    @Test
    public void testInitWithoutExit() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();

        BaseTerminalHandler handler = null;
        try {
            handler = new BaseTerminalHandler(new EmptyDialogMock());
        } catch (TerminalHandlerException e) {
        }

        Thread executionThread = startBaseTerminalHandlerQueue(handler, mock, new ByteArrayOutputStream());
        executionThread.start();
        try {
            executionThread.join(1000);
        } catch (InterruptedException e) {
        }

        assertTrue(mock.isInitialized());
        assertFalse(mock.isDeinitialized());
    }

    @Test
    public void testBasicDialogRendering() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();

        EmptyDialogMock currentDialog = new EmptyDialogMock();
        BaseTerminalHandler handler = null;
        try {
            handler = new BaseTerminalHandler(currentDialog);
        } catch (TerminalHandlerException e) {
        }

        Thread executionThread = startBaseTerminalHandlerQueue(handler, mock, new ByteArrayOutputStream());
        executionThread.start();
        try {
            executionThread.join(500);
        } catch (InterruptedException e) {
        }

        assertEquals(1, currentDialog.getTimesRendered());
        assertEquals(1, currentDialog.getTimesLayouted());
    }

    @Test
    public void testInputDialogRendering() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();

        mock.addToQueue(new byte[] {'i'});
        mock.addToQueue(new byte[] {'i'});
        mock.addToQueue(new byte[] {'i'});
        mock.addToQueue(new byte[] {'i'});

        EmptyDialogMock currentDialog = new EmptyDialogMock();
        BaseTerminalHandler handler = null;
        try {
            handler = new BaseTerminalHandler(currentDialog);
        } catch (TerminalHandlerException e) {
        }

        Thread executionThread = startBaseTerminalHandlerQueue(handler, mock, new ByteArrayOutputStream());
        executionThread.start();
        try {
            executionThread.join(500);
        } catch (InterruptedException e) {
        }

        assertEquals(5, currentDialog.getTimesRendered());
        assertEquals(5, currentDialog.getTimesLayouted());
    }

    @Test
    public void testResizeDialogRendering() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();

        EmptyDialogMock currentDialog = new EmptyDialogMock();
        final BaseTerminalHandler handler;
        try {
            handler = new BaseTerminalHandler(currentDialog);
        } catch (TerminalHandlerException e) {
            throw new RuntimeException(e);
        }

        assertTrue(FieldSwitcher.switchField(handler, "ioTerminalHandler", mock));
        assertTrue(FieldSwitcher.switchField(handler, "outputStream", new ByteArrayOutputStream()));
        mock.init();
        mock.addResizeListener(() -> {
            try {
                handler.updateTerminal();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        assertEquals(0, currentDialog.getTimesRendered());
        assertEquals(0, currentDialog.getTimesLayouted());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        mock.windowResized(new Dimension(100, 100));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        assertEquals(1, currentDialog.getTimesRendered());
        assertEquals(1, currentDialog.getTimesLayouted());
    }

    @Test
    public void testDialogConfigSwitches() {
        IOTerminalHandlerMock mock = new IOTerminalHandlerMock();

        Dialog currentDialog = new WelcomeDialog();
        BaseTerminalHandler handler = null;
        try {
            handler = new BaseTerminalHandler(currentDialog);
        } catch (TerminalHandlerException e) {
        }

        Stack<Dialog> dialogStack = new Stack<>();
        dialogStack.add(currentDialog);

        Thread executionThread = startBaseTerminalHandlerQueue(handler, mock, new ByteArrayOutputStream());
        assertTrue(FieldSwitcher.switchField(handler, "dialogStack", dialogStack));

        assertEquals(1, dialogStack.size());

        executionThread.start();

        mock.addToQueue(new byte[] {'c'});
        mock.addToQueue(new byte[] { TerminalKey.TK_ENTER });

        try {
            executionThread.join(500);
        } catch (InterruptedException e) {
        }

        assertEquals(3, dialogStack.size());

        mock.addToQueue(new byte[] { TerminalKey.TK_CTRL_Q });
        try {
            executionThread.join(500);
        } catch (InterruptedException e) {
        }

        assertEquals(2, dialogStack.size());

        mock.addToQueue(new byte[] { TerminalKey.TK_ESCAPE });
        try {
            executionThread.join(500);
        } catch (InterruptedException e) {
        }

        assertEquals(1, dialogStack.size());
    }
}