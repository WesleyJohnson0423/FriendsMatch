package com.yanpeng.usercenterback.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class DisbandTeamRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -921263170445204270L;

    private Long teamId;

    private Long specialUserId;
}
