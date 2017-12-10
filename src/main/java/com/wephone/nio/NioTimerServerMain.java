package com.wephone.nio;

public class NioTimerServerMain {

    public static void main(String[] args){
        int port=9090;
        MultiplexerTimeServer multiplexerTimeServer=new MultiplexerTimeServer(port);
        //单独开一个线程 轮询多路复用
        new Thread(multiplexerTimeServer,"NIO-MultiplexerTimeServer-001").start();
    }

}
