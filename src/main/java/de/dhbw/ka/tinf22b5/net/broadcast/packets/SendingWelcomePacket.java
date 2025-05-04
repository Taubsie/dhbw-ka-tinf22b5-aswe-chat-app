package de.dhbw.ka.tinf22b5.net.broadcast.packets;

import de.dhbw.ka.tinf22b5.net.broadcast.packets.data.WelcomeData;

import java.nio.charset.StandardCharsets;

public class SendingWelcomePacket implements SendingBroadcastPacket {
    private final WelcomeData welcomeData;

    public SendingWelcomePacket(WelcomeData welcomeData) {
        this.welcomeData = welcomeData;
    }

    @Override
    public byte[] getData() {
        return welcomeData.toJson().getBytes(StandardCharsets.UTF_8);
    }
}