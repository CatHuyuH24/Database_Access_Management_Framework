package com.dam.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a database sequence for generating primary keys.
 * Used with {@code @GeneratedValue(strategy = GenerationType.SEQUENCE)}.
 * 
 * <pre>
 * {
 *   &#64;code
 *   &#64;Id
 *   &#64;GeneratedValue(strategy = GenerationType.SEQUENCE)
 *   @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq")
 *   private Long id;
 * }
 * </pre>
 * 
 * @see GeneratedValue
 * @see GenerationType#SEQUENCE
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SequenceGenerator {

  /**
   * A unique name for this sequence generator.
   * 
   * @return the generator name
   */
  String name() default "";

  /**
   * The name of the database sequence object.
   * If not specified, defaults to {@code <table_name>_<column_name>_seq}.
   * 
   * @return the sequence name in the database
   */
  String sequenceName() default "";

  /**
   * The initial value to be used for the sequence.
   * 
   * @return the initial value (default is 1)
   */
  int initialValue() default 1;
}
