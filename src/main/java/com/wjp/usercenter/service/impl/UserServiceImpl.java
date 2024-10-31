package com.wjp.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjp.usercenter.common.ErrorCode;
import com.wjp.usercenter.common.ResultUtils;
import com.wjp.usercenter.exception.BusinessException;
import com.wjp.usercenter.mapper.UserMapper;
import com.wjp.usercenter.model.domain.User;
import com.wjp.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * @param planetCode 星球编号
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1、校验(是否为空/null/空字符串)
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
//            return -1;
             throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        // 用户账号长度小于2
        if(userAccount.length() < 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于2位");
        }
        // 密码长度小于6
        if(userPassword.length() < 6 || checkPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于6位");
        }

        // 星球编号大于5位
        if(planetCode.length() >5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号不能大于5位");
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        // 星球账号不能重复
        QueryWrapper<User> queryWrapperPlanet = new QueryWrapper<>();
        queryWrapperPlanet.eq("planetCode", planetCode);
        long countPlanet = userMapper.selectCount(queryWrapperPlanet);
        if(countPlanet > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球账号已存在");
        }


        // 密码和校验密码相同
        if(!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不一致");
        }


        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3、保存到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        // 4、保存失败
        if(!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败");
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        // 用户账号长度小于2
        if(userAccount.length() < 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于2位");
        }
        // 密码长度小于6
        if(userPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于6位");
        }

        // 账号不能包含特殊字符
        String validPattern="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 3、用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4、记录用户的登陆状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 用户列表查询
     * @param user
     * @return
     */
    @Override
    public List<User> searchUsers(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 判断用户名是否为空
        if (StringUtils.isNotBlank(user.getUsername())) {
            queryWrapper.like("username", user.getUsername());
        }
        // 判断用户账号是否为空
        if(StringUtils.isNotBlank(user.getUserAccount())) {
            queryWrapper.like("userAccount", user.getUserAccount());
        }
        // 判断手机号是否为空
        if(StringUtils.isNotBlank(user.getPhone())) {
            queryWrapper.like("phone", user.getPhone());
        }
        // 判断用户状态是否为空
        if(user.getUserStatus() != null) {
            queryWrapper.eq("userStatus", user.getUserStatus());
        }
        // 判断用户角色是否为空
        if(user.getUserRole() != null) {
            queryWrapper.eq("userRole", user.getUserRole());
        }

        // 判断 createTime 是否存在，按指定时间范围过滤
        if (user.getCreateTime() != null) {
            // 将 Date 转换为 LocalDateTime
            LocalDateTime createDateTime = user.getCreateTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // 设置开始时间为传递的时间
            LocalDateTime startTime = createDateTime;
            // 设置结束时间为当天的 23:59:59
            LocalDateTime endTime = createDateTime.toLocalDate().atTime(23, 59, 59);

            queryWrapper.between("createTime", startTime, endTime);
        }

        // this.baseMapper: 调用父类的 mapper 对象
        return this.baseMapper.selectList(queryWrapper).stream()
                // 脱敏用户信息
                .map(this::getSafetyUser)
                .collect(Collectors.toList());

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
        safetyUser.setPlanetCode(originUser.getPlanetCode());
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

    @Override
    public int userListUpdate(User user) {
        System.out.println("user = " + user);
        int result = userMapper.updateById(user);
        return result;
    }

    /**
     * 用户列表删除
     * @param id
     * @return
     */
    @Override
    public Integer userListDelete(Long id) {
        int result = userMapper.deleteById(id);
        return result;
    }

    /**
     * 用户头像上传
     * @param file
     * @param request
     * @return
     */
    @Override
    public String saveFile(MultipartFile file, HttpServletRequest request) {
        String originalName = file.getOriginalFilename();

        // 检查文件名是否为空，并确保是图片文件
        if (originalName == null || !originalName.matches(".*\\.(png|jpg|jpeg|gif|bmp)$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型不支持，只允许上传图片文件");
        }

        // 获取文件扩展名
        String fileExtension = originalName.substring(originalName.lastIndexOf("."));

        // 文件保存目录路径
        String format = new SimpleDateFormat("/yyyy/MM/dd/").format(new Date());
        String uploadDir = "D:/uploads" + format;
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 生成文件名并加上原始扩展名
        String newName = UUID.randomUUID().toString() + fileExtension;
        try {
            file.transferTo(new File(folder, newName));

            // 生成文件的访问 URL
            String contextPath = request.getContextPath();
            String fileUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + contextPath + format + newName;

            return fileUrl;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传失败");
        }
    }




}




