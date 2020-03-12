package com.wx.chen.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;

@Table(name = "user_role")
@Data
@NameStyle
public class UserRoleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Integer userId;
	private Integer roleId;
	private Date createTime;
}
