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

import javax.servlet.http.HttpSession;

import rd.riptide.ext.SessionProvider;

/**
 * @author indroneel
 *
 */

public class HazySessionProvider implements SessionProvider {

	private HazyConfig hcfg;

	HazySessionProvider(HazyConfig hc) {
		hcfg = hc;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
