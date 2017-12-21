package com.wephone.Aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServer {

    private int port;
    // 闭锁 主要用于让一个主线程等待一组事件发生后继续执行
    // 本例中用于让线程阻塞 防止完成任务后退出
    CountDownLatch countDownLatch;
    AsynchronousServerSocketChannel serverSocketChannel;

    public AsyncTimeServer(int port) {
        this.port = port;
        try {
            serverSocketChannel=AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("异步时间服务器在"+port+"端口启动");
        } catch (IOException e) {
            e.printStackTrace();
        }
        countDownLatch=new CountDownLatch(1);
    }

    public void run(){
        //accept方法的回调函数必须第一个泛型为AsynchronousSocketChannel
        serverSocketChannel.accept(this,new AcceptCompletionHandler());
        //调用await方法的时候，如果计数不为0，会阻塞自己的线程
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
