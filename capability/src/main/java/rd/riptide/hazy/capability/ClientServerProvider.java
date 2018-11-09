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

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import rd.riptide.hazy.capability.config.HazyConfig;

/**
 * @author indroneel
 *
 */

public class ClientServerProvider extends SessionProviderBase {

	private HazyConfig hazyCfg;

	public ClientServerProvider(HazyConfig cfg, ServletContext ctxt) {
		hazyCfg = cfg;
		super.ctxt = ctxt;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods of interface SessionProvider

	@Override
	public void initialize() {
		ClientConfig ccfg = new ClientConfig();
		hazyCfg.clientServerConfig().populate(ccfg);
		HazelcastInstance hzi = HazelcastClient.newHazelcastClient(ccfg);
		super.hzi = hzi;
		super.initialize();
	}
}
