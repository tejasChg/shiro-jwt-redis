package com.wx.chen.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**   
 * @ClassName:  UserCacheManager   
 * @Description:TODO(重写Shiro缓存管理器)   
 * @author: guanghuiChen
 * @date:   2020年3月11日 下午4:20:21   
 *     
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved. 
 */
public class UserCacheManager implements CacheManager {
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		// TODO Auto-generated method stub
		return new UserCache<K, V>();
	}

}
