package com.zhenglei.netty.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zhenglei.netty.code.MsgPackDecode;
import com.zhenglei.netty.code.MsgPackEncode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 创建链接
 */
public class Server {
	private static final Log log = LogFactory.getLog(Server.class);
	private final int port;// 端口
	private static Map<String, Channel> map = new ConcurrentHashMap<String, Channel>();//保存 Channel 对象。key:客户端的IP，value:Channel对象

	public Server(int port) {
		this.port = port;
		start();
	}
	

	private void start() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //日志级别
                    //.handler(new LoggingHandler(LogLevel.INFO))
			
                    //允许启动一个监听服务器并捆绑其众所周知端口
                    .option(ChannelOption.SO_REUSEADDR, true)
                    // 连接数
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    // 长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 不延迟，消息立即发送
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 连接超时时间
                    .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(10, 0, 0));
                            p.addLast(new MsgPackDecode());
                            p.addLast(new MsgPackEncode());
                            p.addLast(new ServerHandler());
                        }
                    });

			//监听本地端口,同步等待监听结果
            Channel channel = bootstrap.bind(port).sync().channel();

            log.info("Netty 端口 "+port+"启动成功");
			//等待服务端监听端口关闭,优雅退出
            channel.closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
	}
	
	public static Map<String, Channel> getMap() {
		return map;
	}
 
}