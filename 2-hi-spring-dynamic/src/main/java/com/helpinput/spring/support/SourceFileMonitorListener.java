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
package com.helpinput.spring.support;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;


import com.helpinput.core.LoggerBase;
import com.helpinput.propertyeditors.GLClassEditor;
import com.helpinput.propertyeditors.PropertyEditorRegister;
import com.helpinput.settings.Options;
import com.helpinput.spring.SourceFileMonitor;

public class SourceFileMonitorListener implements ApplicationListener<ContextRefreshedEvent> {
	static Logger logger = LoggerBase.logger;
	SourceFileMonitor monitor = null;
	
	private List<String> dirs = null;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		ApplicationContext applicationContext = event.getApplicationContext();
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) ((AbstractApplicationContext) applicationContext)
				.getBeanFactory();
		
		PropertyEditorRegister.registerProtertyEditor(dlbf, GLClassEditor.class);
		//get root applicationContext
		while (applicationContext.getParent() != null) {
			applicationContext = applicationContext.getParent();
		}
		
		if (monitor == null)
			monitor = new SourceFileMonitor(dirs, applicationContext);
		
		if (!monitor.isRunning()) {
			try {
				monitor.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void setInterval(long interval) {
		synchronized (Options.scanInterval) {
			Options.scanInterval = interval;
		}
	}
	
	public void setDirs(List<String> dirs) {
		this.dirs = dirs;
	}
	
}
