package com.yanpeng.usercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.exception.BusinessException;
import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.service.UserService;
import com.yanpeng.usercenterback.mapper.UserMapper;
import com.yanpeng.usercenterback.utils.AlgorithmUtils;
import com.yanpeng.usercenterback.utils.Base64ToImage;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void userRegister_ParamsError() {
        String userAccount = "";
        String userPassword = "testpassword";
        String checkPassword = "testpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userRegister(userAccount, userPassword, checkPassword);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userRegister_UserAccountTooShort() {
        String userAccount = "tes";
        String userPassword = "testpassword";
        String checkPassword = "testpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userRegister(userAccount, userPassword, checkPassword);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userRegister_UserPasswordTooShort() {
        String userAccount = "testuser";
        String userPassword = "tes";
        String checkPassword = "tes";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userRegister(userAccount, userPassword, checkPassword);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }


    @Test
    public void userRegister_UserAccountContainsSpecialChars() {
        String userAccount = "test@user";
        String userPassword = "testpassword";
        String checkPassword = "testpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userRegister(userAccount, userPassword, checkPassword);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userRegister_PasswordMismatch() {
        String userAccount = "testuser";
        String userPassword = "testpassword";
        String checkPassword = "differentpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userRegister(userAccount, userPassword, checkPassword);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }


    @Test
    public void userLogin_ParamsError() {
        String userAccount = "";
        String userPassword = "testpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userLogin(userAccount, userPassword, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userLogin_UserAccountTooShort() {
        String userAccount = "tes";
        String userPassword = "testpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userLogin(userAccount, userPassword, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userLogin_UserPasswordTooShort() {
        String userAccount = "testuser";
        String userPassword = "tes";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userLogin(userAccount, userPassword, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userLogin_UserAccountContainsSpecialChars() {
        String userAccount = "test@user";
        String userPassword = "testpassword";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.userLogin(userAccount, userPassword, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void userLogin_UserNotFound() {
        String userAccount = "testuser";
        String userPassword = "testpassword";

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", DigestUtils.md5DigestAsHex(("yanpeng" + "mypassword" + userPassword).getBytes()));
        when(userMapper.selectOne(queryWrapper)).thenReturn(null);

        HttpServletRequest request = new MockHttpServletRequest();
        User result = userService.userLogin(userAccount, userPassword, request);

        assertNull(result);
    }

    @Test
    public void getSafetyUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setUserAccount("testaccount");
        user.setAvatarUrl("http://example.com/avatar.jpg");
        user.setGender(1);
        user.setPhone("1234567890");
        user.setEmail("test@example.com");
        user.setUserStatus(0);
        user.setUserRole(1);
        user.setCreateTime(new Date());
        user.setTags("[\"tag1\", \"tag2\"]");
        user.setProfile("test profile");

        User safetyUser = userService.getSafetyUser(user);

        assertNotNull(safetyUser);
        assertEquals(user.getId(), safetyUser.getId());
        assertEquals(user.getUsername(), safetyUser.getUsername());
        assertEquals(user.getUserAccount(), safetyUser.getUserAccount());
        assertEquals(user.getAvatarUrl(), safetyUser.getAvatarUrl());
        assertEquals(user.getGender(), safetyUser.getGender());
        assertEquals(user.getPhone(), safetyUser.getPhone());
        assertEquals(user.getEmail(), safetyUser.getEmail());
        assertEquals(user.getUserStatus(), safetyUser.getUserStatus());
        assertEquals(user.getUserRole(), safetyUser.getUserRole());
        assertEquals(user.getCreateTime(), safetyUser.getCreateTime());
        assertEquals(user.getTags(), safetyUser.getTags());
        assertEquals(user.getProfile(), safetyUser.getProfile());
    }

    @Test
    public void getSafetyUser_UserNull() {
        User safetyUser = userService.getSafetyUser(null);

        assertNull(safetyUser);
    }


    @Test
    public void searchUserByTags_EmptyTagsList() {
        List<String> tagsList = new ArrayList<>();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.searchUserByTags(tagsList);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void searchUserByTags_NoMatchingUsers() {
        List<String> tagsList = Arrays.asList("tag1", "tag2");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tag : tagsList) {
            queryWrapper.like("tags", tag);
        }

        when(userMapper.selectList(queryWrapper)).thenReturn(new ArrayList<>());

        List<User> result = userService.searchUserByTags(tagsList);

        assertTrue(result.isEmpty());
    }


    @Test
    public void updateUser_ParamsError() {
        User user = new User();
        user.setId(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(user, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.NULL_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void updateUser_UserNotFound() {
        User user = new User();
        user.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(user, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.NULL_ERROR.getCode(), exception.getCode());
    }

    @Test
    public void updateUser_UpdateInfoEmpty() {
        User user = new User();
        user.setId(1L);

        User oldUser = new User();
        oldUser.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(oldUser);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(user, new MockHttpServletRequest());
        });
        assertEquals(ErrorCode.NULL_ERROR.getCode(), exception.getCode());
    }

}