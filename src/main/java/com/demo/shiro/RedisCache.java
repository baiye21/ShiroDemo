package com.demo.shiro;

import java.util.Collection;
import java.util.Set;

import com.demo.entity.UserMaster;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.demo.common.Const;
import com.demo.common.ConstCode;
import com.demo.common.RedisConst;
import com.demo.util.JwtUtil;
import com.demo.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
@Slf4j
public class RedisCache<K, V> implements Cache<K, V> {

	@Autowired
	@Lazy
	private RedisUtil redisUtil;

	/**
	 * 缓存的key名称获取为shiro:cache:account
	 *
	 * @param key
	 * @return java.lang.String
	 */
	private String getKey(Object key) {

		try {
			SimplePrincipalCollection SimplePrincipalCollection = (SimplePrincipalCollection) key;

			Object primaryPrincipal = SimplePrincipalCollection.getPrimaryPrincipal();

			String userid = ConstCode.STRING_EMPTY;

			if (primaryPrincipal != null) {
				if (primaryPrincipal instanceof UserMaster) {

					UserMaster userMaster = (UserMaster) primaryPrincipal;

					userid = userMaster.getUserId();

				} else {
					userid = JwtUtil.getClaim(key.toString(), Const.TOKEN_CLAIM_USERID);
				}
			}

			return RedisConst.PREFIX_SHIRO_CACHE + userid;

		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 获取缓存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object get(Object key) throws CacheException {

		return redisUtil.get(this.getKey(key));
	}

	/**
	 * 保存缓存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) throws CacheException {

		// 设置Redis的Shiro缓存
		try {
			// redis shiro缓存 过期时间3600秒
			redisUtil.set(this.getKey(key), value, RedisConst.SHIRO_CACHE_EXRP_TIME);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移除缓存
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object remove(Object key) throws CacheException {

		redisUtil.del(this.getKey(key));

		return null;
	}

	/**
	 * 清空所有缓存
	 */
	@Override
	public void clear() throws CacheException {
		// TODO Auto-generated method stub

	}

	/**
	 * 缓存的个数
	 */
	@Override
	public Set<K> keys() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取所有的key
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 获取所有的value
	 */
	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}
}
