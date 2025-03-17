package com.yanpeng.usercenterback;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableFileStorage
@EnableRedisHttpSession
@SpringBootApplication
@MapperScan("com.yanpeng.usercenterback.mapper")
@EnableScheduling//开启定时任务
public class UserCenterBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterBackApplication.class, args);
    }

}
