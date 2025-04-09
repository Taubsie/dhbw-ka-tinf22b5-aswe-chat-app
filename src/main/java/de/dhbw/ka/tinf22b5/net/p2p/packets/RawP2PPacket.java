package de.dhbw.ka.tinf22b5.net.p2p.packets;

import java.net.SocketAddress;

public class RawP2PPacket implements P2PPacket {

    private final byte[] data;
    private final SocketAddress address;

    public RawP2PPacket(byte[] data, SocketAddress address) {
        this.data = data;
        this.address = address;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return address;
    }
}
