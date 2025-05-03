package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.iohandler.IOTerminalHandler;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Queue;

public class IOTerminalHandlerMock extends IOTerminalHandler {

    private final Queue<byte[]> inputQueue = new LinkedList<>();
    private boolean isInitialized = false;
    private boolean isDeinitialized = false;
    private Dimension screenSize;

    public IOTerminalHandlerMock() {
        this.screenSize = new Dimension(180, 30);
    }

    public void addToQueue(byte[] input) {
        inputQueue.add(input);
        synchronized (this) {
            this.notify();
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public boolean isDeinitialized() {
        return isDeinitialized;
    }

    @Override
    public void init() {
        isInitialized = true;
    }

    @Override
    public void deinit() {
        isDeinitialized = true;
    }

    @Override
    public byte[] getChar() {
        if(inputQueue.isEmpty()) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        if (inputQueue.isEmpty()) {
            return null;
        } else {
            return inputQueue.remove();
        }
    }

    public void windowResized(Dimension screenSize) {
        this.screenSize = new Dimension(screenSize);
        this.reportResize();
    }

    @Override
    public Dimension getSize() {
        return new Dimension(screenSize);
    }
}