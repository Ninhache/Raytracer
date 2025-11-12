package fr.ninhache.raytracer.parser;

import java.lang.annotation.*;

/**
 * Annotation pour marquer une classe comme handler de token.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TokenHandler {
    String value();
    int priority() default 100;
}

