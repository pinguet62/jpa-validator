package fr.pinguet62.jpavalidator.util.runner;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ METHOD })
public @interface Script {

    /** The SQL script to execute. */
    String[] value();

}