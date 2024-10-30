package com.wjp.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author wjp
 */
@Data
public class UserRegisterRequest implements Serializable {

    // 序列化ID
    private static final long serialVersionUID = 3097793169897314446L;

    // 用户账号
    private String userAccount;
    // 用户密码
    private String userPassword;
    // 校验密码
    private String checkPassword;
    // 星球编号
    private String planetCode;

}
