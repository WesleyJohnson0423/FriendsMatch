package com.yanpeng.usercenterback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanpeng.usercenterback.model.domain.UserTeam;
import com.yanpeng.usercenterback.mapper.UserTeamMapper;
import com.yanpeng.usercenterback.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author Meng
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2025-01-26 15:35:31
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




