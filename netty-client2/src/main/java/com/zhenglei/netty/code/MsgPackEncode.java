package com.zhenglei.netty.code;

import com.alibaba.fastjson.JSONObject;
import com.zhenglei.netty.constant.TypeData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * MessagePack编码
 */
public class MsgPackEncode extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        JSONObject jsonObject = new JSONObject();
        //获取消息的长度
        int length = msg.toString().getBytes().length;
        if(length<=1){ //消息长度小于1，说明是 PING/PONG 心跳
            jsonObject.put(TypeData.MESSAGE_TYPE,msg);
            jsonObject.put(TypeData.MESSAGE_DATA,null);
        }else{//否则为 业务数据
            jsonObject.put(TypeData.MESSAGE_TYPE,TypeData.CUSTOME);
            jsonObject.put(TypeData.MESSAGE_DATA,msg);
        }
        MessagePack msgPack = new MessagePack();
        byte[] raw = msgPack.write(jsonObject);
        out.writeBytes(raw);
    }
}