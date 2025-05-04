package de.dhbw.ka.tinf22b5.net.p2p.packets;

import com.google.gson.Gson;
import de.dhbw.ka.tinf22b5.chat.ChatRelatedJson;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public class JsonP2PPacket<T extends ChatRelatedJson> extends StringP2PPacket {
    private final Class<T> packetType;

    public JsonP2PPacket(Class<T> packetType, @NotNull T data, SocketAddress address) {
        super(data.toJson(), address);
        this.packetType = packetType;
    }

    public T fromJson() {
        //TODO rework / move to repo?
        return new Gson().fromJson(getString(), packetType);
    }
}