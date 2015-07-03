/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *@Author: niaoge(Zhengsheng Xia)
 *@Email 78493244@qq.com
 *@Date: 2015-6-16
 */
package com.helpinput.spring;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.ApplicationContext;

import com.helpinput.settings.Options;


public class SourceFileMonitor implements Runnable {
	
	private SourceScaner scaner;
	
	private Thread thread = null;
	private ThreadFactory threadFactory;
	private volatile boolean running = false;
	
	public boolean isRunning() {
		return running;
	}
	
	public SourceFileMonitor( List<String> dirs, ApplicationContext applicationContext) {
		scaner = new SourceScaner(dirs, applicationContext);
	}
	
	public synchronized void setThreadFactory(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
	}
	
	public synchronized void start() throws Exception {
		if (running) {
			throw new IllegalStateException("Monitor is already running");
		}
		
		running = true;
		if (threadFactory != null) {
			thread = threadFactory.newThread(this);
		}
		else {
			thread = new Thread(this);
		}
		thread.start();
	}
	
	public synchronized void stop() throws Exception {
		stop(Options.scanInterval);
	}
	
	public synchronized void stop(long stopInterval) throws Exception {
		if (running == false) {
			throw new IllegalStateException("Monitor is not running");
		}
		running = false;
		try {
			thread.join(stopInterval);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		scaner.stop();
	}
	
	public void run() {
		while (running) {
			scaner.scanSource();
			if (!running) {
				break;
			}
			try {
				Thread.sleep(Options.scanInterval);
			}
			catch (final InterruptedException ignored) {
			}
		}
	}
	
}
