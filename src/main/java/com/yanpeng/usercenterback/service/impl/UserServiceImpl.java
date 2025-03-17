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
import java.util.Base64;

import com.yanpeng.usercenterback.utils.Base64ToImage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yanpeng.usercenterback.constant.UserConstant.USER_LOGIN_STATE;

/**
 *
 * 用户实现类
* @author Meng
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-11-27 20:45:36
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值：混淆密码
     */
    public static final String SALT = "yanpeng";

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private FileStorageService fileStorageService;//注入实列

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if(userPassword.length()<4||checkPassword.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount);//前一个是数据库里的字段名,后面是自己的参数
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_REPEAT,"用户重复");

        }
        //校验账户包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"包含特殊字符");
        }

        //密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不相同");
        }

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex(( SALT + "mypassword"+userPassword).getBytes());

        //向用户数据库中插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYS_ERROR);

        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号小于四位");

        }
        if(userPassword.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码小于四位");

        }

        //校验账户包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex(( SALT + "mypassword"+userPassword).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount)//前一个是数据库里的字段名,后面是自己的参数
        .eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed");
//            throw new BusinessException(ErrorCode.NULL_ERROR,"未找到该用户");
            System.out.println("未找到该用户");

        }
        //脱敏
        User safetyuser = getSafetyUser(user);

//        //记录用户的登录态
//        log.info("sessionid{}",request.getSession().getId());
//        request.getSession().setAttribute(USER_LOGIN_STATE, safetyuser);


        return safetyuser;
    }

    public User getSafetyUser(User user){
        if(user == null)
        {
            return null;
        }
        User safetyuser = new User();
        safetyuser.setId(user.getId());
        safetyuser.setUsername(user.getUsername());
        safetyuser.setUserAccount(user.getUserAccount());
        safetyuser.setAvatarUrl(user.getAvatarUrl());
        safetyuser.setGender(user.getGender());
        safetyuser.setPhone(user.getPhone());
        safetyuser.setEmail(user.getEmail());
        safetyuser.setUserStatus(user.getUserStatus());
        safetyuser.setUserRole(user.getUserRole());
        safetyuser.setCreateTime(user.getCreateTime());
        safetyuser.setTags(user.getTags());
        safetyuser.setProfile(user.getProfile());
        return safetyuser;
    }

    @Override
    public Integer loginout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUserByTags(List<String> tagsList){
        if (CollectionUtils.isEmpty(tagsList))
        {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();

        for (String tag : tagsList) {
            queryWrapper = queryWrapper.like("tags",tag);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        log.info("user search tags success {}",users);

        return  users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)//事务回滚，当该方法的里出异常时，事务会自动回滚
    public Integer updateUser(User user,HttpServletRequest request) {
        Long id = user.getId();
        User oldUser = userMapper.selectById(id);
        if(oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"暂无改用户");
        }
        if(user.getGender()== null && user.getAvatarUrl()== null && user.getUsername()== null && user.getPhone()== null && user.getTags()== null&&user.getEmail()==null ){
            throw new BusinessException(ErrorCode.NULL_ERROR,"修改信息为空");
        }
        if (user.getAvatarUrl()!=null){
//            Base64ToImage base64ToImage = new Base64ToImage();
            String objname = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"/";
            String objurl = user.getAvatarUrl();
            objurl = objurl.substring(objurl.indexOf(",", 1) + 1);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] imgbytes = decoder.decode(objurl);
            FileInfo fileInfo = fileStorageService.of(imgbytes)
                    .setPath(objname).upload();//保存到相对路径下，为了方便管理，不需要可以不写
//
//            String url = base64ToImage.generateImage(user.getAvatarUrl());
            String url = fileInfo.getUrl();
            if(url == null) {
                throw new BusinessException(ErrorCode.SYS_ERROR,"图片上传失败");
            }
            log.info("图片地址{}", url);
            user.setAvatarUrl(url);
        }
        int i = userMapper.updateById(user);
        if(i==1){
            request.getSession().setAttribute(USER_LOGIN_STATE,userMapper.selectById(id));
        }
        return i;
    }

    public Page<User> recommendUser(long pageSize,long pageNum,HttpServletRequest request){
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        String redisKey = String.format("yanpeng:user:recommend:%s",loginUser.getId());
        Page<User> value = (Page<User>)opsForValue.get(redisKey);
        if(value!=null){
            return value;
        }
        QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
        Page<User> userPage = this.page(new Page<>(pageNum , pageSize),objectQueryWrapper);
       try {
           opsForValue.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);//这是毫秒，所以就是30秒，这个缓存过期
       }
       catch (Exception e){
           log.info("redis set key error {}",e);
       }
        return userPage;
    }

    @Override
    public List<User> matchUserList(long num, User loginUser) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.isNotNull("tags");//查询tags不为空的字段
        List<User> userList = this.list(userQueryWrapper);
        String tags = loginUser.getTags();
        if(tags == null || tags.length() == 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请设置的自己的标签");
        }
        Gson gson = new Gson();
        List<String> tagsList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        Map<Integer, User> userMap = new TreeMap<Integer, User>(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer obj1, Integer obj2) {
                        // 降序排序
                        return obj1.compareTo(obj2);
                    }
                });
        for (int i =0 ; i < userList.size();i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
                if(StringUtils.isBlank(userTags) || userTags.charAt(1) == ']' || user.getId() .equals(loginUser.getId()) ){
                continue;
            }
            List<String> userTagsList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            int similar = AlgorithmUtils.minDistance(tagsList, userTagsList);
            User safetyUser = getSafetyUser(user);
            userMap.put(similar, safetyUser);
        }
        List<User> similarUserList = new ArrayList<>();
        userMap.entrySet().stream().limit(num).forEach(entry -> {
            similarUserList.add(entry.getValue());
        });
        return similarUserList;
    }
}




