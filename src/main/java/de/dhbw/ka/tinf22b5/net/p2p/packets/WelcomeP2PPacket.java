package de.dhbw.ka.tinf22b5.net.p2p.packets;

import de.dhbw.ka.tinf22b5.chat.Chat;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public class WelcomeP2PPacket extends JsonP2PPacket<Chat> {
    public static final String TYPE = "welcome";

    public WelcomeP2PPacket(@NotNull Chat data, SocketAddress address) {
        super(TYPE, data, address);
    }
}