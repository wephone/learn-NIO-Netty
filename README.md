# learn-NIO-Netty
## Linux五种IO模型
一次IO分为两个步骤,1.等待数据报备2.将数据从内核复制到用户空间
- 阻塞IO:步骤1,2都阻塞
- 非阻塞IO:步骤1非阻塞,轮询检查内核数据,步骤2阻塞
- 多路复用IO:步骤1,2都阻塞,可同时监听多个IO操作
- 信号驱动式IO:步骤1非阻塞,当数据已经报备好，一个SIGIO信号传送给我们的进程告诉我们数据准备好了，然后进程开始步骤2,步骤2阻塞
- 异步IO:步骤1,2都非阻塞
## NIO
- 非阻塞IO
- 通过管道channel和缓冲区buffer来进行传输
- 通过选择器selector来进行IO多路复用
- 将管道channel注册到selector上，并关联对应key作为他感兴趣的事件
- 服务端和客户端各自创建一个线程，在这个线程里不断轮询，检查是否有感兴趣的key返回了
- 这样服务端端只需要一个线程就可以完成一个服务端监听多个客户端的效果
- 检测到返回想要的key,例如读写，连接，接收事件时 做出相应的响应
- buffer的flip可以从写模式变为读模式
- 向管道read/write buffer实现读写效果
## AIO
- 异步IO
- 同样使用channel和buffer
- 使用接口回调的方式实现异步IO
- CompletionHandler泛型有两个参数 
- V result和A attachment
- 为带上的附件，再回调completed等时会带上这个附件
- result注释里 The result of the I/O operation.
- 回调接口也会返回这个参数
- 我理解为返回IO操作的结果 如Integer AsynchronousSocketChannel等

## Netty
- 同样是用channel和buffer进行读写
- 配置线程组
- 同样使用回调来处理相应的事件 例如channelRead等
- 启动辅助类的childHandler,handler方法也是设置回调 在在进行初始化时，将对应的回调处理类设置到pipeline里，用于处理网络IO事件
- socketChannel.pipeline().addLast();可以添加回调对象(处理channelRead等)和编码器
- 不需要另开线程进行轮询监听事件
- netty框架的nio操作比元素nio简便很多

## TCP粘包拆包
- TCP底层不了解上层业务数据,会根据TCP缓冲区的实际情况进行包的划分
- 会导致一个大数据包被分为多个接收到，或者多个小包被合成一起接收到，或包含了其他数据包的部分信息等等，称为读写半包
- 可在应用层通过报文固定长不够补空格,包尾加换行符分割,或将消息分为消息头消息体，消息头标明报文长度等来解决
- netty可通过在initChannel里pipeline().addLast设置LineBasedFrameDecoder和StringDecoder等其他分隔符编码器来解决
- LineBasedFrameDecoder就是判断"\n" 如果有 则以此作为结束位置，从可读索引到结束位置就组成了一行(一个完整包)
- StringDecoder就是将接收到的对象转换为字符串,例如在channelRead回调里把ByteBuf自动转换为String再回调给用户
- LineBasedFrameDecoder+StringDecoder组合就是按行切换的文本解码器
- DelimiterBasedFrameDecoder 自动完成以分隔符做结束标志的消息的解码(仍需设置单条消息最大长度，达到该长度后仍然没有查找到分隔符时就抛出异常)
- FixedLengthFrameDecoder自动完成对定长消息的解码