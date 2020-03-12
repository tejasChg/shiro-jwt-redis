package com.wx.chen.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**   
 * @ClassName:  Base64ConvertUtil   
 * @Description:TODO(Base64工具)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 上午11:59:19   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public class Base64ConvertUtil {
	/**
	 * @Title: encode   
	 * @Description: TODO(JDK1.8加密)   
	 * @param: @param str
	 * @param: @return
	 * @param: @throws UnsupportedEncodingException      
	 * @return: String      
	 * @throws
	 */
	 public static String encode(String str) throws UnsupportedEncodingException {
	        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes("utf-8"));
	        return new String(encodeBytes);
	    }
	 /**
	  * @Title: decode   
	  * @Description: TODO(JDK1.8解密)   
	  * @param: @param str
	  * @param: @return
	  * @param: @throws UnsupportedEncodingException      
	  * @return: String      
	  * @throws
	  */
	 public static String decode(String str) throws UnsupportedEncodingException {
	        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes("utf-8"));
	        return new String(decodeBytes);
	    }
}
