package com.yanpeng.usercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.exception.BusinessException;
import com.yanpeng.usercenterback.model.domain.Team;
import com.yanpeng.usercenterback.mapper.TeamMapper;
import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.model.domain.UserTeam;
import com.yanpeng.usercenterback.model.dto.TeamQuery;
import com.yanpeng.usercenterback.model.request.DisbandTeamRequest;
import com.yanpeng.usercenterback.model.request.JoinTeamRequest;
import com.yanpeng.usercenterback.model.request.UpdateTeamRequest;
import com.yanpeng.usercenterback.model.vo.TeamUserVO;
import com.yanpeng.usercenterback.model.vo.UserVO;
import com.yanpeng.usercenterback.service.TeamService;
import com.yanpeng.usercenterback.service.UserService;
import com.yanpeng.usercenterback.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author Meng
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2025-01-26 15:34:16
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Autowired
    private TeamMapper teamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)//事务回滚，当该方法的里出异常时，事务会自动回滚
    public long addTeam(Team team, User loginUser){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);//如果传来的参数为空，默认设置为0
        if(maxNum>20 || maxNum<1){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数超出范围");
        }
        if(StringUtils.isBlank(team.getName()) || team.getName().length()>20){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称超出范围");
        }
        if (StringUtils.isBlank(team.getDescription()) || team.getDescription().length()>50){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述超出范围");
        }
        if(team.getStatus()>2 || team.getStatus()<0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态错误");
        }
        if(team.getStatus()==2){
            if(team.getPassword().length()>32 || StringUtils.isBlank(team.getPassword())){
                throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍密码错误");
            }
        }
        if(new Date().after(team.getExpireTime())){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"队伍过期时间错误");
        }
        //todo 如果用户疯狂点创建，可能会反应不过来，就可能创建数量多于五个
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId",loginUser.getId());
        long count = this.count(teamQueryWrapper);
        if(count >= 5){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建5个队伍");
        }
        team.setUserId(loginUser.getId());
        boolean save = this.save(team);//保存完给数据库后，会把team的赋值给后端，所以下面的team.getId()不为空
         if(!save){
             throw  new BusinessException(ErrorCode.SYS_ERROR,"创建失败");
         }
         UserTeam userTeam = new UserTeam();
         userTeam.setUserId(loginUser.getId());
         userTeam.setTeamId(team.getId());
         userTeam.setJoinTime(new Date());
         boolean save1 = userTeamService.save(userTeam);
         if(!save1){
             throw  new BusinessException(ErrorCode.SYS_ERROR,"创建失败");
         }
         return team.getId();
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery) {
        if (teamQuery != null) {
            QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String name = teamQuery.getName();
            if (name != null) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (description != null) {
                queryWrapper.like("description", description);
            }
            Integer status = teamQuery.getStatus();
            if (status != null && status >= 0) {
                queryWrapper.eq("status", status);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
            List<Team> teamList = this.list(queryWrapper);
            if(teamList == null){
                return new ArrayList<>();
            }
            List<TeamUserVO> teamUserVOList = new ArrayList<>();
            TeamUserVO teamUserVO = new TeamUserVO();
            for (Team team : teamList) {
                Long teamId = team.getId();
                teamUserVO = teamMapper.getUserListByTeamId(teamId);
                teamUserVOList.add(teamUserVO);
            }
            return teamUserVOList;
        }
        return null;
    }

    @Override
    public boolean updateteam(UpdateTeamRequest updateTeamRequest) {
        if (updateTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = updateTeamRequest.getId();

        if (id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id错误");
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        if (updateTeamRequest.getStatus()!= 2 ){
            updateTeamRequest.setPassword(null);
        }
        Team team = new Team();
        BeanUtils.copyProperties(updateTeamRequest,team);
        boolean result = this.updateById(team);
        return result;

    }

    @Override
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User userlogin) {
        Long teamId = joinTeamRequest.getTeamId();
        if(teamId <= 0||  teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id错误");
        }
        Team team = this.getById(teamId);
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        if(team.getStatus() == 1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无法加入私密队伍");
        }
        if (team.getExpireTime().before(new Date())){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已过期");
        }
        String password = joinTeamRequest.getPassword();
        if(team.getStatus() == 2 && StringUtils.isBlank(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码为空");
        }
        if(team.getStatus() == 2 && !team.getPassword().equals(password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
        }
        Long userloginId = userlogin.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",userloginId);
        long count = userTeamService.count(userTeamQueryWrapper);
        if(count >= 5){
            throw new BusinessException(ErrorCode.NULL_ERROR,"最多加入5个队伍");
        }

        userTeamQueryWrapper.eq("teamId",teamId);
        count = userTeamService.count(userTeamQueryWrapper);
        if (count>=1){
            throw new BusinessException(ErrorCode.NULL_ERROR,"已经加入该队伍");
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper1 = new QueryWrapper<>();
        userTeamQueryWrapper1.eq("teamId",teamId);
        long teamCount = userTeamService.count(userTeamQueryWrapper1);
        if (teamCount >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已满");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userloginId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        return result;
    }

    @Override
    public boolean quitTeam(DisbandTeamRequest disbandTeamRequest, User userlogin) {
        if (disbandTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        Long teamId = disbandTeamRequest.getTeamId();
        if (teamId == null || teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍id错误");
        }
        Team team = this.getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍不存在");
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        userTeamQueryWrapper.eq("userId",userlogin.getId());
        long count = userTeamService.count(userTeamQueryWrapper);
        if (count == 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"未加入该队伍");
        }
        userTeamQueryWrapper =new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        long userNum = userTeamService.count(userTeamQueryWrapper);
        if (userNum == 1){

            boolean b = userTeamService.remove(userTeamQueryWrapper);
            if (!b){
                throw new BusinessException(ErrorCode.SYS_ERROR,"删除失败");
            }
            boolean result = this.removeById(teamId);
            if (!result){
                throw new BusinessException(ErrorCode.SYS_ERROR,"删除失败");
            }
        }
        else{

            if(userlogin.getId().equals(team.getUserId())){
                if(disbandTeamRequest.getSpecialUserId() == null){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"请指定要替换的队长");
                }
                Team newteam = new Team();
                team.setUserId(disbandTeamRequest.getSpecialUserId());
                team.setId(disbandTeamRequest.getTeamId());
                boolean result = this.updateById(team);
                if (!result){
                    throw new BusinessException(ErrorCode.SYS_ERROR,"删除失败");
                }
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.eq("userId",userlogin.getId());
                boolean b = userTeamService.remove(userTeamQueryWrapper);
                if (!b){
                    throw new BusinessException(ErrorCode.SYS_ERROR,"删除失败");
                }
            }
            else {
                userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.eq("userId",userlogin.getId());
                boolean b = userTeamService.remove(userTeamQueryWrapper);
                if (!b){
                    throw new BusinessException(ErrorCode.SYS_ERROR,"删除失败");
                }
            }
        }
        return true;
    }

    @Override
    public List<TeamUserVO> listTeamsmy(long id) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId",id);
        List<UserTeam> userTeams = userTeamService.list(userTeamQueryWrapper);
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        userTeams.forEach(userTeam -> {
            Long teamId = userTeam.getTeamId();
            TeamUserVO userListByTeamId = teamMapper.getUserListByTeamId(teamId);
            teamUserVOList.add(userListByTeamId);
        });
        return teamUserVOList;
    }

    @Override
    public List<TeamUserVO> listcreatemyTeam(long id) {
        TeamQuery teamQuery = new TeamQuery();
        teamQuery.setUserId(id);
        List<TeamUserVO> teamUserVOList = this.listTeams(teamQuery);
        return teamUserVOList;
    }
}




