package com.wjp.usercenter.service;
import java.util.Date;

import com.wjp.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 * @author wjp
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("wjp");
        user.setUserAccount("test");
        user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1840273607343382530/xGloqqJXswh2fwCk.webp");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("13812345678");
        user.setEmail("13812345678@qq.com");
        user.setUserStatus(0);


        // 插入用户数据
        boolean result = userService.save(user);
        System.out.println(user.getId());

        // 断言，是否成功
        assertTrue(result);
    }

    /**
     * 用户注册测试
     */
    @Test
    void userRegister() {
        String userAccount = "wjp";
        String userPassword = "";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        userAccount = "1";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        userAccount = "wjp";
        userPassword= "1234";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);



        userAccount = "xxx _?wjp";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);


        userAccount = "xxxwjp";
        userPassword= "1234";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);


        userAccount = "test1";
        userPassword= "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

    }
}