package com.wx.chen.config;

import org.springframework.stereotype.Component;

@Component
public class Config {
 	// 	是否拦截请求
 	public static Boolean shiroConfig = false;
 	//是否开放游客权限
	public static Boolean mustLoginFlag = false;
}
