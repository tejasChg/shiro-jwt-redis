package com.wx.chen.mapper;

import java.util.List;

import com.wx.chen.dto.PermissionDto;
import com.wx.chen.dto.RoleDto;
import com.wx.chen.entity.PermissionEntity;

import tk.mybatis.springboot.util.MyMapper;

/**   
 * @ClassName:  PermissionMapper   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 下午3:46:50   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public interface PermissionMapper extends MyMapper<PermissionEntity>{

	List<PermissionDto> findPermissionByRole(RoleDto roleDto);

}
