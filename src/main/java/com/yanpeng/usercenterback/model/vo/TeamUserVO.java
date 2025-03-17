package com.yanpeng.usercenterback.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类
 */
@Data
public class TeamUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2723997639716455135L;
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
     * 队伍最大人数
     */
    private Integer maxNum;



    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建人Id
     */
    private Long userId;

    /**
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 入队的人员列表
     */
    private List<UserVO> userVOList;

}
