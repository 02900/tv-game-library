package com.example.tvgamelibrary;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class WakeOnLANThread implements Runnable {
    private final String broadcast;
    private final String mac;
    public static final int PORT = 9;

    WakeOnLANThread(String broadcast, String mac) {
        this.broadcast = broadcast;
        this.mac = mac;
    }

    @Override
    public void run() {
        sendMagicPackage(broadcast, mac);
    }

    public void sendMagicPackage(String broadcast, String mac) {
        // sendEvent("Usage: java WakeOnLan <broadcast-ip> <mac-address>");
        // sendEvent("Example: java WakeOnLan 192.168.0.255 00:0D:61:08:22:4A");

        try {
            byte[] macBytes = getMacBytes(mac);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(broadcast);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
        }
        catch (Exception e) {
            System.exit(1);
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }
}
