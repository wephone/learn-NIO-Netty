package com.wephone.nettyDemo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


public class ChildChannelMain extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }

    public static void main(String[] args) throws Exception {
        int port=9090;
        new TimeServer().bind(port);
    }
}
