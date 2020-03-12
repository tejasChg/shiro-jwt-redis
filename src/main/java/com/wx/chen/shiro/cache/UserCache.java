package com.wx.chen.shiro.cache;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import com.wx.chen.constant.CommonConstant;
import com.wx.chen.shiro.jwt.JwtUtil;
import com.wx.chen.util.PropertiesUtil;
import com.wx.chen.util.RedisUtil;

/**
 * @ClassName: UserCache
 * @Description:TODO(重写Shiro的Cache保存读取)
 * @author: guanghuiChen
 * @date: 2020年3月11日 下午4:22:23
 * 
 * @Copyright: 2020 cghzy@foxmail.com Inc. All rights reserved.
 */
public class UserCache<K, V> implements Cache<K, V> {

	/**
	 * @Title: getKey @Description:
	 * TODO(缓存的key名称获取为shiro:cache:account) @param: @param
	 * key @param: @return @return: String @throws
	 */
	private String getKey(Object key) {
		return CommonConstant.PREFIX_SHIRO_CACHE + JwtUtil.getClaim(key.toString(), CommonConstant.ACCOUNT);
	}

	/**
	 * <p>
	 * Title: get
	 * </p>
	 * <p>
	 * Description: 获取缓存
	 * </p>
	 * 
	 * @param key
	 * @return
	 * @throws CacheException
	 * @see org.apache.shiro.cache.Cache#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) throws CacheException {
		// 查看redis中是否存在该缓存
		if (!RedisUtil.hasKey(this.getKey(key))) {
			// 不存在。返回null
			return null;
		}
		// 存在则返回当前的缓存
		return RedisUtil.get(this.getKey(key));
	}

	/**
	 * <p>
	 * Title: put
	 * </p>
	 * <p>
	 * Description:保存缓存
	 * </p>
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws CacheException
	 * @see org.apache.shiro.cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(Object key, Object value) throws CacheException {
		// 读取配置文件，获取Redis的Shiro缓存过期时间
		PropertiesUtil.readProperties("application.yml");
		String refreshTokenExpireTime = PropertiesUtil.getProperty("shiroCacheExpireTime");
		// 设置Redis的Shiro缓存
		RedisUtil.setEx(key.toString(), value.toString(), Long.parseLong(refreshTokenExpireTime), TimeUnit.SECONDS);
		return refreshTokenExpireTime;//返回值没有意义
	}

	/**
	 * <p>
	 * Title: remove
	 * </p>
	 * <p>
	 * Description:移除缓存
	 * </p>
	 * 
	 * @param key
	 * @return
	 * @throws CacheException
	 * @see org.apache.shiro.cache.Cache#remove(java.lang.Object)
	 */
	@Override
	public Object remove(Object key) throws CacheException {
		// 查看redis中是否存在该缓存
		if (!RedisUtil.hasKey(this.getKey(key))) {
			// 不存在。返回null
			return null;
		}
		RedisUtil.delete(this.getKey(key));
		return null;
	}

	/**
	 * <p>
	 * Title: clear
	 * </p>
	 * <p>
	 * Description:清空所有缓存
	 * </p>
	 * 
	 * @throws CacheException
	 * @see org.apache.shiro.cache.Cache#clear()
	 */
	@Override
	public void clear() throws CacheException {
		RedisUtil.clearRedis();
	}

	/**
	 * <p>
	 * Title: size
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @return
	 * @see org.apache.shiro.cache.Cache#size()
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return RedisUtil.redisSize();
	}

	/**
	 * <p>
	 * Title: keys
	 * </p>
	 * <p>
	 * Description:获取所有的key
	 * </p>
	 * 
	 * @return
	 * @see org.apache.shiro.cache.Cache#keys()
	 */
	@Override
	public Set keys() {
		// TODO Auto-generated method stub
		return RedisUtil.keys();
	}

	/**
	 * <p>
	 * Title: values
	 * </p>
	 * <p>
	 * Description:获取所有的value
	 * </p>
	 * 
	 * @return
	 * @see org.apache.shiro.cache.Cache#values()
	 */
	@Override
	public Collection values() {
		// TODO Auto-generated method stub
		return RedisUtil.values();
	}

}
