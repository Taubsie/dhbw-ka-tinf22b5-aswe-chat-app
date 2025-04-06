package de.dhbw.ka.tinf22b5.net.broadcast;

import java.net.DatagramPacket;

public interface BroadcastPacketListener {

    // TODO: use abstraction for datagram packet
    void packetReceived(DatagramPacket packet);
}
