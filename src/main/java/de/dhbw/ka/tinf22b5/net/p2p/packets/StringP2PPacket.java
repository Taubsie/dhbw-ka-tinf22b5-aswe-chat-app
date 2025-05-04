package de.dhbw.ka.tinf22b5.net.p2p.packets;

import org.jetbrains.annotations.NotNull;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class StringP2PPacket extends RawP2PPacket {
    public StringP2PPacket(@NotNull String data, SocketAddress address) {
        super(data.getBytes(StandardCharsets.UTF_8), address);
    }

    @NotNull
    public String getString() {
        return new String(getData(), StandardCharsets.UTF_8);
    }
}