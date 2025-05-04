package de.dhbw.ka.tinf22b5.net.p2p;

import com.google.gson.JsonObject;
import de.dhbw.ka.tinf22b5.chat.Chat;
import de.dhbw.ka.tinf22b5.chat.ChatRelatedJson;
import de.dhbw.ka.tinf22b5.chat.Message;
import de.dhbw.ka.tinf22b5.net.p2p.packets.MessageSendP2PPacket;
import de.dhbw.ka.tinf22b5.net.p2p.packets.P2PPacket;
import de.dhbw.ka.tinf22b5.net.p2p.packets.WelcomeP2PPacket;
import de.dhbw.ka.tinf22b5.util.GsonUtil;

import java.net.InetSocketAddress;
import java.util.Optional;

public class P2PPacketParser {
    private P2PPacketParser() {}

    public static Optional<P2PPacket> parse(String packetType, JsonObject data, InetSocketAddress inetSocketAddress) {
        ChatRelatedJson chatRelatedJson = GsonUtil.getGson().fromJson(data, ChatRelatedJson.class);

        if(packetType.equals(MessageSendP2PPacket.TYPE) && chatRelatedJson instanceof Message message) {
            return Optional.of(new MessageSendP2PPacket(message, inetSocketAddress));
        }

        if(packetType.equals(WelcomeP2PPacket.TYPE) && chatRelatedJson instanceof Chat chat) {
            return Optional.of(new WelcomeP2PPacket(chat, inetSocketAddress));
        }

        return Optional.empty();
    }
}