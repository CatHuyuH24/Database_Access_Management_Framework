package com.dam.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the mapping for a database column.
 * <p>
 * If not specified, the field name will be used as the column name.
 * 
 * <pre>
 * {@code
 * @Column(name = "user_name", nullable = false, length = 100)
 * private String username;
 * }
 * </pre>
 * 
 * @see Entity
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    
    /**
     * The name of the column.
     * Defaults to the field name if not specified.
     * 
     * @return the column name
     */
    String name() default "";
    
    /**
     * Whether the column allows null values.
     * 
     * @return true if nullable, false otherwise
     */
    boolean nullable() default true;
    
    /**
     * The length of the column (for string types).
     * 
     * @return the column length
     */
    int length() default 255;
    
    /**
     * Whether the column has a unique constraint.
     * 
     * @return true if unique, false otherwise
     */
    boolean unique() default false;
}
