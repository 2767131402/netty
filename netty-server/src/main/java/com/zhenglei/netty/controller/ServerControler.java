package com.zhenglei.netty.controller;

import com.zhenglei.netty.server.Server;
import io.netty.channel.Channel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ServerControler {

	@RequestMapping("/test")
    public String test() {
        Map<String, Channel> map = Server.getMap();
        Channel channel = map.get("127.0.0.1");
        channel.writeAndFlush("服务端主动向客户端推送");
        return "hello";
    }
}
