package de.dhbw.ka.tinf22b5.net.broadcast;

import java.io.Closeable;

public interface BroadcastUtil extends Closeable {

    void open();
    void close();

    void addBroadcastListener(BroadcastPacketListener broadcastPacketListener);
    void removeBroadcastListener(BroadcastPacketListener broadcastPacketListener);

    void sendBroadcastPacket(SendingBroadcastPacket broadcastPacket);

    default void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }
}