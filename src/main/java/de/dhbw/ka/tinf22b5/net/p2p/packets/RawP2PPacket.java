package de.dhbw.ka.tinf22b5.net.p2p.packets;

import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public class RawP2PPacket implements P2PPacket {
    private final byte @NotNull [] data;
    private final SocketAddress address;

    public RawP2PPacket(byte @NotNull [] data, SocketAddress address) {
        this.data = data;
        this.address = address;
    }

    @Override
    public byte @NotNull [] getData() {
        return data;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return address;
    }
}
