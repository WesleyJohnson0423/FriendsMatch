package com.yanpeng.usercenterback;


import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class UserCenterBackApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(UserCenterBackApplicationTests.class);
    @Resource
    UserService userService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    RedissonClient redissonClient;


    @Test//单线程插入数据
    void digesttest() throws NoSuchAlgorithmException {
        StopWatch stopWatch = new StopWatch();
        List<User> userList = new ArrayList<>();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("spring");
            user.setUserAccount("spring");
            user.setAvatarUrl("https://ts3.cn.mm.bing.net/th?id=OIP-C.nkWmM-lReaN8kH-ieXmZrQHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2");
            user.setGender(0);
            user.setUserPassword("1234");
            user.setPhone("142132");
            user.setEmail("42141");
            user.setTags("[]");
            user.setUserStatus(1);
            user.setUserRole(0);
            userList.add(user);
        }
        userService.saveBatch(userList, 100);//批量插入，每次插入100条数据

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
    @Test//多线程插入数据
    void multitest() throws NoSuchAlgorithmException {
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 100, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int bathsize = 25;
        int j = 0;
        List<CompletableFuture<Void>> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            List<User> userList = new ArrayList<>();

            while(true) {

                j++;
                User user = new User();
                user.setUsername("spring");
                user.setUserAccount("spring");
                user.setAvatarUrl("https://ts3.cn.mm.bing.net/th?id=OIP-C.nkWmM-lReaN8kH-ieXmZrQHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2");
                user.setGender(0);
                user.setUserPassword("1234");
                user.setPhone("142132");
                user.setEmail("42141");
                user.setTags("[]");
                user.setUserStatus(1);
                user.setUserRole(0);
                userList.add(user);
                if(j % bathsize == 0)
                    break;
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, bathsize);
            });
            list.add(future);
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    void redistest(){
        ValueOperations stringObjectValueOperations = redisTemplate.opsForValue();
        stringObjectValueOperations.set("yanpeng","yanpeng");
        stringObjectValueOperations.set("hahah",1);
        User user = new User();
        user.setUsername("spring");
        user.setUserAccount("spring");
        user.setAvatarUrl("https://ts3.cn.mm.bing.net/th?id=OIP-C.nkWmM-lReaN8kH-ieXmZrQHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2");
        user.setGender(0);
        user.setUserPassword("1234");
        user.setPhone("142132");
        user.setEmail("42141");
        user.setTags("[]");
        user.setUserStatus(1);
        user.setUserRole(0);
        stringObjectValueOperations.set("user",user);
        String  o = (String) stringObjectValueOperations.get("yanpeng");
        System.out.println(o);
        Object o1 = stringObjectValueOperations.get("hahah");
        System.out.println(o1);
        Object o2 = stringObjectValueOperations.get("user");
        System.out.println(o2);
    }

    @Test
    void redissontest(){
        RList<Object> test = redissonClient.getList("test");
        test.add("yanpeng");
        Object o = test.get(0);
        log.info("ass{}",o);
    }

}
