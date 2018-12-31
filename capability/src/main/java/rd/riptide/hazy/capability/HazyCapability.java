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

import java.util.logging.Level;
import java.util.logging.Logger;

import rd.riptide.ext.Capability;
import rd.riptide.ext.Environment;
import rd.riptide.hazy.capability.config.HazyConfig;

/**
 * @author randondiesel
 *
 */

public class HazyCapability implements Capability {

	private static final Logger LOGGER = Logger.getLogger(HazyCapability.class.getName());

	private SessionProviderBase hsp;

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Methods of interface Capability

	@Override
	public void initialize(Environment env) {
		HazyConfig hcfg = env.getConfig("hazy", HazyConfig.class);
		if(hcfg == null) {
			if(LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("missing configuration entry: hazy");
			}
			return;
		}

		if(hcfg.clientServerConfig() != null) {
			hsp = new ClientServerProvider(hcfg, env.getServletContext());
		}
		else if(hcfg.peer2PeerConfig() != null) {
			hsp = new Peer2PeerProvider(hcfg, env.getServletContext());
		}
		else {
			if(LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("missing both client-server and p2p configuration for hazy");
			}
			return;
		}
		env.setSessionProvider(hsp);
	}

	@Override
	public void dispose() {
		if(hsp != null) {
			hsp.dispose();
		}
		hsp = null;
	}
}
