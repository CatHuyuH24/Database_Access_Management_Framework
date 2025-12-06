package com.dam.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a database entity.
 * <p>
 * Classes annotated with @Entity will be mapped to database tables.
 * The entity class must have:
 * <ul>
 *   <li>A no-argument constructor</li>
 *   <li>At least one field annotated with @Id</li>
 * </ul>
 * 
 * <pre>
 * {@code
 * @Entity
 * @Table(name = "users")
 * public class User {
 *     @Id
 *     private Long id;
 *     private String name;
 *     // getters and setters
 * }
 * }
 * </pre>
 * 
 * @see Table
 * @see Id
 * @see Column
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    // Marker annotation - no attributes needed
}
