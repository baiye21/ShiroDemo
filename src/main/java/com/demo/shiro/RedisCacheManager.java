package com.demo.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public class RedisCacheManager implements CacheManager {

	// private RedisTemplate<String, Object> redisTemplate;

	@Override
	public <K, V> Cache<K, V> getCache(String arg0) throws CacheException {
		// Auto-generated method stub
		return new RedisCache<K, V>();
	}

}
