//package com.yanpeng.usercenterback.once;
//import java.util.Date;
//
//import com.yanpeng.usercenterback.mapper.UserMapper;
//import com.yanpeng.usercenterback.model.domain.User;
//import jakarta.annotation.Resource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StopWatch;
//
//@Component
//public class InsertUsers {
//
//    @Resource
//    private UserMapper userMapper;
//
//    public void doInsertUsers() {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        final int INSERT_NUM = 1000;
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUsername("");
//            user.setUserAccount("");
//            user.setAvatarUrl("");
//            user.setGender(0);
//            user.setUserPassword("");
//            user.setPhone("");
//            user.setEmail("");
//            user.setTags("");
//            user.setUserStatus(0);
//            user.setUserRole(0);
//            userMapper.insert(user);
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }
//}
