package de.dhbw.ka.tinf22b5.net.p2p.packets;

import de.dhbw.ka.tinf22b5.chat.Message;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public class MessageSendP2PPacket extends JsonP2PPacket<Message> {
    public static final String TYPE = "message";

    public MessageSendP2PPacket(@NotNull Message data, SocketAddress address) {
        super(TYPE, data, address);
    }
}