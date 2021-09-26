package com.example.net.nio;

public class NioTimeServer {

    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer server = new MultiplexerTimeServer(port);
        new Thread(server, "NIO-MultiplexerTimerServer-001").start();
    }
}
