package de.dhbw.ka.tinf22b5.net.broadcast.packets;

import java.net.InetAddress;

public interface ReceivingBroadcastPacket {

    byte[] getData();

    InetAddress getRemoteAddress();
}
