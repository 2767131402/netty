package com.zhenglei.netty;

import com.zhenglei.netty.client.Client;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component//被spring容器管理
@Order(1)//如果多个自定义ApplicationRunner，用来标明执行顺序
public class StartSocket implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Client("127.0.0.1", 8888);
    }

}
