package com.wx.chen.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;


@Table(name = "role")
@Data
@NameStyle
public class RoleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private short status;// 0:失效 1：生效',
	private Date createTime;
	private Date updateTime;
}
