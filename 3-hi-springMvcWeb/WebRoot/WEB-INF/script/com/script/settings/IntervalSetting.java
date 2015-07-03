package com.script.settings;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import com.helpinput.core.LoggerBase;
import com.helpinput.settings.Options;

public class IntervalSetting {
	static Logger logger = LoggerBase.logger;
	
	//can overide the source monitor interval dynamic
	@PostConstruct
	public void setInteval() {
		Options.scanInterval = 6000L;
	}
}
