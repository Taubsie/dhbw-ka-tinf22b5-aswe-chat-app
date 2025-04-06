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
import java.util.Optional;
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

    private final ConfigurationRepository configurationRepository;

    public UDPBroadcastUtil() {
        this(new EmptyConfigurationRepository());
    }

    public UDPBroadcastUtil(@NotNull ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
        this.running = new AtomicBoolean(false);
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

        try {
            this.multicastSocket.leaveGroup(this.multicastAddress);
            this.multicastSocket.close();
        } catch (IOException _) {
            // ignoring closing errors
        }

        running.set(false);
    }

    @Override
    public void addBroadcastListener(BroadcastPacketListener broadcastPacketListener) {

    }

    @Override
    public void removeBroadcastListener(BroadcastPacketListener broadcastPacketListener) {

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

    @Override
    public Optional<DatagramPacket> receiveBroadcastPacket() {
        if(!running.get()) {
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
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try (UDPBroadcastUtil util = new UDPBroadcastUtil()) {
                util.open();
                util.sendBroadcastPacket("Hello World"::getBytes);
            }
        }).start();

        try (UDPBroadcastUtil util = new UDPBroadcastUtil()) {
            util.open();
            Optional<DatagramPacket> packet = util.receiveBroadcastPacket();
            if (packet.isPresent()) {
                DatagramPacket datagramPacket = packet.get();
                System.out.printf("%s: %s\r\n", datagramPacket.getAddress(), new String(datagramPacket.getData()));
            } else {
                System.out.println("empty packet");
            }
        }
    }
}