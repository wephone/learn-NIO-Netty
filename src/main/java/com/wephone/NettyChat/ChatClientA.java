package com.wephone.NettyChat;

import com.wephone.NettyDemo.TimeClientHandler;
import com.wephone.NettyDemo.TimeClientMain;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChatClientA {

    public void connect(int port,String host){
        //配置客户端NIO线程组
        EventLoopGroup group=new NioEventLoopGroup();
        try {
            Bootstrap b=new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //initChannel的作用是 当创建socketChannel成功之后，在进行初始化时，将对应的回调处理类设置到pipeline里，用于处理网络IO事件
                            socketChannel.pipeline().addLast(new ChatClientHandlerA());
                        }
                    });
            //发起异步连接 调用同步方法等待连接成功
            ChannelFuture f=b.connect(host,port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port=9090;
        new ChatClientA().connect(port,"127.0.0.1");
    }

}
