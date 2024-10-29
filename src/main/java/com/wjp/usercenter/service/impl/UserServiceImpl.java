package com.wjp.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjp.usercenter.mapper.UserMapper;
import com.wjp.usercenter.model.domain.User;
import com.wjp.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wjp.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
* @author wjp
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-28 17:01:17
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /*
     * 盐值
     */

    private static final String SALT = "wjp";



    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1、校验(是否为空/null/空字符串)
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }
        // 用户账号长度小于2
        if(userAccount.length() < 2) {
            return -1;
        }
        // 密码长度小于6
        if(userPassword.length() < 6 || checkPassword.length() < 6) {
            return -1;
        }

        // 账号不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return -1;
        }

        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        // 用户账号重复
        if(count > 0) {
            return -1;
        }



        // 密码和校验密码相同
        if(!userPassword.equals(checkPassword)) {
            return -1;
        }


        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3、保存到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        // 4、保存失败
        if(!saveResult) {
            return -1;
        }

        return user.getId();


    }

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求对象
     * @return
     */

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、校验(是否为空/null/空字符串)
        if(StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        // 用户账号长度小于2
        if(userAccount.length() < 2) {
            return null;
        }
        // 密码长度小于6
        if(userPassword.length() < 6) {
            return null;
        }

        // 账号不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return null;
        }


        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在或密码错误
        if(user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }

        // 3、用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4、记录用户的登陆状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return 返回脱敏信息
     */
    @Override
    public User getSafetyUser(User originUser) {
        if(originUser == null) return null;
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());

        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除session
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




