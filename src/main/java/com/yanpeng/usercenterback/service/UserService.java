package com.yanpeng.usercenterback.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanpeng.usercenterback.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


/**
* @author Meng
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-11-27 20:45:36
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求域
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user 原始用户数据
     * @return 脱敏后的信息
     */
    User getSafetyUser(User user);

    Integer loginout(HttpServletRequest request);

    List<User> searchUserByTags(List<String> tags);

    Integer updateUser(User user,HttpServletRequest request);;

    Page<User> recommendUser(long pageSize, long pageNum,HttpServletRequest request);

    List<User> matchUserList(long num, User loginUser);
}
