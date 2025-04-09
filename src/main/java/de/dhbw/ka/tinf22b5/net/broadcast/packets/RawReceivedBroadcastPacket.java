package de.dhbw.ka.tinf22b5.net.broadcast.packets;

import java.net.InetAddress;

public class RawReceivedBroadcastPacket implements ReceivingBroadcastPacket {

    private final byte[] data;
    private final InetAddress senderAddress;

    public RawReceivedBroadcastPacket(byte[] data, InetAddress senderAddress) {
        this.data = data;
        this.senderAddress = senderAddress;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public InetAddress getRemoteAddress() {
        return senderAddress;
    }
}
