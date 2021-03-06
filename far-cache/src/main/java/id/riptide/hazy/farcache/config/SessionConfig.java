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

package id.riptide.hazy.farcache.config;

import id.jsonmapper.JSON;

/**
 * @author indroneel
 *
 */

public class SessionConfig {

	@JSON("backup-count")
	private int backupCount;

	@JSON("time-to-live")
	private int timeToLive;

	public SessionConfig() {
		backupCount = 3;
		timeToLive = 300;
	}

	public int getBackupCount() {
		return backupCount;
	}

	public int getTimeToLive() {
		return timeToLive;
	}
}
