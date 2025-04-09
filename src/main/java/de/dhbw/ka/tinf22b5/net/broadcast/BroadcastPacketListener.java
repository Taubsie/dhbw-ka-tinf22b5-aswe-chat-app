package de.dhbw.ka.tinf22b5.net.broadcast;

import de.dhbw.ka.tinf22b5.net.broadcast.packets.ReceivingBroadcastPacket;

public interface BroadcastPacketListener {

    void packetReceived(ReceivingBroadcastPacket packet);
}
