package com.wjp.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author wjp
 */
@Data
public class UserLoginRequest implements Serializable {
    // 序列化ID
    private static final long serialVersionUID = -378279663255607690L;




    // 用户账号
    private String userAccount;
    // 用户密码
    private String userPassword;

}
