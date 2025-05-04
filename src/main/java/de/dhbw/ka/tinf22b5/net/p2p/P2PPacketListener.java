package de.dhbw.ka.tinf22b5.net.p2p;

import de.dhbw.ka.tinf22b5.net.p2p.packets.P2PPacket;

public interface P2PPacketListener {

    void onP2PPacketReceived(P2PPacket packet);
}
