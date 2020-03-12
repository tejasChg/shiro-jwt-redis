package com.wx.chen.mapper;

import java.util.List;

import com.wx.chen.dto.RoleDto;
import com.wx.chen.dto.UserDto;
import com.wx.chen.entity.RoleEntity;

import tk.mybatis.springboot.util.MyMapper;

/**   
 * @ClassName:  RoleMapper   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 下午3:47:05   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public interface RoleMapper extends MyMapper<RoleEntity>{

	List<RoleDto> findRoleListByAccount(UserDto userDto);

}
