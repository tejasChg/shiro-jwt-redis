package com.wx.chen.service;

import javax.servlet.http.HttpServletResponse;

import com.wx.chen.request.UserPo;
import com.wx.chen.util.ResponseBean;

/**   
 * @ClassName:  UserService   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: guanghuiChen
 * @date:   2020年3月12日 上午9:53:36   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public interface UserService {

	ResponseBean login(UserPo user, HttpServletResponse httpServletResponse);

	/**   
	 * @Title: userList   
	 * @Description: TODO(这里用一句话描述这个方法的作用)   
	 * @param: @return      
	 * @return: ResponseBean      
	 * @throws   
	 */
	ResponseBean userList();

}
