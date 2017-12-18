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
