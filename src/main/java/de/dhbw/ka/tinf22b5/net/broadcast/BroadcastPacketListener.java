package de.dhbw.ka.tinf22b5.net.broadcast;

public interface BroadcastPacketListener {

    void packetReceived(SendingBroadcastPacket packet);
}
