package com.wx.chen.shiro.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.wx.chen.constant.CommonConstant;
import com.wx.chen.dto.PermissionDto;
import com.wx.chen.dto.RoleDto;
import com.wx.chen.dto.UserDto;
import com.wx.chen.entity.UserEntity;
import com.wx.chen.mapper.PermissionMapper;
import com.wx.chen.mapper.RoleMapper;
import com.wx.chen.mapper.UserMapper;
import com.wx.chen.shiro.jwt.JWTToken;
import com.wx.chen.shiro.jwt.JwtUtil;
import com.wx.chen.util.RedisUtil;
import com.wx.chen.util.StringUtil;

/**
 * @ClassName: UserRealm
 * @Description:TODO(自定义的realm)
 * @author: guanghuiChen
 * @date: 2020年3月11日 下午3:34:43
 * 
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved.
 */
@Service
//@Component  //经测试两个注解在此处的用户相同
public class UserRealm extends AuthorizingRealm {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private PermissionMapper permissionMapper;
	@Autowired
	private RoleMapper roleMapper;

	/**
	 * <p>
	 * Title: supports
	 * </p>
	 * <p>
	 * Description: 大坑，必须重写此方法，不然Shiro会报错
	 * </p>
	 * 
	 * @param token
	 * @return
	 * @see org.apache.shiro.realm.AuthenticatingRealm#supports(org.apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	public boolean supports(AuthenticationToken authenticationToken) {
		return authenticationToken instanceof JWTToken;
	}

	/**
	 * <p>
	 * Title: doGetAuthorizationInfo
	 * </p>
	 * <p>
	 * Description: 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
	 * </p>
	 * 
	 * @param principals
	 * @return
	 * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		// 从PrincipalCollection中获取token进行验证
		String account = JwtUtil.getClaim(principals.toString(), CommonConstant.ACCOUNT);
		UserDto userDto = new UserDto();
		userDto.setAccount(account);
		// 通过账号获取到该用户的角色信息
		List<RoleDto> roleList = roleMapper.findRoleListByAccount(userDto);
		// 遍历角色信息
		for (RoleDto roleDto : roleList) {
			if (null != roleDto) {
				// 添加该用户的角色信息
				simpleAuthorizationInfo.addRole(roleDto.getName());
				// 根据用户角色查询权限
				List<PermissionDto> permissionDtos = permissionMapper.findPermissionByRole(roleDto);
				for (PermissionDto permissionDto : permissionDtos) {
					if (null != permissionDto) {
						// 添加权限
						simpleAuthorizationInfo.addStringPermission(permissionDto.getAction());
					}
				}
			}
		}
		return simpleAuthorizationInfo;
	}

	/**
	 * <p>
	 * Title: doGetAuthenticationInfo
	 * </p>
	 * <p>
	 * Description:默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
	 * </p>
	 * 
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 * @see org.apache.shiro.realm.AuthenticatingRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
			throws AuthenticationException {
		// 获取到token
		String token = (String) authenticationToken.getCredentials();
		// 获取到token信息用于和数据库对比
		String account = JwtUtil.getClaim(token, CommonConstant.ACCOUNT);
		if (StringUtil.isBlank(account)) {
			throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
		}
		// 去数据库查询用户是否存在
		UserEntity userDto = new UserDto();
		userDto.setAccount(account);
		userDto = userMapper.selectOne(userDto);
		if (userDto == null) {
			throw new AuthenticationException("该帐号不存在(The account does not exist.)");
		}
		// 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
		if (JwtUtil.verify(token) && RedisUtil.hasKey(CommonConstant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
			// 获取RefreshToken的时间戳
			// Redis中RefreshToken还存在，获取RefreshToken的时间戳
			String currentTimeMillisRedis = RedisUtil.get(CommonConstant.PREFIX_SHIRO_REFRESH_TOKEN + account);
			// 获取AccessToken时间戳，与RefreshToken的时间戳对比
			if (JwtUtil.getClaim(token, CommonConstant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
				return new SimpleAuthenticationInfo(token, token, "userRealm");
			}
		}
		throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
	}
}
