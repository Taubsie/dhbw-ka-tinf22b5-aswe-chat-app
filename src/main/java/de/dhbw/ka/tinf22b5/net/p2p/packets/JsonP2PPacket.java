package de.dhbw.ka.tinf22b5.net.p2p.packets;

import de.dhbw.ka.tinf22b5.chat.ChatRelatedJson;
import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;

public abstract class JsonP2PPacket<T extends ChatRelatedJson> extends StringP2PPacket {
    private final String type;
    private final T jsonData;

    public JsonP2PPacket(String type, @NotNull T data, SocketAddress address) {
        super(data.toJson(), address);
        this.type = type;
        this.jsonData = data;
    }

    public String getType() {
        return type;
    }

    public T getJsonData() {
        return jsonData;
    }
}