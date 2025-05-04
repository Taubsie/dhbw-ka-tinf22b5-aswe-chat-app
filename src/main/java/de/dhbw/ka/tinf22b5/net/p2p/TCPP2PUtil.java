package de.dhbw.ka.tinf22b5.net.p2p;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import de.dhbw.ka.tinf22b5.chat.ChatRelatedJson;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.EmptyConfigurationRepository;
import de.dhbw.ka.tinf22b5.net.broadcast.BroadcastUtil;
import de.dhbw.ka.tinf22b5.net.broadcast.UDPBroadcastUtil;
import de.dhbw.ka.tinf22b5.net.p2p.packets.JsonP2PPacket;
import de.dhbw.ka.tinf22b5.net.p2p.packets.P2PPacket;
import de.dhbw.ka.tinf22b5.net.p2p.packets.RawP2PPacket;
import de.dhbw.ka.tinf22b5.util.GsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPP2PUtil implements P2PUtil {

    private static final int DEFAULT_P2P_SERVER_PORT = 1337;

    private static final int SOCKET_TIMEOUT = 1000; // timeout in ms

    private final ConfigurationRepository configurationRepository;

    private int port;
    private final AtomicBoolean running;
    private ServerSocket serverSocket;

    private final AtomicBoolean listenerRunning;
    private Thread listenerThread;
    private final Set<P2PPacketListener> p2PPacketListeners;

    public TCPP2PUtil() {
        this(new EmptyConfigurationRepository());
    }

    public TCPP2PUtil(@NotNull ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;

        this.port = -1;
        this.running = new AtomicBoolean(false);

        this.listenerRunning = new AtomicBoolean(false);
        this.p2PPacketListeners = new HashSet<>();
    }

    @Override
    public synchronized void open() {
        if(this.running.get())
            return;

        this.port = configurationRepository.getIntConfigurationValue(ConfigurationKey.P2P_SERVER_PORT).orElse(DEFAULT_P2P_SERVER_PORT);

        this.listenerRunning.set(true);
        this.listenerThread = createListenerThread();
        this.listenerThread.start();

        try {
            this.serverSocket = new ServerSocket();
            this.serverSocket.setSoTimeout(SOCKET_TIMEOUT);
            this.serverSocket.bind(new InetSocketAddress(this.port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.running.set(true);
    }

    private Thread createListenerThread() {
        Thread thread = new Thread(() -> {

            while (this.listenerRunning.get()) {
                Optional<P2PPacket> p2PPacket = receiveP2PPacket();

                if (p2PPacket.isPresent()) {
                   Set<P2PPacketListener> listeners;
                   synchronized (p2PPacketListeners) {
                       listeners = new HashSet<>(p2PPacketListeners);
                   }

                   listeners.forEach(l -> l.onP2PPacketReceived(p2PPacket.get()));
                }
            }
        });

        thread.setDaemon(true);
        thread.setName("TCPP2PListenerThread");

        return thread;
    }

    @Override
    public synchronized void close() {
        if(!this.running.get())
            return;

        this.port = -1;

        this.listenerRunning.set(false);
        try {
            this.listenerThread.join();
        } catch (InterruptedException _) {
        }

        try {
            this.serverSocket.close();
        } catch (IOException _) {
            // ignoring closing errors
        }

        this.running.set(false);
    }

    @Override
    public int getServerPort() {
        if(!running.get())
            return -1;
        else
            return port;
    }

    @Override
    public void addP2PListener(P2PPacketListener p2PPacketListener) {
        synchronized (p2PPacketListeners) {
            p2PPacketListeners.add(p2PPacketListener);
        }
    }

    @Override
    public void removeP2PListener(P2PPacketListener p2PPacketListener) {
        synchronized (p2PPacketListeners) {
            p2PPacketListeners.remove(p2PPacketListener);
        }
    }

    @Override
    public void sendP2PPacket(P2PPacket p2PPacket) {
        if(!running.get())
            return;

        try (Socket socket = new Socket()) {
            socket.setSoTimeout(SOCKET_TIMEOUT);
            socket.connect(p2PPacket.getRemoteAddress(), SOCKET_TIMEOUT);

            socket.getOutputStream().write(port2Array(this.port));

            if(p2PPacket instanceof JsonP2PPacket<?> jsonP2PPacket) {
                JsonPrimitive type = new JsonPrimitive(jsonP2PPacket.getType());

                JsonElement data = GsonUtil.getGson().toJsonTree(jsonP2PPacket.getJsonData());

                JsonObject result = new JsonObject();

                result.add("type", type);
                result.add("data", data);

                socket.getOutputStream().write(GsonUtil.getGson().toJson(result).getBytes(StandardCharsets.UTF_8));
            } else {
                socket.getOutputStream().write(p2PPacket.getData());
            }

            socket.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<P2PPacket> receiveP2PPacket() {
        if(!running.get())
            return Optional.empty();

        try (Socket client = serverSocket.accept()) {
            byte[] remotePortBytes = new byte[2];
            int nread = client.getInputStream().read(remotePortBytes);
            if(nread != 2)
                return Optional.empty();

            int remotePort = array2Port(remotePortBytes);
            InetAddress remoteAddress = client.getInetAddress();

            byte[] sendBuffer = client.getInputStream().readAllBytes();

            String json = new String(sendBuffer, StandardCharsets.UTF_8);

            try {
                JsonObject jsonObject = GsonUtil.getGson().fromJson(json, JsonObject.class);
                if(!jsonObject.has("type")) {
                    throw new JsonSyntaxException("Missing type field");
                }

                String packetType = jsonObject.getAsJsonPrimitive("type").getAsString();

                return P2PPacketParser.parse(packetType, jsonObject.getAsJsonObject("data"), new InetSocketAddress(remoteAddress, remotePort));
            } catch (JsonSyntaxException e) {
                return Optional.of(new RawP2PPacket(sendBuffer, new InetSocketAddress(remoteAddress, remotePort)));
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private byte[] port2Array(int port) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putInt(port);
        return Arrays.copyOfRange(buf.array(), 2, 4);
    }

    private int array2Port(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.position(2);
        buf.put(bytes);
        buf.position(0);
        return buf.getInt();
    }

    public static void main(String[] ignoredArgs) {

        TCPP2PUtil p2pUtil = new TCPP2PUtil();
        p2pUtil.attachShutdownHook();
        p2pUtil.open();
        p2pUtil.addP2PListener(p -> System.out.printf("%s: %s\r\n", p.getRemoteAddress(),new String(p.getData())));

        BroadcastUtil broadcastUtil = new UDPBroadcastUtil();
        broadcastUtil.attachShutdownHook();
        broadcastUtil.open();
        broadcastUtil.addBroadcastListener(p -> {
            System.out.println("Got broadcast recovery packet from " + p.getRemoteAddress());
            p2pUtil.sendP2PPacket(new RawP2PPacket("Hello World!".getBytes(), new InetSocketAddress(p.getRemoteAddress(), 1337)));
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException _) {
        }

        broadcastUtil.sendBroadcastPacket(() -> new byte[0]);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException _) {
        }

        p2pUtil.close();
        broadcastUtil.close();
    }
}
