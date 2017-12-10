package com.wephone.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    //IO多路复用选择器
    private Selector selector;
    //服务端socket通道
    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector=Selector.open();
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            //记得监听接收事件，刚忘加了 导致一直没有响应客户端的发送消息
            serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
            System.out.println("时间服务器在"+port+"端口启动");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop=true;
    }

    public void run() {
        //一直阻塞循环检测selector监听的通道有没有可用的
        while (!stop){
            try {
                //超时时间一秒 一秒后无论如何都会唤醒检测一次
                selector.select(1000);
//                selector.select();
                //当向Selector注册Channel时，register()方法会返回一个SelectionKey对象。表示需要监听的就绪的情况
                Set<SelectionKey> selectionKeys=selector.selectedKeys();
//                System.out.println("可用key数量:"+selectionKeys.size());
                Iterator<SelectionKey> it=selectionKeys.iterator();
                SelectionKey key=null;
                //操作各个监听到的就绪状态的key
                while (it.hasNext()){
                    key=it.next();
                    it.remove();
                    try {
                        handleKey(key);
                    }catch (Exception e){
                        if (key!=null){
                            key.cancel();
                            if (key.channel()!=null){
                                key.channel().close();
                            }
                        }
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //关闭多路复用选择器 所有注册在上面的通道等都会被自动释放 不需要手动关闭
        if (selector!=null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * key是一个关注集合，代表我们关注的注册在选择器上的channel状态
     * 处理得到的可用的key
     * SelectionKey的四个常量来表示：
     * SelectionKey.OP_CONNECT
     * SelectionKey.OP_ACCEPT
     * SelectionKey.OP_READ
     * SelectionKey.OP_WRITE
     * @param key
     */
    private void handleKey(SelectionKey key) throws IOException {
        if (key.isValid()){

            //监听到了接收事件
            if (key.isAcceptable()){
                //key.channel()返回为之创建此键的通道
                ServerSocketChannel serverSocketChannel1= (ServerSocketChannel) key.channel();
                //通过的accept接收客户端的连接请求并创建对象 相当于完成了TCP的三次握手
                SocketChannel socketChannel=serverSocketChannel1.accept();
                //注意别设置成服务端的通道了 前面设置过了 再设置一次变成了java.nio.channels.IllegalBlockingModeException
                socketChannel.configureBlocking(false);
                //向注册器注册这个客户端通道 监听读事件
                socketChannel.register(selector,SelectionKey.OP_READ);
            }
            //监听到了可读事件
            if (key.isReadable()){
                SocketChannel socketChannel= (SocketChannel) key.channel();
                //分配一个Buffer 1024字节
                ByteBuffer readBuffer=ByteBuffer.allocate(1024);
                int readBytes=socketChannel.read(readBuffer);
                if (readBytes>0){
                    //buffer本质上就是一块内存区，可以用来写入数据，并在稍后读取出来。这块内存被NIO Buffer包裹起来，对外提供一系列的读写方便开发的接口。
                    //http://wiki.jikexueyuan.com/project/java-nio-zh/java-nio-buffer.html
                    //flip()方法可以把Buffer从写模式切换到读模式。调用flip方法会把position归零，并设置limit为之前的position的值。 也就是说，现在position代表的是读取位置，limit标示的是已写入的数据位置。
                    readBuffer.flip();
                    byte[] bytes=new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body=new String(bytes,"UTF-8");
                    System.out.println("时间服务器接收到请求:"+body);
                    String currentTime="查询时间".equalsIgnoreCase(body)
                            ? new Date(System.currentTimeMillis()).toString():"请求有误";
                    doWrite(socketChannel,currentTime);
                }else if (readBytes<0){
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    private void doWrite(SocketChannel socketChannel,String response) throws IOException {
        if (response!=null && response.trim().length()>0){
            byte[] bytes=response.getBytes();
            ByteBuffer writeBuffer=ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            //我感觉需要判断下之前是读还是写在flip翻转吧？后来发现不用 他只是换了下position而已 这里写完再flip的 没事
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        }
    }

    /*
     * http://xiachaofeng.iteye.com/blog/1416634
     * 一般在从Buffer读出数据前调用
     * public final Buffer flip() {
     * limit = position;
     * position = 0;
     * mark = -1;
     * return this;
     * }
     */
}
