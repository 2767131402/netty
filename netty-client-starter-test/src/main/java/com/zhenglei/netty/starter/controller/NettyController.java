package com.zhenglei.netty.starter.controller;

import com.zhenglei.netty.client.Client;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NettyController {
    @Autowired
    private Client client;

    @RequestMapping("/test")
    public void test(){
        Channel channel = client.getChannel();
        channel.writeAndFlush("这是starter的数据");
    }
}
