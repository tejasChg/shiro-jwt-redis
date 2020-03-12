package com.wx.chen.shiro.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @ClassName: JWTToken
 * @Description:TODO(设置JWT设置token的方法)
 * @author: guanghuiChen
 * @date: 2020年3月11日 上午11:41:42
 * 
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved.
 */
public class JWTToken implements AuthenticationToken {

	private String token;

	public JWTToken(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

}
