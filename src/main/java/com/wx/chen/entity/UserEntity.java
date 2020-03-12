package com.wx.chen.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import tk.mybatis.mapper.annotation.NameStyle;

@Table(name = "user")
@Data
@NameStyle
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String account;
	private String password;//32
	private short status;// 0:失效 1：生效',
	@Column(name = "create_time")
	private Date createTime;
	@Column(name = "update_time")
	private Date updateTime;
}

