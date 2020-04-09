package com.zhenglei.netty.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhenglei.netty.constant.TypeData;
import com.zhenglei.netty.utils.IPUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 服务端：核心处理业务，用于处理业务请求
 */
public abstract class CustomHeartbeatHandler extends ChannelInboundHandlerAdapter {
	private static final Log log = LogFactory.getLog(CustomHeartbeatHandler.class);

	protected String name;

    public CustomHeartbeatHandler(String name) {
        this.name = name;
    }

    /**
     * 服务端用来接收客户端传过来的数据<br>
     * 也可以通过ctx再次将数据传给客户端
     * @param ctx 用于传输业务数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        JSONObject json = JSONObject.parseObject(msg.toString());
        log.info("服务端接收的数据："+json);
        //获取type属性
        int type = json.getInteger(TypeData.MESSAGE_TYPE);
        switch (type){
            case TypeData.PING://PONG
                sendPongMsg(ctx);
                break;
            case TypeData.PONG:
                break;
            case TypeData.CUSTOME:
                handleData(ctx, json.get(TypeData.MESSAGE_DATA));
                break;
        }

    }

    /**
     * 心跳包，发送PING命令
     * @param context 用于传输业务数据
     */
    protected void sendPingMsg(ChannelHandlerContext context) {
        context.channel().writeAndFlush(TypeData.PING);
    }
    
    /**
     * 心跳包，发送PONG命令
     * @param context 用于传输业务数据
     */
    private void sendPongMsg(ChannelHandlerContext context) {
        context.channel().writeAndFlush(TypeData.PONG);
    }

    /**
     * 非心跳包。处理业务逻辑
     * @param channelHandlerContext 用于传输业务数据
     * @param msg 数据
     */
    protected abstract void handleData(ChannelHandlerContext channelHandlerContext, Object msg);

    /**
     * 心跳和重连的整个过程：
     * 	1）客户端连接服务端
	 *	2）在客户端的的ChannelPipeline中加入一个比较特殊的IdleStateHandler，设置一下客户端的写空闲时间，例如5s
	 *	3）当客户端的所有ChannelHandler中4s内没有write事件，则会触发userEventTriggered方法
	 *	4）我们在客户端的userEventTriggered中对应的触发事件下发送一个心跳包给服务端，检测服务端是否还存活，防止服务端已经宕机，客户端还不知道
	 *	5）同样，服务端要对心跳包做出响应，其实给客户端最好的回复就是“不回复”，这样可以服务端的压力，假如有10w个空闲Idle的连接，那么服务端光发送心跳回复，则也是费事的事情，那么怎么才能告诉客户端它还活着呢，其实很简单，因为5s服务端都会收到来自客户端的心跳信息，那么如果10秒内收不到，服务端可以认为客户端挂了，可以close链路
	 *	6）加入服务端因为什么因素导致宕机的话，就会关闭所有的链路链接，所以作为客户端要做的事情就是短线重连
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE://有一段时间没有收到数据
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE://暂时没有发送数据
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE://一段时间内没有接收或发送数据
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 客户端和服务端<font color=red>连接成功</font>时触发
     * @param ctx 用于传输业务数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	//链接成功 向Server中的map保存channel对象
    	Server.getMap().put(IPUtils.getIPString(ctx), ctx.channel());
    	log.info("服务端" + ctx.channel().remoteAddress() + " 链接成功");
    }

    /**
     * 客户端和服务端 <font color=red>连接断开</font> 时触发
     * @param ctx 用于传输业务数据
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	//连接断开 移除channel对象
    	Server.getMap().remove(IPUtils.getIPString(ctx));
        log.error("服务端 " + ctx.channel().remoteAddress() + " 链接断开");
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