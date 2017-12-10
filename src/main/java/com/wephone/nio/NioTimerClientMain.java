package com.wephone.nio;

public class NioTimerClientMain {
    public static void main(String[] args){
        new Thread(new MultiplexerTimeClient("127.0.0.1",9090),"time-client-001").start();
        new Thread(new MultiplexerTimeClient("127.0.0.1",9090),"time-client-002").start();
    }
}
