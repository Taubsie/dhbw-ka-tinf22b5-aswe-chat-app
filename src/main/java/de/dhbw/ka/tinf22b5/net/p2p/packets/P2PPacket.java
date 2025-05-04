package de.dhbw.ka.tinf22b5.net.p2p.packets;

import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public interface P2PPacket {
    byte @NotNull [] getData();

    SocketAddress getRemoteAddress();
}
