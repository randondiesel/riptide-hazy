/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package rd.riptide.hazy.capability;

import java.util.Collections;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

/**
 * @author indroneel
 *
 */

public class HazySession implements HttpSession {

	private IMap<String, SessionData> sessions;
	private MultiMap<String, String>  sessionKeys;
	private IMap<String, Object>      sessionValues;
	private ServletContext            ctxt;

	private String      sessionId;
	private SessionData sessionData;
	private boolean     newFlag;

	public static final HazySession createNew (
			IMap<String, SessionData> sessions,
			MultiMap<String, String> skeys,
			IMap<String, Object> svals, ServletContext ctxt) {

		UUID uuid = UUID.randomUUID();
		StringBuilder sb = new StringBuilder();
		sb.append(Long.toString(uuid.getMostSignificantBits(), 36))
			.append('-')
			.append(Long.toString(uuid.getMostSignificantBits(), 36));
		String sessionId = sb.toString().toLowerCase();

		if(sessions.containsKey(sessionId)) {
			// if a session already exists with the given id, do not create a new one.
			return null;
		}

		SessionData sd = new SessionData();
		sd.setCreationTime(System.currentTimeMillis());
		sd.setLastAccessTime(Long.MIN_VALUE);
		sessions.put(sessionId, sd);

		HazySession hs = new HazySession();

		hs.sessionId = sessionId;
		hs.sessions = sessions;
		hs.sessionKeys = skeys;
		hs.sessionValues = svals;
		hs.ctxt = ctxt;
		hs.sessionData = sd;
		hs.newFlag = true;
		return hs;
	}

	public static final HazySession getExisting(String sessionId,
			IMap<String, SessionData> sessions,
			MultiMap<String, String> skeys,
			IMap<String, Object> svals, ServletContext ctxt) {

		if(!sessions.containsKey(sessionId)) {
			// a session must exist already with the given id, else return null.
			return null;
		}

		SessionData sd = sessions.get(sessionId);
		sd.setLastAccessTime(System.currentTimeMillis());
		sessions.put(sessionId, sd);

		HazySession hs = new HazySession();
		hs.sessionId = sessionId;
		hs.sessions = sessions;
		hs.sessionKeys = skeys;
		hs.sessionValues = svals;
		hs.ctxt = ctxt;
		hs.sessionData = sd;
		return hs;
	}

	private HazySession() {
		//NOOP
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods of interface HttpSession

	@Override
	public long getCreationTime() {
		return sessionData.getCreationTime();
	}

	@Override
	public String getId() {
		return sessionId;
	}

	@Override
	public long getLastAccessedTime() {
		return sessionData.getLastAccessTime();
	}

	@Override
	public ServletContext getServletContext() {
		return ctxt;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
	 */
	@Override
	public void setMaxInactiveInterval(int interval) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
	 */
	@Override
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getSessionContext()
	 */
	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		return sessionValues.get(sessionId + ":" + name);
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(sessionKeys.get(sessionId));
	}

	@Override
	public String[] getValueNames() {
		return sessionKeys.get(sessionId).toArray(new String[0]);
	}

	@Override
	public void setAttribute(String name, Object value) {
		sessionValues.put(sessionId + ":" + name, value);
		sessionKeys.put(sessionId, name);
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		sessionKeys.remove(sessionId, name);
		sessionValues.remove(sessionId + ":" + name);
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#invalidate()
	 */
	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isNew() {
		return newFlag;
	}
}
