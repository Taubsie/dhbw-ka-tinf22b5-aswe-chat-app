package de.dhbw.ka.tinf22b5.chat;

import com.google.gson.Gson;

public abstract class ChatRelatedJson {
    private final User sender;

    protected ChatRelatedJson(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public String toJson() {
        //TODO rework / move to repo
        return new Gson().toJson(this);
    }
}