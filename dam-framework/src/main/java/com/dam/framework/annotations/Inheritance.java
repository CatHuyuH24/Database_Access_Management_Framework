package com.dam.framework.annotations;


import static com.dam.framework.mapping.entity.InheritanceStrategyType.SINGLE_TABLE;

import com.dam.framework.mapping.entity.InheritanceStrategyType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Inheritance {
    InheritanceStrategyType strategy() default SINGLE_TABLE;
}