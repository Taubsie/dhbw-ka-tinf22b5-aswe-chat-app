package de.dhbw.ka.tinf22b5.net.p2p.packets;

import java.net.SocketAddress;

public interface P2PPacket {

    byte[] getData();
    SocketAddress getRemoteAddress();
}
