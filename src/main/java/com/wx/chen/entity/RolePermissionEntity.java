package com.wx.chen.entity;

import java.util.Date;
import javax.persistence.Table;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;
@Table(name = "role_permission")
@Data
@NameStyle
public class RolePermissionEntity {
	private Integer roleId;
	private Integer permissionId;
	private Date createTime;
}
