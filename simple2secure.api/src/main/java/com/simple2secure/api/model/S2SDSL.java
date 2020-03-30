package com.simple2secure.api.model;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface S2SDSL {
	public String value() default "";
}
