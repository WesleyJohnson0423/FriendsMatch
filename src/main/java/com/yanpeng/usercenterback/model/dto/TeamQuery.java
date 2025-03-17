package com.yanpeng.usercenterback.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yanpeng.usercenterback.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = true)
public class TeamQuery extends PageRequest {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 创建人Id
     */
    private Long userId;

    /**
     * 队伍状态 0-公开，1-私有，2-加密
     */
    private Integer status;


}
