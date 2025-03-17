-- auto-generated definition
create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '队伍最大人数',
    password    varchar(512)                       null comment '密码',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             null comment '创建人Id',
    status      int      default 0                 not null comment '队伍状态 0-公开，1-私有，2-加密',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';

-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null,
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '用户状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色    0-普通用户，1-管理员用户',
    tags         varchar(1024)                      null comment '标签列表json',
    profile      varchar(512)                       null comment '个人简介'
)
    comment '用户';

-- auto-generated definition
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '创建人Id',
    teamId     bigint                             null comment '队伍Id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';

