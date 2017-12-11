# learn-NIO-Netty
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
- 同样使用channel和buffer
- 使用接口回调的方式实现异步IO
- CompletionHandler泛型有两个参数 
- V result和A attachment
- 为带上的附件，再回调completed等时会带上这个附件
- result注释里 The result of the I/O operation.
- 回调接口也会返回这个参数
- 我理解为返回IO操作的结果 如Integer AsynchronousSocketChannel等
