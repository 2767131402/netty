package com.zhenglei.netty.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.netty.channel.ChannelHandlerContext;

/**
 * Description:
 */
public class ServerHandler extends CustomHeartbeatHandler {
	private static final Log log = LogFactory.getLog(ServerHandler.class);
	
	public ServerHandler() {
        super("server");
    }

	/**
	 * 重写父类的 handleData 对象<br>
	 * 处理业务逻辑:客户端处理完推送后会向服务端发送数据，此方法可以用来判断是否发送成功以及接受返回的数据。
	 */
    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, Object msg) {
    	//测试用的。用来测试输出数据格式
        System.out.println("server 接收业务数据:"+msg);
    }

    /**
     * 读数空闲将连接断开，释放空间
     * 注：客户端可能存在卡或者突然宕机情况导致连接并未断开占用资源。
     */
    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        log.info("服务端长时间未收到客户端 "+ctx.channel().remoteAddress().toString()+" 数据");
        ctx.close();
    }

    /**
         * 异常情况（服务端）<br>
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端出现异常:"+cause);
    }
}