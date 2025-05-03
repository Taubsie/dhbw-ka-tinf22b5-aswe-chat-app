package de.dhbw.ka.tinf22b5.chat;

import java.util.LinkedList;
import java.util.List;

public class Chat {

    private final List<Message> messages;
    private final User remote;

    public Chat(User remote) {
        this.messages = new LinkedList<>();
        this.remote = remote;
    }

    public void addMessage(Message message) {
        messages.addFirst(message);
    }

    public List<Message> getMessages() {
        return new LinkedList<>(messages);
    }

    public User getRemoteUser() {
        return remote;
    }
}
