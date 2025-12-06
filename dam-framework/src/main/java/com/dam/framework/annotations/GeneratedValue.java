package com.dam.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the primary key value should be generated automatically.
 * 
 * <pre>
 * {@code
 * @Id
 * @GeneratedValue(strategy = GenerationType.IDENTITY)
 * private Long id;
 * }
 * </pre>
 * 
 * @see Id
 * @see GenerationType
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedValue {
    
    /**
     * The strategy for generating primary key values.
     * 
     * @return the generation strategy
     */
    GenerationType strategy() default GenerationType.IDENTITY;
}
