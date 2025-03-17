package com.yanpeng.usercenterback.service;

import com.yanpeng.usercenterback.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 用户服务测试
 *
 * @author yanpeng
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testUserLogin(){
        //输入用户名
        String userAccount = "Test";
        //输入用户密码
        String userPassword = "123456";
        //调用用户登录接口
        User user = userService.userLogin(userAccount, userPassword, null);
        //判断用例是否成功
        Assertions.assertNull(user);
    }//测试不存在

    @Test
    public void testUserLogin2(){
        //输入用户名
        String userAccount = "testUser";
        //输入用户密码
        String userPassword = "1234";
        //调用用户登录接口
        User user = userService.userLogin(userAccount, userPassword, null);
        //判断用例是否成功
        Assertions.assertNotNull(user);
    }//测试存在



    @Test
    void userRegister() {
        String account = "yupi";
        String password = "123456";
        String checkpasswrod = "123456";
        long result = userService.userRegister(account, password, checkpasswrod);
        Assertions.assertEquals(-1,result);

        account="yp";
        result = userService.userRegister(account, password, checkpasswrod);
        Assertions.assertEquals(-1,result);

    }

    @Test
    public void testSearchByTags(){
        List<String> list = Arrays.asList("java","python");
        List<User> users = userService.searchUserByTags(list);
        Assert.assertNotNull(users);
    }
}