package com.wephone.aio;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 接收成功时的异步接口回调
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServer> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServer attachment) {
        // 接收成功后的操作
        //result The result of the I/O operation.
        //attachment The object to attach to the I/O operation
        //一个是你在回调中需要操作的内容 一个是操作后得到的结果
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
