package com.yanpeng.usercenterback.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 9044202748962596583L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
