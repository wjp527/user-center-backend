package com.wjp.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjp.usercenter.mapper.UserMapper;
import com.wjp.usercenter.model.domain.User;
import com.wjp.usercenter.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author wjp
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-10-28 17:01:17
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




