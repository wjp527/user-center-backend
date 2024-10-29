package com.wjp.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjp.usercenter.model.domain.User;
import com.wjp.usercenter.model.domain.request.UserLoginRequest;
import com.wjp.usercenter.model.domain.request.UserRegisterRequest;
import com.wjp.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wjp.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.wjp.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author wjp
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null) {
            return null;
        }
        // TODO: 注册逻辑
        String userAccount = userRegisterRequest.getUserAccount();
        String getUserPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAllBlank(userAccount, getUserPassword, checkPassword)) {
            return null;
        }
        long id = userService.userRegister(userAccount, getUserPassword, checkPassword);
        if(id == 0) {
            return null;
        }
        return id;
    }
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 校验参数
        if(StringUtils.isAllBlank(userAccount, userPassword)) {
            return null;
        }
        User userResult = userService.userLogin(userAccount, userPassword, request);
        if(userResult == null) {
            return null;
        }
        return userResult;
    }

    /**
     * 搜索用户数据
     * @param username
     * @return
     */
    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        // 仅管理员可查询
        if(!admin) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 判断用户名是否为空
        if(StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }

        List<User> userList = userService.list(queryWrapper);
        List<User> collectUserList = userList.stream().map(user -> {
            // 获取用户脱敏信息
            User safetyUser = userService.getSafetyUser(user);
            return safetyUser;
        }).collect(Collectors.toList());

        return collectUserList;
    }

    /**
     * 删除用户数据
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        if(!admin) {
            return false;
        }
        if(id <= 0) {
            return false;
        }
        // 逻辑删除
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {

        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }


    /**
     * 获取当前用户信息
     * @param request
     * @return 返回用户信息
     */
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 强转
        User user  =(User) attribute;
        if(user == null) {
            return null;
        }

        Long id = user.getId();
        // TODO: 校验用户是否合法(比如: 封号)
        User userInfo = userService.getById(id);
        // 返回脱敏后的信息
        return userService.getSafetyUser(userInfo);
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Integer logout(HttpServletRequest request) {
        if(request == null) {
            return null;
        }

        return  userService.userLogout(request);
    }
}
