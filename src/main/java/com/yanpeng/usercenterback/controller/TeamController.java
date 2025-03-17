package com.yanpeng.usercenterback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanpeng.usercenterback.common.BaseResponse;
import com.yanpeng.usercenterback.common.ErrorCode;
import com.yanpeng.usercenterback.common.ResultUtils;
import com.yanpeng.usercenterback.exception.BusinessException;
import com.yanpeng.usercenterback.model.domain.Team;
import com.yanpeng.usercenterback.model.domain.User;
import com.yanpeng.usercenterback.model.dto.TeamQuery;
import com.yanpeng.usercenterback.model.request.AddTeamRequest;
import com.yanpeng.usercenterback.model.request.DisbandTeamRequest;
import com.yanpeng.usercenterback.model.request.JoinTeamRequest;
import com.yanpeng.usercenterback.model.request.UpdateTeamRequest;
import com.yanpeng.usercenterback.model.vo.TeamUserVO;
import com.yanpeng.usercenterback.model.vo.UserVO;
import com.yanpeng.usercenterback.service.TeamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yanpeng.usercenterback.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody AddTeamRequest addTeamRequest, HttpServletRequest request){
        if(addTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        Team team = new Team();
        BeanUtils.copyProperties(addTeamRequest,team);
        User userlogin = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        long save = teamService.addTeam(team,userlogin);
        if(save<=0){
            throw new BusinessException(ErrorCode.SYS_ERROR,"保存失败");
        }
        return ResultUtils.success(team.getId());
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody Team team){
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        boolean save = teamService.removeById(team);
        if(!save){
            throw new BusinessException(ErrorCode.SYS_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody UpdateTeamRequest updateTeamRequest){
        if(updateTeamRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        boolean save = teamService.updateteam(updateTeamRequest);
        if(!save){
            throw new BusinessException(ErrorCode.SYS_ERROR,"修改失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/getteam")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"数据不存在");
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamList);
    }
    @GetMapping("/listmy")
    public BaseResponse<List<TeamUserVO>> getmyTeamList(long id){
        if (id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }

        List<TeamUserVO> teamList = teamService.listTeamsmy(id);
        return ResultUtils.success(teamList);
    }
    @GetMapping("/listmycreate")
    public BaseResponse<List<TeamUserVO>> getmycreatTeamList(long id){
        if (id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }

        List<TeamUserVO> teamList = teamService.listcreatemyTeam(id);
        return ResultUtils.success(teamList);
    }


    @GetMapping("/listPage")
    public BaseResponse<Page<Team>> getTeamPage(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        Team team = new Team();
        BeanUtils.copyProperties(team,teamQuery);
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> objectQueryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, objectQueryWrapper);
        return ResultUtils.success(teamPage);
    }

    @PostMapping("/jointeam")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        if(joinTeamRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        User userlogin = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        boolean save = teamService.joinTeam(joinTeamRequest,userlogin);
        return ResultUtils.success(save);
    }

    @PostMapping("/quitteam")
    public BaseResponse<Boolean> quitTeam(@RequestBody DisbandTeamRequest disbandTeamRequest, HttpServletRequest request){
        if (disbandTeamRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        User userlogin = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        boolean save = teamService.quitTeam(disbandTeamRequest,userlogin);
        return ResultUtils.success(save);
    }

}
