package com.wx.chen.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wx.chen.constant.CommonConstant;
import com.wx.chen.dto.UserDto;
import com.wx.chen.entity.UserEntity;
import com.wx.chen.mapper.UserMapper;
import com.wx.chen.request.UserPo;
import com.wx.chen.service.UserService;
import com.wx.chen.shiro.jwt.JwtUtil;
import com.wx.chen.util.MD5;
import com.wx.chen.util.RedisUtil;
import com.wx.chen.util.ResponseBean;

/**
 * @ClassName: UserServiceImpl
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: guanghuiChen
 * @date: 2020年3月12日 上午9:54:04
 * 
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Value("${refreshTokenExpireTime}")
	private  String refreshTokenExpireTime;
//	@Autowired
//	private RedisUtil redisUtil;

	@Override
	public ResponseBean login(UserPo user, HttpServletResponse httpServletResponse) {
		// 对密码进行加密
		String password = MD5.GetMD5Code(user.getPassword());
		UserEntity userEntity = new UserEntity();
		userEntity.setAccount(user.getAccount());
		userEntity.setPassword(password);
		UserEntity result = userMapper.selectOne(userEntity);
		if (null == result) {
			return new ResponseBean(0, "用户名或者密码错误", false);
		}
		// 清除可能存在的Shiro权限信息缓存
		if (RedisUtil.hasKey(CommonConstant.PREFIX_SHIRO_CACHE + user.getAccount())) {
			// 删除
			RedisUtil.delete(CommonConstant.PREFIX_SHIRO_CACHE + user.getAccount());
		}
		// 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
		String currentTimeMillis = String.valueOf(System.currentTimeMillis());
		RedisUtil.setEx(CommonConstant.PREFIX_SHIRO_REFRESH_TOKEN + user.getAccount(), currentTimeMillis,
				Long.parseLong(refreshTokenExpireTime), TimeUnit.SECONDS);
		// 使用jwt进行登录
		String token = JwtUtil.sign(user.getAccount(), currentTimeMillis);
		// 从Header中Authorization返回AccessToken，时间戳为当前时间戳
		httpServletResponse.setHeader("Authorization", token);
		httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
		return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)", null);
	}

	@Override
	public ResponseBean userList() {
		List<UserEntity> selectAll = userMapper.selectAll();
		return new ResponseBean(HttpStatus.OK.value(), "查询成功", selectAll);
	}
}
