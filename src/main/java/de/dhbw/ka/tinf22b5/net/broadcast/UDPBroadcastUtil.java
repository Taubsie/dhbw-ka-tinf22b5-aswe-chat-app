package de.dhbw.ka.tinf22b5.net.broadcast;

import com.google.gson.JsonSyntaxException;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationKey;
import de.dhbw.ka.tinf22b5.configuration.ConfigurationRepository;
import de.dhbw.ka.tinf22b5.configuration.EmptyConfigurationRepository;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.RawReceivedBroadcastPacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.ReceivingBroadcastPacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.ReceivingWelcomePacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.SendingBroadcastPacket;
import de.dhbw.ka.tinf22b5.net.broadcast.packets.data.WelcomeData;
import de.dhbw.ka.tinf22b5.util.GsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class UDPBroadcastUtil implements BroadcastUtil {
    private static final String DEFAULT_BROADCAST_IP = "233.1.33.69";
    private static final int DEFAULT_PORT = 1337;

    private static final int BUFFER_SIZE = 1024;
    private static final int SOCKET_TIMEOUT = 1000; // 1s timeout

    private static final String NI_PING_ADDRESS = "google.com";
    private static final int HTTP_PORT = 80;

    private InetAddress multicastAddress;
    private int multicastPort;
    private NetworkInterface networkInterface;

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
        this.networkInterface = getNetworkInterface();

        try {
            this.multicastSocket = new MulticastSocket(this.multicastPort);
            //this.multicastSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
            this.multicastSocket.setSoTimeout(SOCKET_TIMEOUT);

            this.multicastSocket.joinGroup(new InetSocketAddress(this.multicastAddress, this.multicastPort), networkInterface);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        running.set(true);
    }

    private InetAddress getBroadcastAddress() {
        InetAddress multicastAddress;

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

    private NetworkInterface getNetworkInterface() {
        NetworkInterface networkInterface = getDefaultNetworkInterface();

        try {
            networkInterface = NetworkInterface.getByName(configurationRepository.getConfigurationValue(ConfigurationKey.NETWORK_INTERFACE).orElseThrow());
        } catch (SocketException | NoSuchElementException _) {
        }

        return networkInterface;
    }

    private NetworkInterface getDefaultNetworkInterface() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(NI_PING_ADDRESS);
            socket.connect(address, HTTP_PORT);
            return NetworkInterface.getByInetAddress(socket.getLocalAddress());
        } catch (UnknownHostException | SocketException _) {
        }

        try {
            return NetworkInterface.getByIndex(0);
        } catch (SocketException _) {
        }

        return null;
    }

    @Override
    public synchronized void close() {
        if(!running.get()) {
            return;
        }

        closeListenerThread();

        try {
            this.multicastSocket.leaveGroup(new InetSocketAddress(this.multicastAddress, this.multicastPort), networkInterface);
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
        Thread listenerThread = new Thread(() -> {

            Optional<DatagramPacket> receivedPacket;
            DatagramPacket packet;
            while (!shouldStop.get()) {
                receivedPacket = receiveBroadcastPacket();

                if (receivedPacket.isPresent()) {
                    packet = receivedPacket.get();

                    long senderPID = arrayToLong(packet.getData());
                    long ownPID = ProcessHandle.current().pid();

                    if (addressIsLocalAddress(packet.getAddress()) && senderPID == ownPID)
                        continue;

                    ReceivingBroadcastPacket receivingBroadcastPacket;
                    byte[] rawData = Arrays.copyOfRange(packet.getData(), 8, packet.getLength());

                    try {
                        String data = new String(rawData, StandardCharsets.UTF_8);

                        WelcomeData welcomeData = GsonUtil.getGson().fromJson(data, WelcomeData.class);

                        receivingBroadcastPacket = new ReceivingWelcomePacket(welcomeData, packet.getAddress());
                    } catch (JsonSyntaxException e) {
                        receivingBroadcastPacket = new RawReceivedBroadcastPacket(rawData, packet.getAddress());
                    }

                    Set<BroadcastPacketListener> listeners;
                    synchronized (broadcastPacketListeners) {
                        listeners = new HashSet<>(broadcastPacketListeners);
                    }

                    for (BroadcastPacketListener listener : listeners) {
                        listener.packetReceived(receivingBroadcastPacket);
                    }
                }
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.setName("UDPBroadcastListenerThread");

        return listenerThread;
    }

    private long arrayToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.position(0);
        return buffer.getLong();
    }

    private byte[] longToArray(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(l);
        return buffer.array();
    }

    private boolean addressIsLocalAddress(InetAddress address) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress networkAddress = addresses.nextElement();
                    if (networkAddress.equals(address)) {
                        return true;
                    }
                }
            }

        } catch (SocketException e) {
            return false;
        }

        return false;
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
            broadcastPacketListeners.remove(broadcastPacketListener);
        }
    }

    @Override
    public void sendBroadcastPacket(@NotNull SendingBroadcastPacket broadcastPacket) {
        if (!running.get()) {
            return;
        }

        long processPID = ProcessHandle.current().pid();

        byte[] packetData = broadcastPacket.getData();
        byte[] data = new byte[packetData.length + 8];

        System.arraycopy(longToArray(processPID), 0, data, 0, 8);
        System.arraycopy(packetData, 0, data, 8, packetData.length);

        DatagramPacket packet = new DatagramPacket(data, data.length, multicastAddress, multicastPort);
        try {
            // TODO: Probably implement send acknowledge mechanism for better reliability
            multicastSocket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
                util.attachShutdownHook();
                util.open();
                util.sendBroadcastPacket("Hello World"::getBytes);
            }
        }).start();

        try (UDPBroadcastUtil util = new UDPBroadcastUtil()) {
            util.attachShutdownHook();
            util.open();
            util.addBroadcastListener(p -> System.out.printf("%s: %s: (%s) %s\r\n", Thread.currentThread().getName(), p.getRemoteAddress(), p.getClass().getSimpleName(), new String(p.getData())));

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}