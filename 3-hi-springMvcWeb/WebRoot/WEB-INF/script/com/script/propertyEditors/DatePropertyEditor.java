package com.script.propertyEditors;

import java.beans.PropertyEditorSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.helpinput.core.Utils;
import com.helpinput.spring.annotation.Property;
import com.helpinput.spring.annotation.Prototype;
import com.helpinput.spring.annotation.Properties;
import com.helpinput.spring.annotation.TargetType;
import org.slf4j.Logger;
import com.helpinput.core.LoggerBase;

@Prototype
@TargetType(Date.class)
@Properties({ @Property(name = "format", value = "yyyy-MM-dd") })
// or @Property(name = "format", value = "yyyy-MM-dd") when has 1 setter
public class DatePropertyEditor extends PropertyEditorSupport {
	static Logger logger = LoggerBase.logger;
	
	private String format = "yyyy-MM-dd";
	
	public void setFormat(String format) {
		this.format = format;
		
	}
	
	public void setAsText(String text) throws IllegalArgumentException {
		logger.info( text+ "........〖1〗........" + format);
		if (Utils.hasLength(text) && Utils.hasLength(format)) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				Date date = sdf.parse(text);
				this.setValue(date);
				return;
			}
			catch (ParseException e) {
				System.out.println(e.getMessage() + "data format error!");
			}
		}
		setValue(null);
	}
	
	@Override
	public String getAsText() {
		if (Utils.hasLength(format)) {
			Object value = getValue();
			if (value != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				return sdf.format((Date) value);
			}
		}
		return "";
	}
	
}
