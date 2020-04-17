package com.zhenglei.netty.starter;

import com.zhenglei.netty.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//开启自动配置
@EnableConfigurationProperties({NettyProperties.class})
@ConditionalOnProperty(prefix = "netty.config", name = "enable", havingValue = "true")
public class NttyAutoConfiguration {

    @Autowired
    private NettyProperties nttyProperties;

    /**
     * 初始化Client
     * @return
     */
    @Bean
    public Client testService(){
        return new Client(this.nttyProperties.getIp(), nttyProperties.getPort());
    }
}
