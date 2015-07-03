package com.helpinput.spring.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Property {

	String name();
	String value();
	
}
