package de.dhbw.ka.tinf22b5.util;

import java.io.IOException;
import java.net.*;

public class ConnectionUtil {
    private static final String MULTICAST_IP = "233.1.33.69";
    private final InetAddress group;
    private final int port = 1337;

    protected MulticastSocket receiveSocket;
    protected DatagramSocket sendSocket;
    protected byte[] buffer = new byte[256];

    public ConnectionUtil() {
        try {
            group = InetAddress.getByName(MULTICAST_IP);

            receiveSocket = new MulticastSocket(port);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void listen() {
        try {
            receiveSocket.joinGroup(group);
            while(true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(packet);
                String received = new String(
                        packet.getData(), 0, packet.getLength());
                if ("end".equals(received)) {
                    break;
                }
                System.out.println(received);
            }
            receiveSocket.leaveGroup(group);
            receiveSocket.close();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public void send(String message) {
        try {
            sendSocket = new DatagramSocket();
            buffer = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
            sendSocket.setBroadcast(true);
            //sendSocket.joinGroup(new InetSocketAddress(group, port), NetworkInterface.getByName("wireless_32768"));
            sendSocket.send(packet);
            //sendSocket.leaveGroup(new InetSocketAddress(group, port), NetworkInterface.getByName("wireless_32768"));
            sendSocket.close();
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ConnectionUtil util = new ConnectionUtil();
            util.send("Hello world!");
        }).start();

        ConnectionUtil util = new ConnectionUtil();
        util.listen();
    }
}