package com.example.net.nio;

public class NioTimeClient {
    public static void main(String[] args) {
        new Thread(new TimeClientHandler("127.0.0.1", 8080)).start();
    }
}
