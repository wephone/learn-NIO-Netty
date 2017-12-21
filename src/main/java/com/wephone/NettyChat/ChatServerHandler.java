package com.wephone.NettyChat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ChatServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String client=ctx.channel().remoteAddress().toString();
        System.out.println(client+"已连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf= (ByteBuf) msg;
        byte[] req=new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body=new String(req,"UTF-8");
        System.out.println("收到客户端消息 发给"+body);
        String[] strArr=body.split(",");
        if (strArr[0].equals("B")){
            //发给B的 则存入A的通道
            ChatServer.channelContextMap.put("A",ctx);
        }else {
            ChatServer.channelContextMap.put("B",ctx);
        }
        ByteBuf byteBuf=Unpooled.copiedBuffer("好的 我帮你转发".getBytes());
        ctx.write(byteBuf);
        if (strArr[0].equals("A")){
            ChannelHandlerContext ctxA=ChatServer.channelContextMap.get("A");
            byte[] respMsgByte=strArr[1].getBytes();
            ByteBuf respMsg= Unpooled.buffer(respMsgByte.length);
            respMsg.writeBytes(req);
            ctxA.writeAndFlush(respMsg);
        }else if (strArr[0].equals("B")){
            if (ChatServer.channelContextMap.size()>2) {
                ChannelHandlerContext ctxB = ChatServer.channelContextMap.get("B");
                byte[] respMsgByte = strArr[1].getBytes();
                ByteBuf respMsg = Unpooled.buffer(respMsgByte.length);
                respMsg.writeBytes(req);
                ctxB.writeAndFlush(respMsg);
            }else {
                ByteBuf noMsg=Unpooled.copiedBuffer("B不在线".getBytes());
                ctx.writeAndFlush(noMsg);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
