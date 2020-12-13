package com.someexp.modules.user.service.impl;

import com.someexp.common.exception.BusinessException;
import com.someexp.common.utils.JwtUtils;
import com.someexp.common.utils.MsgUtils;
import com.someexp.common.utils.PasswordEncoderUtils;
import com.someexp.modules.user.domain.dto.UserDTO;
import com.someexp.modules.user.domain.entity.User;
import com.someexp.modules.user.mapper.UserMapper;
import com.someexp.modules.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;

/**
 * @author someexp
 * @date 2020/10/24
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 检查手机号是否注册过
     * xss过滤
     *
     * @param userDTO
     * @return
     */
    @Override
    public String register(UserDTO userDTO) {
        User dbUser = userMapper.getByPhone(userDTO.getPhone());
        if (dbUser != null) {
            throw new BusinessException(MsgUtils.get("user.phone.exist"));
        }

        User user = new User();
        user.setName(HtmlUtils.htmlEscape(userDTO.getName()));
        user.setPhone(userDTO.getPhone());
        user.setPassword(PasswordEncoderUtils.encode(userDTO.getPassword()));

        userMapper.save(user);
        return user.getName();
    }

    /**
     * 根据phone寻找用户
     * match密码
     *
     * @param userDTO
     * @return
     */
    @Override
    public String login(UserDTO userDTO) {
        User user = userMapper.getByPhone(userDTO.getPhone());
        if (user == null) {
            throw new BusinessException(MsgUtils.get("user.not.found"));
        }

        if (!PasswordEncoderUtils.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(MsgUtils.get("user.password.dont.match"));
        }

        String token = JwtUtils.create(user.getId());
        return token;
    }


}
