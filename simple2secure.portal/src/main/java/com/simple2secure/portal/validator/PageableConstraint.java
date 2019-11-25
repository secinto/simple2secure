package com.simple2secure.portal.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = PageableValidator.class)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PageableConstraint {
	String message() default "Invalid pagination";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int maxPerPage() default 20;
}
