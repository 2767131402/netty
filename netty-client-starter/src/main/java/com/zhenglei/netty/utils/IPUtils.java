package com.zhenglei.netty.utils;

import io.netty.channel.ChannelHandlerContext;

public class IPUtils {
    /**
     * 1.根据ctx获取IP
     * @param ctx 用于传输业务数据
     */
    public static String getIPString(ChannelHandlerContext ctx){
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }
}
