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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

import rd.riptide.ext.SessionProvider;

/**
 * @author indroneel
 *
 */

public abstract class HazySessionProvider implements SessionProvider {

	protected HazelcastInstance hzi;
	protected ServletContext    ctxt;

	private IMap<String, SessionData> sessions;
	private MultiMap<String, String>  sessionKeys;
	private IMap<String, Object>      sessionValues;

	protected HazySessionProvider() {
		//NOOP
	}

	@Override
	public void initialize() {
		sessions = hzi.getMap("hazy-sessions");
		sessionKeys = hzi.getMultiMap("hazy-session-keys");
		sessionValues = hzi.getMap("hazy-session-values");
	}

	@Override
	public HttpSession getSession() {
		return HazySession.createNew(sessions, sessionKeys, sessionValues, ctxt);
	}

	@Override
	public HttpSession getSession(String id) {
		return HazySession.getExisting(id, sessions, sessionKeys, sessionValues, ctxt);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
