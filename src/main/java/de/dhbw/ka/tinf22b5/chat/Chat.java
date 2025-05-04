package de.dhbw.ka.tinf22b5.chat;

import java.util.LinkedList;
import java.util.List;

public class Chat extends ChatRelatedJson {

    private final List<Message> messages;

    public Chat(User remote) {
        super("chat", remote);
        this.messages = new LinkedList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }
}
