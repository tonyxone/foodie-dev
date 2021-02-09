package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan(basePackages = "com.imooc.mapper")
@ComponentScan(basePackages = {
        "com.imooc",
        "org.n3r.idworker"
})
@EnableScheduling //开始定时任务
@EnableRedisHttpSession //开启使用redis作为spring session
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
