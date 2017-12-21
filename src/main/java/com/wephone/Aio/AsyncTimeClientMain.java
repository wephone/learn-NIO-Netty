package com.wephone.Aio;

public class AsyncTimeClientMain {
    public static void main(String[] args){
        int port=9090;
        new Thread(new AsyncTimeClient(port),"time-client-001").start();
        new Thread(new AsyncTimeClient(port),"time-client-002").start();
    }
}
