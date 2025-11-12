package fr.ninhache.raytracer.parser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface FileFormatParser {
    String[] extensions();
    String description() default "";
    int priority() default 100;
}
