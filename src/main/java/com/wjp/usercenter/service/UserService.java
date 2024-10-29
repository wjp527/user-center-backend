package com.wjp.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjp.usercenter.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
* @author wjp
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-10-28 17:01:17
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求对象
     * @return 返回用户脱敏信息
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前用户(脱敏信息)
     * @param originUser
     */
    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);

}
