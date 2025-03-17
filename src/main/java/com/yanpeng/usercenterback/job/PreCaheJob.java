package com.yanpeng.usercenterback.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.exception.BusinessException;
import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yanpeng.usercenterback.constant.UserConstant.USER_LOGIN_STATE;

@Component
@Slf4j
public class PreCaheJob {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainUserIdList = Arrays.asList(1L,2L,3L,4L,5L,8L,9L,10L);
    //每天11：40执行
    @Scheduled(cron = "0 54 13 ? * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("yanpeng:user:recommend:lock");
        try {
            //trylock就是检验你是否抢到了锁，抢到了就是true，没抢到就是false，，下面是抢到锁后设置的东西
            // 还会给你设置一个其他服务器等待他的时间（第一个参数），
            // 还会给你设置一个过期时间（第二个参数），如果第二个参数是-1（就会执行续期，就是防止方法还没执行完，锁就过期了），第三个参数是单位
            if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
            for(Long mainUserId : mainUserIdList){
                log.info("lock.tryLock成功，执行推荐用户缓存");
                ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
                String redisKey = String.format("yanpeng:user:recommend:%s",mainUserId);
                QueryWrapper<User> objectQueryWrapper = new QueryWrapper<>();
                Page<User> userPage = userService.page(new Page<>(1 , 20),objectQueryWrapper);
                try {
                    opsForValue.set(redisKey,userPage,400000, TimeUnit.MILLISECONDS);//这是毫秒，，这个缓存过期
                }
                catch (Exception e){
                    log.info("redis set key error {}",e);
                }
            }
            }
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYS_ERROR);
        }
        finally {
            //释放锁，写在这里是为了如果运行出错，也会释放锁
            if(lock.isHeldByCurrentThread()){
                log.info("lock.unlock成功");
                lock.unlock();
            }
        }
    }
}
