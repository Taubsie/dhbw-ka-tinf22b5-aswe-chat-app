package de.dhbw.ka.tinf22b5.net.broadcast.packets;

import de.dhbw.ka.tinf22b5.net.broadcast.packets.data.WelcomeData;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class ReceivingWelcomePacket extends RawReceivedBroadcastPacket {
    private final WelcomeData welcomeData;

    public ReceivingWelcomePacket(WelcomeData welcomeData, InetAddress senderAddress) {
        super(welcomeData.toJson().getBytes(StandardCharsets.UTF_8), senderAddress);
        this.welcomeData = welcomeData;
    }

    public WelcomeData getWelcomeData() {
        return welcomeData;
    }
}