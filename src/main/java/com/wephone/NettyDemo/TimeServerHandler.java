package com.wephone.NettyDemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/*
 * 各个事件的相应回调
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws UnsupportedEncodingException {
        ByteBuf buf= (ByteBuf) msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.println("时间服务器收到请求指令："+body);
        String currentTime="查询时间".equalsIgnoreCase(body)
                ? new Date(System.currentTimeMillis()).toString():"请求有误";
        ByteBuf resp= Unpooled.copiedBuffer(currentTime.getBytes());
        //netty的write方法并没有直接写入通道(为避免多次唤醒多路复用选择器)
        //而是把待发送的消息放到缓冲数组中，flush方法再全部写到通道中
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//这句的作用是将消息发送队列中的消息写入到SocketChannel中发给对方
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
