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

import java.math.BigInteger;
import java.util.Collections;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.hazelcast.core.EntryView;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

/**
 * @author indroneel
 *
 */

@SuppressWarnings("deprecation")
public class HazySession implements HttpSession {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// This is the builder class that is used to create an instance of HazySession. The builder is
	// stateful, with each instance should be used to create exactly one session instancem, ever.

	public static class Builder {

		private HazySession hzs;

		public Builder() {
			hzs = new HazySession();
		}

		public Builder sessionsMap(IMap<String, SessionData> smap) {
			hzs.sessions = smap;
			return this;
		}

		public Builder sessionKeysMap(MultiMap<String, String> skmap) {
			hzs.sessionKeys = skmap;
			return this;
		}

		public Builder sessionValuesMap(IMap<String, Object> svmap) {
			hzs.sessionValues = svmap;
			return this;
		}

		public Builder servletContext(ServletContext ctxt) {
			hzs.ctxt = ctxt;
			return this;
		}

		public HazySession createNew() {
			UUID uuid = UUID.randomUUID();
			StringBuilder sb = new StringBuilder();
			sb.append(toPackedString(uuid.getMostSignificantBits()))
				.append('-')
				.append(toPackedString(uuid.getMostSignificantBits()));
			String sessionId = sb.toString().toLowerCase();

			if(hzs.sessions.containsKey(sessionId)) {
				// if a session already exists with the given id, do not create a new one.
				return null;
			}

			SessionData sd = new SessionData();
			sd.setCreationTime(System.currentTimeMillis());
			sd.setLastAccessTime(Long.MIN_VALUE);
			hzs.sessions.put(sessionId, sd);

			hzs.sessionId = sessionId;
			hzs.sessionData = sd;
			hzs.newFlag = true;
			return hzs;
		}

		public HazySession getExisting(String sessionId) {

			if(!hzs.sessions.containsKey(sessionId)) {
				// a session must exist already with the given id, else return null.
				return null;
			}

			SessionData sd = hzs.sessions.get(sessionId);
			sd.setLastAccessTime(System.currentTimeMillis());
			hzs.sessions.put(sessionId, sd);

			hzs.sessionId = sessionId;
			hzs.sessionData = sd;
			return hzs;
		}

		//Helper methods

		/** the constant 2^64 */
		private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);

		private String toPackedString(long l) {
			BigInteger b = BigInteger.valueOf(l);
			if(b.signum() < 0) {
				b = b.add(TWO_64);
			}
			return b.toString(36);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// This is a blank implementation of HttpSessionContext for backward compatibility

	private static class SessionContextImpl implements HttpSessionContext {

		@Override
		public HttpSession getSession(String sessionId) {
			return null;
		}

		@Override
		public Enumeration<String> getIds() {
			return Collections.emptyEnumeration();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private IMap<String, SessionData> sessions;
	private MultiMap<String, String>  sessionKeys;
	private IMap<String, Object>      sessionValues;
	private ServletContext            ctxt;

	private String      sessionId;
	private SessionData sessionData;
	private boolean     newFlag;
	private boolean     invalid;

	private HazySession() {
		//NOOP
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods of interface HttpSession

	@Override
	public long getCreationTime() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return sessionData.getCreationTime();
	}

	@Override
	public String getId() {
		return sessionId;
	}

	@Override
	public long getLastAccessedTime() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return sessionData.getLastAccessTime();
	}

	@Override
	public ServletContext getServletContext() {
		return ctxt;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		SessionData sd = sessions.get(sessionId);
		sessions.put(sessionId, sd, interval, TimeUnit.SECONDS);
	}

	@Override
	public int getMaxInactiveInterval() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		EntryView<String, SessionData> ev = sessions.getEntryView(sessionId);
		return (int) ev.getTtl();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return new SessionContextImpl();
	}

	@Override
	public Object getAttribute(String name) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return sessionValues.get(sessionId + ":" + name);
	}

	@Override
	public Object getValue(String name) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return Collections.enumeration(sessionKeys.get(sessionId));
	}

	@Override
	public String[] getValueNames() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return sessionKeys.get(sessionId).toArray(new String[0]);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		sessionValues.put(sessionId + ":" + name, value);
		sessionKeys.put(sessionId, name);
	}

	@Override
	public void putValue(String name, Object value) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		sessionKeys.remove(sessionId, name);
		sessionValues.remove(sessionId + ":" + name);
	}

	@Override
	public void removeValue(String name) {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		removeAttribute(name);
	}

	@Override
	public void invalidate() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		sessions.remove(sessionId);
		invalid = true;
	}

	@Override
	public boolean isNew() {
		if(invalid) {
			throw new IllegalStateException("session has been invalidated");
		}
		return newFlag;
	}
}
