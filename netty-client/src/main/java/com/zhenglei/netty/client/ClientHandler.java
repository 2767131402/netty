package com.zhenglei.netty.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.netty.channel.ChannelHandlerContext;

/**
 * Description:
 */
@SuppressWarnings("unchecked")
public class ClientHandler extends CustomHeartbeatHandler {
    private static final Log log = LogFactory.getLog(ClientHandler.class);

    private Client client;

    public ClientHandler(Client client) {
        super("client");
        this.client = client;
    }


    /**
     * 真正处理业务逻辑的类，父类只是抽象类
     */
    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, Object msg) {
        System.out.println("客户端收到服务端的业务数据："+msg);
        channelHandlerContext.channel().writeAndFlush("客户端接收数据成功");
    }

    /**
     * 服务端和客户端都空闲时  客户端发送PING 心跳包
     *
     * @param ctx 上下文对象
     */
    @Override
    protected void handleAllIdle(ChannelHandlerContext ctx) {
        super.handleAllIdle(ctx);
        //客户端 发送PING 心跳包
        sendPingMsg(ctx);
    }

    /**
     * 客户端与服务端 链接断开时<br>
     * 客户端重连接服务端
     *
     * @param ctx 上下文对象
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //重连
        client.doConnect();
    }

    /**
     * 1.客户端异常，属于客户管抛出的异常，与服务端无关。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端 " + ctx.channel().remoteAddress() + " 异常：" + cause);
    }

}