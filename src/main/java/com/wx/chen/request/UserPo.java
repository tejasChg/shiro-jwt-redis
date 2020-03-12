package com.wx.chen.request;

import java.util.Date;

import lombok.Data;

/**   
 * @ClassName:  UserPo   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: guanghuiChen
 * @date:   2020年3月12日 上午9:50:03   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
@Data
public class UserPo {
	private Integer id;
	private String name;
	private String account;
	private String password;//32
	private short status;// 0:失效 1：生效',
	private Date createTime;
	private Date updateTime;
}
