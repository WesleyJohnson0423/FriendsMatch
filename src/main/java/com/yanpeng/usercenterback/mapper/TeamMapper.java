package com.yanpeng.usercenterback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanpeng.usercenterback.model.domain.Team;
import com.yanpeng.usercenterback.model.vo.TeamUserVO;
import com.yanpeng.usercenterback.model.vo.UserVO;

import java.util.List;

/**
* @author Meng
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2025-01-26 15:34:16
* @Entity com.yanpeng.usercenterback.model.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {

    public TeamUserVO getUserListByTeamId(Long teamId);

}



