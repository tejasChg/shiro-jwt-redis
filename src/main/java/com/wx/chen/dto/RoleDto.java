package com.wx.chen.dto;

import java.util.Date;

import lombok.Data;


@Data
public class RoleDto {
	private Integer id;
	private String name;
	private short status;// 0:失效 1：生效',
	private Date createTime;
	private Date updateTime;
}
