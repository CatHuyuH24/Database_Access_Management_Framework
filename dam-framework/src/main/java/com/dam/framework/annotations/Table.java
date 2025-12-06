package com.dam.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the database table name for an entity.
 * <p>
 * If not specified, the class name will be used as the table name.
 * 
 * <pre>
 * {@code
 * @Entity
 * @Table(name = "users", schema = "public")
 * public class User {
 *     // ...
 * }
 * }
 * </pre>
 * 
 * @see Entity
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    
    /**
     * The name of the table.
     * Defaults to the class name if not specified.
     * 
     * @return the table name
     */
    String name() default "";
    
    /**
     * The schema of the table.
     * 
     * @return the schema name
     */
    String schema() default "";
}
