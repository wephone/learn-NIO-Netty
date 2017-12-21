package com.wephone.NettyDemo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


public class ChildChannelMain extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //当创建socketChannel成功之后，在进行初始化时，将对应的回调处理类设置到pipeline里
        //客户端连接成功时会调用
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }

    public static void main(String[] args) throws Exception {
        int port=9090;
        new TimeServer().bind(port);
    }
}
