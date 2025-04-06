package de.dhbw.ka.tinf22b5.net.broadcast;

import java.io.Closeable;
import java.net.DatagramPacket;
import java.util.Optional;

public interface BroadcastUtil extends Closeable {

    void open();
    void close();

    void addBroadcastListener(BroadcastPacketListener broadcastPacketListener);
    void removeBroadcastListener(BroadcastPacketListener broadcastPacketListener);

    void sendBroadcastPacket(SendingBroadcastPacket broadcastPacket);
    // TODO: use abstraction for datagram packet
    Optional<DatagramPacket> receiveBroadcastPacket();

    default void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }
}