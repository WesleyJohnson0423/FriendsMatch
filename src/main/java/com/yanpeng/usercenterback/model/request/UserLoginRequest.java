package com.yanpeng.usercenterback.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -3891287459677295146L;

    private String userAccount;

    private String userPassword;

}
