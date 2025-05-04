package de.dhbw.ka.tinf22b5.chat;

import java.util.Calendar;
import java.util.Objects;

public class Message extends ChatRelatedJson {

    private final String message;
    private final Calendar date;
    private final boolean isRemoteMessage;

    public Message(User sender, String message, Calendar date, boolean isRemoteMessage) {
        super(sender);
        this.message = message;
        this.date = date;
        this.isRemoteMessage = isRemoteMessage;
    }

    public String getMessage() {
        return message;
    }

    public Calendar getDate() {
        return date;
    }

    public boolean isRemoteMessage() {
        return isRemoteMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return isRemoteMessage == message1.isRemoteMessage && Objects.equals(message, message1.message) && Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, date, isRemoteMessage);
    }
}
