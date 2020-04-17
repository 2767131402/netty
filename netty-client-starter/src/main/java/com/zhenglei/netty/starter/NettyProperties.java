package com.zhenglei.netty.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 描述：配置信息 实体
 **/
@ConfigurationProperties(prefix = "netty.server")
public class NettyProperties {
    private String ip;
    private Integer port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}