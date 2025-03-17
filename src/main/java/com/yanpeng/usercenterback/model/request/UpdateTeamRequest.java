package com.yanpeng.usercenterback.model.request;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 修改队伍信息类
 * @TableName team
 */
@Data
public class UpdateTeamRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 4434693924187235251L;
    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    /**
     * 密码
     */
    private String password;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    /**
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer status;

}