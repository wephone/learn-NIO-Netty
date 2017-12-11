package com.wephone.aio;

public class AsyncTimeServerMain {
    public static void main(String[] args) {
        int port = 9090;
        new AsyncTimeServer(port).run();
    }
}
