package de.dhbw.ka.tinf22b5.net.broadcast;

import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.EmptyConfigurationRepository;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPBroadcastUtil implements BroadcastUtil {
    private static final String DEFAULT_BROADCAST_IP = "233.1.33.69";
    private static final int DEFAULT_PORT = 1337;
    private static final int BUFFER_SIZE = 1024;
    private static final int SOCKET_TIMEOUT = 1000; // 1s timeout

    private InetAddress multicastAddress;
    private int multicastPort;

    private MulticastSocket multicastSocket;
    private final AtomicBoolean running;
    private final AtomicBoolean shouldStop;
    private Thread listenerThread;
    private final Set<BroadcastPacketListener> broadcastPacketListeners;

    private final ConfigurationRepository configurationRepository;

    public UDPBroadcastUtil() {
        this(new EmptyConfigurationRepository());
    }

    public UDPBroadcastUtil(@NotNull ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;

        this.running = new AtomicBoolean(false);
        this.shouldStop = new AtomicBoolean(true);
        this.broadcastPacketListeners = new HashSet<>();
    }

    @Override
    public synchronized void open() {
        if(running.get()) {
            return;
        }

        this.multicastAddress = getBroadcastAddress();
        this.multicastPort = getBroadcastPort();

        try {
            this.multicastSocket = new MulticastSocket(DEFAULT_PORT);
            this.multicastSocket.setSoTimeout(SOCKET_TIMEOUT);

            this.multicastSocket.joinGroup(this.multicastAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        running.set(true);
    }

    private InetAddress getBroadcastAddress() {
        InetAddress multicastAddress = null;

        try {
                multicastAddress = InetAddress.getByName(configurationRepository.getConfigurationValue(ConfigurationKey.BROADCAST_IP_ADDRESS)
                        .orElse(DEFAULT_BROADCAST_IP));
        } catch (UnknownHostException _) {
            // ignoring error, using default
            try {
                multicastAddress = InetAddress.getByName(DEFAULT_BROADCAST_IP);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        return multicastAddress;
    }

    private int getBroadcastPort() {
        return configurationRepository.getIntConfigurationValue(ConfigurationKey.BROADCAST_PORT).orElse(DEFAULT_PORT);
    }

    @Override
    public synchronized void close() {
        if(!running.get()) {
            return;
        }

        closeListenerThread();

        try {
            this.multicastSocket.leaveGroup(this.multicastAddress);
            this.multicastSocket.close();
        } catch (IOException _) {
            // ignoring closing errors
        }

        running.set(false);
    }

    private synchronized void openListenerThread() {
        if (!this.shouldStop.get()) {
            return;
        }

        this.shouldStop.set(false);
        this.listenerThread = getListenerThread();
        this.listenerThread.start();
    }

    private synchronized void closeListenerThread() {
        if (this.shouldStop.get()) {
            return;
        }

        this.shouldStop.set(true);

        try {
            this.listenerThread.join();
        } catch (InterruptedException _) {
        }
        this.listenerThread = null;
    }

    private Thread getListenerThread() {
        return new Thread(() -> {

            Optional<DatagramPacket> packet;
            while (!shouldStop.get()) {
                packet = receiveBroadcastPacket();
                if (packet.isPresent()) {
                    Set<BroadcastPacketListener> listeners;
                    synchronized (broadcastPacketListeners) {
                        listeners = new HashSet<>(broadcastPacketListeners);
                    }

                    for (BroadcastPacketListener listener : listeners) {
                        listener.packetReceived(packet.get());
                    }
                }
            }
        });
    }

    @Override
    public void addBroadcastListener(BroadcastPacketListener broadcastPacketListener) {
        synchronized (broadcastPacketListeners) {
            broadcastPacketListeners.add(broadcastPacketListener);
        }

        openListenerThread();
    }

    @Override
    public void removeBroadcastListener(BroadcastPacketListener broadcastPacketListener) {
        synchronized (broadcastPacketListeners) {
            broadcastPacketListeners.add(broadcastPacketListener);
        }
    }

    @Override
    public void sendBroadcastPacket(@NotNull SendingBroadcastPacket broadcastPacket) {
        if (!running.get()) {
            return;
        }

        byte[] data = broadcastPacket.getData();
        DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, multicastPort);
        try {
            // TODO: Probably implement send acknowledge mechanism for better reliability
            multicastSocket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: use abstraction for datagram packet
    private Optional<DatagramPacket> receiveBroadcastPacket() {
        if (!running.get()) {
            return Optional.empty();
        }

        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            this.multicastSocket.receive(packet);
            return Optional.of(packet);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try (UDPBroadcastUtil util = new UDPBroadcastUtil()) {
                util.open();
                util.sendBroadcastPacket("Hello World"::getBytes);
            }
        }).start();

        try (UDPBroadcastUtil util = new UDPBroadcastUtil()) {
            util.open();
            util.addBroadcastListener(p -> System.out.println(new String(p.getData(), 0, p.getLength())));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}