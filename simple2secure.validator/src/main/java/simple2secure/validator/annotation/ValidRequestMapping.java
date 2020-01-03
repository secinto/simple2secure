package simple2secure.validator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import simple2secure.validator.model.ValidRequestMethodType;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidRequestMapping {

	ValidRequestMethodType method() default ValidRequestMethodType.GET;

	String[] consumes() default {};

	String[] produces() default {};

	String value() default "";

}
