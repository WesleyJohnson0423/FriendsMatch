package com.yanpeng.usercenterback.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanpeng.usercenterback.model.domain.Team;
import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.model.dto.TeamQuery;
import com.yanpeng.usercenterback.model.request.DisbandTeamRequest;
import com.yanpeng.usercenterback.model.request.JoinTeamRequest;
import com.yanpeng.usercenterback.model.request.UpdateTeamRequest;
import com.yanpeng.usercenterback.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Meng
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-01-26 15:34:16
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);

    List<TeamUserVO> listTeams(TeamQuery teamQuery);

    boolean updateteam(UpdateTeamRequest updateTeamRequest);

    boolean joinTeam(JoinTeamRequest joinTeamRequest, User userlogin);

    boolean quitTeam(DisbandTeamRequest disbandTeamRequest, User userlogin);

    List<TeamUserVO> listTeamsmy(long id);

    List<TeamUserVO> listcreatemyTeam(long id);
}
