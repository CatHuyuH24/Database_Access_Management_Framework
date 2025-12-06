package com.dam.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the primary key field of an entity.
 * <p>
 * Each entity must have exactly one field annotated with @Id.
 * 
 * <pre>
 * {@code
 * @Entity
 * public class User {
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long id;
 *     // ...
 * }
 * }
 * </pre>
 * 
 * @see Entity
 * @see GeneratedValue
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
    // Marker annotation - no attributes needed
}
