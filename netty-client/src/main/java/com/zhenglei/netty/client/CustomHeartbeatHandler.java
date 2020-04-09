package com.zhenglei.netty.client;

import com.zhenglei.netty.constant.TypeData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 客户端：核心处理业务，用于处理业务请求
 * @Description: 
 * @Date: 2019年3月8日
 * @auther: zhenglei
 */
public abstract class CustomHeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static final Log log = LogFactory.getLog(CustomHeartbeatHandler.class);

	protected String name;

    public CustomHeartbeatHandler(String name) {
        this.name = name;
    }

    /**
     * 1.接收 服务传来的数据，并处理
     * @param ctx 用于传输业务数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	Integer i = Integer.parseInt(msg.toString());
        switch (i){
            case TypeData.PING://心跳包 发送PONG 命令
                sendPongMsg(ctx);
                break;
            case TypeData.PONG:
                System.out.println(name + " get pong msg from " + ctx.channel().remoteAddress());
                break;
            case TypeData.CUSTOME:
            	handleData(ctx, msg);//处理业务逻辑--推送
                break;
        }

    }
    
    /**
     * 1.心跳包，发送PING命令
     * @param context 用于传输业务数据
     */
    protected void sendPingMsg(ChannelHandlerContext context) {
        context.channel().writeAndFlush(TypeData.PING);
    }

    /**
     * 1.心跳包，发送PONG命令
     * @param context 用于传输业务数据
     */
    private void sendPongMsg(ChannelHandlerContext context) {
        context.channel().writeAndFlush(TypeData.PONG);
    }

    /**
     * 1.非心跳包。处理业务逻辑
     * @param channelHandlerContext 用于传输业务数据
     * @param msg 数据
     */
    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, Object msg);

    /**
     * 1.心跳和重连的整个过程：
     * 	1）客户端连接服务端<br>
	 *	2）在客户端的的ChannelPipeline中加入一个比较特殊的IdleStateHandler，设置一下客户端的写空闲时间，例如5s<br>
	 *	3）当客户端的所有ChannelHandler中4s内没有<font color=red>write</font>事件，则会触发userEventTriggered方法<br>
	 *	4）我们在客户端的userEventTriggered中对应的触发事件下发送一个心跳包给服务端，检测服务端是否还存活，防止服务端已经宕机，客户端还不知道<br>
	 *	5）同样，服务端要对心跳包做出响应，其实给客户端最好的回复就是“不回复”，这样可以服务端的压力，假如有10w个空闲Idle的连接，那么服务端光发送心跳回复，则也是费事的事情，那么怎么才能告诉客户端它还活着呢，其实很简单，因为5s服务端都会收到来自客户端的心跳信息，那么如果10秒内收不到，服务端可以认为客户端挂了，可以close链路<br>
	 *	6）加入服务端因为什么因素导致宕机的话，就会关闭所有的链路链接，所以作为客户端要做的事情就是短线重连
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx); //有一段时间没有收到数据
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx); //暂时没有发送数据
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx); //一段时间内没有接收或发送数据
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 1.客户端和服务端<font color=red>连接成功</font>时触发
     * @param ctx 用于传输业务数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("与服务端 " + ctx.channel().remoteAddress() + " 创建链接成功");
    }

    /**
     * 1.客户端和服务端 <font color=red>链接断开</font> 时触发
     * @param ctx 用于传输业务数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	log.error("与服务端 " + ctx.channel().remoteAddress() + " 链接断开");
    }

    /**
     * userEventTriggered 事件
     * @param ctx 用于传输业务数据
     */
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
    	log.info(ctx.channel().remoteAddress()+"读数空闲");
    }

    /**
     * userEventTriggered 事件
     * @param ctx 用于传输业务数据
     */
    protected void handleWriterIdle(ChannelHandlerContext ctx) {
    	log.info(ctx.channel().remoteAddress()+"写入空闲");
    }

    /**
     * userEventTriggered 事件
     * @param ctx 用于传输业务数据
     */
    protected void handleAllIdle(ChannelHandlerContext ctx) {
    	log.info(ctx.channel().remoteAddress()+"读和写空闲");
    }
    
}