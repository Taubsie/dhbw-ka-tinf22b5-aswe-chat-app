package de.dhbw.ka.tinf22b5.net.broadcast.packets.data;

import de.dhbw.ka.tinf22b5.util.GsonUtil;

public class WelcomeData {
    private final String username;
    private final int port;

    public WelcomeData(String username, int port) {
        this.username = username;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public int getPort() {
        return port;
    }

    public String toJson() {
        //TODO use provider instead
        return GsonUtil.getGson().toJson(this);
    }
}