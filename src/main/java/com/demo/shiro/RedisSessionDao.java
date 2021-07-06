package com.demo.shiro;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.util.RedisUtil;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 
*/
public class RedisSessionDao extends AbstractSessionDAO {

	// Session超时时间，单位为毫秒
	private long expireTime = 120000;

	@Autowired
	RedisUtil redisUtil;

	@Override
	public void update(Session session) throws UnknownSessionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Session session) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Session> getActiveSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Serializable doCreate(Session session) {
		// TODO Auto-generated method stub
		Serializable sessionId = this.generateSessionId(session);
		this.assignSessionId(session, sessionId);

		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

}
