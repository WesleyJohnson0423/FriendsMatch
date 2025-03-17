package com.yanpeng.usercenterback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanpeng.usercenterback.common.BaseResponse;
import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.common.ResultUtils;
import com.yanpeng.usercenterback.exception.BusinessException;
import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.model.request.UserLoginRequest;
import com.yanpeng.usercenterback.model.request.UserRegisterRequest;
import com.yanpeng.usercenterback.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.yanpeng.usercenterback.constant.UserConstant.USER_LOGIN_STATE;
import static com.yanpeng.usercenterback.constant.UserConstant.USER_ROLE_MANAGER;

//@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"},allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword))
        {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount,userPassword))
        {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUserInfo(HttpServletRequest request) {
        Object userCurrent = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userCurrent;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        Long UserId = currentUser.getId();
        User user = userService.getById(UserId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> userSearch(String username,HttpServletRequest request)
    {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR);

        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> list = userService.list(queryWrapper);
        List<User> userList = list.stream().map(user -> {
            user.setUserPassword(null);
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestBody long userId,HttpServletRequest request) {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR);

        }
        if(userId<=0)
        {
            throw new BusinessException(ErrorCode.NULL_ERROR);

        }
        boolean b = userService.removeById(userId);
        return ResultUtils.success(b);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLoginout( HttpServletRequest request) {
        if(request == null)
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        else {
            Integer loginout = userService.loginout(request);
            return ResultUtils.success(loginout);
        }
    }
    private boolean isAdmin(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if(user == null){
            return false;
        }
        else {
            if(user.getUserRole()!=USER_ROLE_MANAGER)
                return false;
        }
        return true;
    }

    @GetMapping("/searchByTags")//required = false注解是表示该参数可以为空，如果这个地方写true，前端不穿参数，就会报错（spring抛的错误），自己的异常处理器处理不到
    public BaseResponse<List<User>> getUserByTags(@RequestParam(required = false ) List<String> tagNameList  ,HttpServletRequest request) {

        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> users = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(users);
    }

    @PostMapping("/updateUser")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        if(user == null || user.getId() < 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请上传修改信息");
        }
        Integer result = userService.updateUser(user,request);
        return ResultUtils.success(result);
    }
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> getRecommendUsers(@RequestParam(required = false,defaultValue = "10") long pageSize, @RequestParam(required = false,defaultValue = "1") long pageNum, HttpServletRequest request) {
        if(pageNum<=0 || pageSize<=0 || request == null || request.getSession().getAttribute(USER_LOGIN_STATE) == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<User> userPage = userService.recommendUser(pageSize, pageNum, request);
        userPage.getRecords().replaceAll(user -> userService.getSafetyUser(user));
        return ResultUtils.success(userPage);
    }
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUserList(long num, HttpServletRequest request){
        if (num <= 0 || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User loginUser = (User) attribute;
        List<User> userList = userService.matchUserList(num, loginUser);
        return ResultUtils.success(userList);
    }

}
