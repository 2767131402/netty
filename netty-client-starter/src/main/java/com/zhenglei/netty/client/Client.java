package com.zhenglei.netty.client;

import java.util.concurrent.TimeUnit;

import com.zhenglei.netty.code.MsgPackDecode;
import com.zhenglei.netty.code.MsgPackEncode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 创建客户端
 */
public class Client {
	private static final Log log = LogFactory.getLog(Client.class);
	
	private final String host;//IP地址
	private final int port;//端口
	
    private NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
    private Channel channel;
    private Bootstrap bootstrap;

    public Client(String host, int port) {
    	this.host = host;
		this.port = port;
        start();
    }

    public void start() {
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(0, 0, 5));//30s服务器无任何操作则发送心跳包
                            p.addLast(new MsgPackDecode());
                            p.addLast(new MsgPackEncode());
                            p.addLast(new ClientHandler(Client.this));
                        }
                    });
            doConnect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 1.重连机制,每隔2s重新连接一次服务器
     */
    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }

        ChannelFuture future = bootstrap.connect(host, port);

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    log.info(host+":"+port+" 链接成功");
                } else {
                	log.error(host+":"+port+" 链接失败，正在重连....");
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 10, TimeUnit.SECONDS);
                }
            }
        });
    }

    public Channel getChannel() {
        return channel;
    }
}
