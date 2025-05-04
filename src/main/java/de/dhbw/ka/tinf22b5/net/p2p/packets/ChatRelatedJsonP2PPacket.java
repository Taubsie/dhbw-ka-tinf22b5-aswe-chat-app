package de.dhbw.ka.tinf22b5.net.p2p.packets;

import de.dhbw.ka.tinf22b5.chat.ChatRelatedJson;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public abstract class ChatRelatedJsonP2PPacket extends StringP2PPacket {
    public ChatRelatedJsonP2PPacket(@NotNull ChatRelatedJson data, SocketAddress address) {
        super(data.toJson(), address);
    }

    @NotNull
    public ChatRelatedJson getJson() {
        return fromJson(getString());
    }

    public abstract ChatRelatedJson fromJson(String json);
}