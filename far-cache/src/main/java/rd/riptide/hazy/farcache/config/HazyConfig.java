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

package rd.riptide.hazy.farcache.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;

import rd.jsonmapper.JSON;

/**
 * @author indroneel
 *
 */

public class HazyConfig {

	@JSON("address")
	private String address;

	@JSON("port")
	private int port;

	@JSON("management-center")
	private ManagementCenter mgmtctr;

	@JSON("cluster")
	private Cluster cluster;

	public final Config createHazelcastConfig() {
		Config cfg = new Config();

		NetworkConfig netcfg = cfg.getNetworkConfig();
		if(address != null && address.trim().length() > 0) {
			netcfg.setPublicAddress(address);
		}
		netcfg.setPort(port >= 0 ? port : 0);
		netcfg.setPortAutoIncrement(false);
		netcfg.setPortCount(1);

		if(mgmtctr != null) {
			mgmtctr.populate(cfg);
		}

		if(cluster != null) {
			cluster.populate(cfg);
		}
		return cfg;
	}
}
