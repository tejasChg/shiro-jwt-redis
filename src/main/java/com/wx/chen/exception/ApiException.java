package com.wx.chen.exception;


/**   
 * @ClassName:  ApiException   
 * @Description:TODO(自定义异常)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 下午12:02:30   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public class ApiException  extends RuntimeException {
	private int code;
	public ApiException(String msg){
		super(msg);
	}
	
	public ApiException(int code,String msg){
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
