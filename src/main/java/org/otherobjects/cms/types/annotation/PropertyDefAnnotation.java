package org.otherobjects.cms.types.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.Ordered;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PropertyDefAnnotation {

    PropertyType type() default PropertyType.STRING;

    String label() default "";

    String description() default "";

    boolean required() default false;

    int size() default -1;

    String valang() default "";

    String help() default "";

    PropertyType collectionElementType() default PropertyType.REFERENCE;

    // TODO Can we have null defaults or make this optional
    // TODO Should this type be a class? Prob not for the sake or linking to DynaNodes. Maybe a relatedTypeClass aswell?
    String relatedType() default "";
    
    int order() default Ordered.LOWEST_PRECEDENCE;

}