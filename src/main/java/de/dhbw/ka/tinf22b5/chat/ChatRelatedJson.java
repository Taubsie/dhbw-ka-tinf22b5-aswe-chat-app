package de.dhbw.ka.tinf22b5.chat;

import de.dhbw.ka.tinf22b5.net.p2p.serialization.JsonSubtype;
import de.dhbw.ka.tinf22b5.net.p2p.serialization.JsonType;
import de.dhbw.ka.tinf22b5.util.GsonUtil;

@JsonType(property = "type", subtypes = {
        @JsonSubtype(clazz = Chat.class, name = "chat"),
        @JsonSubtype(clazz = Message.class, name = "message")
})
public abstract class ChatRelatedJson {
    private final String type;
    private final User sender;

    protected ChatRelatedJson(String type, User sender) {
        this.type = type;
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public User getSender() {
        return sender;
    }

    public String toJson() {
        return GsonUtil.getGson().toJson(this);
    }
}