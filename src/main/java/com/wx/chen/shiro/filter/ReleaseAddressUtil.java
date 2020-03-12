package com.wx.chen.shiro.filter;

import java.util.HashSet;
import java.util.Set;

/**   
 * @ClassName:  ReleaseAddressUtil   
 * @Description:TODO(自定义要放行的接口)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 下午5:03:59   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public class ReleaseAddressUtil {
	private static Set<String> getInterface() {
		Set<String> set =new HashSet<String>();
		set.add("/user/login");
		return set;
	}
	public static Boolean confirm(String requestURI) {
		Set<String> set = getInterface();
		return set.contains(requestURI);
	}
}
