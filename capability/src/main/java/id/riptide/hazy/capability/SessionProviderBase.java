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

package id.riptide.hazy.capability;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

import id.riptide.ext.SessionProvider;
import id.riptide.hazy.capability.config.HazyConstants;

/**
 * @author indroneel
 *
 */

public abstract class SessionProviderBase implements SessionProvider, HazyConstants {

	protected HazelcastInstance hzi;
	protected ServletContext    ctxt;

	private IMap<String, SessionData> sessions;
	private MultiMap<String, String>  sessionKeys;
	private IMap<String, Object>      sessionValues;

	protected SessionProviderBase() {
		//NOOP
	}

	@Override
	public void initialize() {
		sessions = hzi.getMap(MNAME_SESSIONS);
		sessionKeys = hzi.getMultiMap(MNAME_SESSION_KEYS);
		sessionValues = hzi.getMap(MNAME_SESSION_VALUES);
	}

	@Override
	public HttpSession getSession() {
		return new HazySession.Builder()
			.servletContext(ctxt)
			.sessionsMap(sessions)
			.sessionKeysMap(sessionKeys)
			.sessionValuesMap(sessionValues)
			.createNew();
	}

	@Override
	public HttpSession getSession(String id) {
		return new HazySession.Builder()
				.servletContext(ctxt)
				.sessionsMap(sessions)
				.sessionKeysMap(sessionKeys)
				.sessionValuesMap(sessionValues)
				.getExisting(id);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
