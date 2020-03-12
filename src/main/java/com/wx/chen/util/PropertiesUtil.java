package com.wx.chen.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wx.chen.exception.ApiException;

/**   
 * @ClassName:  PropertiesUtil   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 下午1:54:17   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public class PropertiesUtil {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * PROP
     */
    private static final Properties PROP = new Properties();

    /**
     * 读取配置文件
     * @param fileName
     * @return void
     */
    public static void readProperties(String fileName) {
        InputStream in = null;
        try {
            in = PropertiesUtil.class.getResourceAsStream("/" + fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            PROP.load(bf);
        } catch (IOException e) {
            logger.error("PropertiesUtil工具类读取配置文件出现IOException异常:" + e.getMessage());
            throw new ApiException("PropertiesUtil工具类读取配置文件出现IOException异常:" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("PropertiesUtil工具类读取配置文件出现IOException异常:" + e.getMessage());
                throw new ApiException("PropertiesUtil工具类读取配置文件出现IOException异常:" + e.getMessage());
            }
        }
    }

    /**
     * 根据key读取对应的value
     * @param key
     * @return java.lang.String
     */
    public static String getProperty(String key){
        return PROP.getProperty(key);
    }
}
