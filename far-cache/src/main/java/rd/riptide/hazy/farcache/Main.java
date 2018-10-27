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

package rd.riptide.hazy.farcache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import rd.jsonmapper.decode.Json2Object;
import rd.riptide.hazy.farcache.config.HazyConfig;

/**
 * @author indroneel
 *
 */

public class Main {

	public static void main(String[] args) throws Exception {
		//setup slf4j as the underlying logging mechanism.
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		if(args.length == 0) {
			//TODO: print error message
			return;
		}

		Main main = new Main();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				main.stop();
			}
		};
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));

		main.start(args[0]);
		Thread.currentThread().join();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	private HazelcastInstance hinst;

	private void start(String cfgFileName) throws IOException {
		FileInputStream fis = new FileInputStream(new File(cfgFileName));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int oneByte;
		while((oneByte = fis.read()) >= 0) {
			bos.write(oneByte);
			byte[] buffer = new byte[fis.available()];
			int amt = fis.read(buffer);
			bos.write(buffer, 0, amt);
		}
		bos.flush();
		fis.close();

		HazyConfig hazyCfg = (HazyConfig) new Json2Object().convert(bos.toByteArray(), HazyConfig.class);
		bos.close();

		Config cfg = hazyCfg.createHazelcastConfig();
		hinst = Hazelcast.newHazelcastInstance(cfg);
	}

	private void stop() {
		if(hinst != null) {
			hinst.shutdown();
		}
	}
}
