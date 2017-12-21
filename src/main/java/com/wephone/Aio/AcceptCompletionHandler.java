package com.wephone.Aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 接收成功时的异步接口回调
 * JDK底层通过线程池ThreadPoolExecutor来执行回调通知，
 * 异步回调通知类由sun.Nio.ch.AsynchronousChannelGroupImpl实现，
 * 它经过层层调用，最终回调com.phei.netty.Aio.AsyncTimeClientHandler$1.completed方法，完成回调通知。
 * 异步Socket Channel是被动执行对象，我们不需要像NIO编程那样创建一个独立的I/O线程来处理读写操作。
 * 对于AsynchronousServerSocketChannel和AsynchronousSocketChannel它们都由JDK底层的线程池负责回调并驱动读写操作。
 * 正因为如此，基于NIO 2.0新的异步非阻塞Channel进行编程比NIO编程更为简单。
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServer> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServer attachment) {
        // 接收成功后的操作
        //result The result of the I/O operation.
        //attachment The object to attach to the I/O operation
        //一个是前面带入的附件 一个是操作后得到的结果
        // 获取目标对象通道  再次调用接收方法 让服务端继续接收其他客户端的请求
        attachment.serverSocketChannel.accept(attachment,this);
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        // 第一个buffer 接收缓冲区  用于从异步通道读取数据
        // 第二个buffer 异步通道携带的附件 通知回调时作为入参使用
        //链路建立成功 读取客户端的请求消息
        result.read(buffer,buffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServer attachment) {
        attachment.countDownLatch.countDown();
    }
}
