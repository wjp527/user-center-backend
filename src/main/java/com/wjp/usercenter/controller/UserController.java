package com.wjp.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wjp.usercenter.common.BaseResponse;
import com.wjp.usercenter.common.ErrorCode;
import com.wjp.usercenter.common.ResultUtils;
import com.wjp.usercenter.exception.BusinessException;
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
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null) {
            return null;
        }
        // TODO: 注册逻辑
        String userAccount = userRegisterRequest.getUserAccount();
        String getUserPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        // 判断都不能为空
        if(StringUtils.isAllBlank(userAccount, getUserPassword, checkPassword, planetCode)) {
            // 请求参数错误
            // return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            // 抛出异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, getUserPassword, checkPassword, planetCode);
        if(result == 0) {
            // 注册失败
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        // 返回注册成功的用户id
//        return new BaseResponse<>(0, result, "ok");
        return ResultUtils.success(result);
    }
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 校验参数
        if(StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        User userResult = userService.userLogin(userAccount, userPassword, request);
        if(userResult == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        return ResultUtils.success(userResult);
    }

    /**
     * 搜索用户数据
     * @param username
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        // 仅管理员可查询
        if(!admin) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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

//        return collectUserList;
        return ResultUtils.success(collectUserList);
    }

    /**
     * 删除用户数据
     * @param id
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        boolean admin = isAdmin(request);
        if(!admin) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        if(id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 逻辑删除
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
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
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 强转
        User user  =(User) attribute;
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }

        Long id = user.getId();
        // TODO: 校验用户是否合法(比如: 封号)
        User userInfo = userService.getById(id);
        // 返回脱敏后的信息
        User safetyUser = userService.getSafetyUser(userInfo);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> logout(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        int result = userService.userLogout(request);
        return  ResultUtils.success(result);
    }
}
