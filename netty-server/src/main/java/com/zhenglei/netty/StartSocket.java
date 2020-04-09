package com.zhenglei.netty;

import com.zhenglei.netty.server.Server;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动查询 server表。并创建链接
 */
@Component//被spring容器管理
@Order(1)//如果多个自定义ApplicationRunner，用来标明执行顺序
public class StartSocket implements ApplicationRunner {
	
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Server(8888);
    }

}
