-- auto-generated definition
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '用户账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '用户性别',
    userPassword varchar(512)                       not null comment '用户密码',
    phone        varchar(128)                       null comment '用户手机号',
    email        varchar(256)                       null comment '用户邮箱',
    userStatus   int      default 0                 not null comment '用户状态 0-正常',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 普通用户-0 管理员-1',
    planetCode   varchar(512)                       null comment '星球编号',
    remark       varchar(1024)                      null comment '备注'
)
    comment '用户表';

