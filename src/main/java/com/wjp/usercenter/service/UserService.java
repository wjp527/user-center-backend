package com.wjp.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjp.usercenter.model.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * @param planetCode 星球编号
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

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
     * 用户列表查询
     * @param user
     * @return
     */
    List<User> searchUsers(User user);

    /**
     * 获取当前用户(脱敏信息)
     * @param originUser
     */
    User getSafetyUser(User originUser);

    int userLogout(HttpServletRequest request);


    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int userListUpdate(User user);

    /**
     * 用户列表删除
     * @param id
     * @return
     */
    Integer  userListDelete(Long id);

    /**
     * 上传文件
     * @param file
     * @param request
     * @return
     */
    String saveFile(MultipartFile file, HttpServletRequest request);



}
