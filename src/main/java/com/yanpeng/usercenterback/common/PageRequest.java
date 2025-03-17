package com.yanpeng.usercenterback.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1474244332289620976L;

    protected Integer pageNum;

    protected Integer pageSize;
}
