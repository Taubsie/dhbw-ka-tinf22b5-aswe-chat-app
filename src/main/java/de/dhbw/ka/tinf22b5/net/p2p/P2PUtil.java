package de.dhbw.ka.tinf22b5.net.p2p;

import de.dhbw.ka.tinf22b5.net.p2p.packets.P2PPacket;

import java.io.Closeable;

public interface P2PUtil extends Closeable {

    void open();
    void close();

    int getServerPort();

    void addP2PListener(P2PPacketListener p2PPacketListener);
    void removeP2PListener(P2PPacketListener p2PPacketListener);

    void sendP2PPacket(P2PPacket p2PPacket);

    default void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }
}
