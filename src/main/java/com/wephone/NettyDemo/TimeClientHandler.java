package com.wephone.NettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

public class TimeClientHandler extends ChannelHandlerAdapter {
    private static final Logger logger=Logger.getLogger(TimeClientHandler.class.getName());
    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        byte[] req="查询时间".getBytes();
        firstMessage= Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    //通道连接成功时调用 即链路建立成功时
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("发送查询时间的请求");
        ctx.writeAndFlush(firstMessage);
    }

    //服务器返回 有东西可读时调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf= (ByteBuf) msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.println("当前时间为"+body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /*
         * 连接后关闭服务器端会输出如下
         * 警告: 接收到异常远程主机强迫关闭了一个现有的连接。
         */
        logger.warning("接收到异常"+cause.getMessage());
        ctx.close();
    }
}
