package com.example.tvgamelibrary;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

// source: https://www.geeksforgeeks.org/how-to-communicate-with-pc-using-android/
// the ClientThread class performs
// the networking operations
class ClientThread implements Runnable {
    private final String ip;
    private final String message;
    private Socket client;
    private PrintWriter printwriter;

    public static final int PORT = 4444;

    ClientThread(String ip, String message) {
        this.ip = ip;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            // the IP and port should be correct to have a connection established
            // Creates a stream socket and connects it to the specified port number on the named host.
            client = new Socket(ip, PORT); // connect to server
            printwriter = new PrintWriter(client.getOutputStream(),true);
            printwriter.write(message); // write the message to output stream
            printwriter.flush();
            printwriter.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}