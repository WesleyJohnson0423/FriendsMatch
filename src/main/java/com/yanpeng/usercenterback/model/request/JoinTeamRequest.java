package com.yanpeng.usercenterback.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class JoinTeamRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5236757865744340081L;

    private Long teamId;

    private String password;
}
