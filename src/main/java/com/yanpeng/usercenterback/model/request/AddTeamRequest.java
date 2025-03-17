package com.yanpeng.usercenterback.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class AddTeamRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5007718910086657109L;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

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
     * 创建人Id
     */
    private Long userId;

    /**
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer status;

}
