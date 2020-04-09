package com.zhenglei.netty.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * MessagePack解码
 */
public class MsgPackDecode extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		final int length = msg.readableBytes();
		final byte[] array = new byte[length];
		// 从数据包msg中获取要操作的byte数组
		msg.getBytes(msg.readerIndex(), array, 0, length);
		// 将array反序列化成对象,并添加到解码列表中
		MessagePack msgpack = new MessagePack();
		out.add(msgpack.read(array));
	}

}