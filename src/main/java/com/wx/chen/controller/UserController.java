package com.wx.chen.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wx.chen.request.UserPo;
import com.wx.chen.service.UserService;
import com.wx.chen.util.ResponseBean;
import com.wx.chen.util.StringUtil;

import io.swagger.annotations.ApiOperation;

/**
 */
@RestController
@RequestMapping("/user")
@ApiOperation(value = "用户管理",notes = "user manager")
public class UserController {

	@Autowired
	private UserService userService;
	
	@ApiOperation(value = "统一报错接口",notes = "error")
	@RequestMapping(value = "/401",method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean login401() {
		return new ResponseBean(401,"Authc ERROR",false);
	}
	
	
	@ApiOperation(value = "用户登录接口",notes = "Login")
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean login(@RequestBody UserPo user, HttpServletResponse httpServletResponse) {
		if(StringUtil.isBlank(user.getAccount())||StringUtil.isBlank(user.getPassword())) {
			return new ResponseBean(0,"用户名或者密码为空",false);
		}
		return userService.login(user,httpServletResponse);
	}
	
	@ApiOperation(value = "用户登录接口",notes = "Login")
	@RequiresRoles(value = "admin")//--通过角色来控制访问权限。管理员可以访问
	@RequestMapping(value = "/userList",method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean userList(@RequestBody UserPo user) {
		return userService.userList();
	}
	
	
	@ApiOperation(value = "根据角色所绑定的权限去启用接口",notes = "Login")
	@RequiresPermissions(value = "user:add")
	@RequestMapping(value = "/testPermission",method = RequestMethod.POST)
	@ResponseBody
	public ResponseBean testPermission(@RequestBody UserPo user) {
		return new ResponseBean(100,"你所在的权限拥有  {user:add} 页面权限  ",true);
	}
}