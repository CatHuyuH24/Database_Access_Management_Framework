package com.dam.framework.mapping;

import com.dam.framework.annotations.GenerationType;
import com.dam.framework.exception.DAMException;

/**
 * Factory for creating ID generators based on GenerationType strategy.
 * <p>
 * Implements the Factory Pattern to encapsulate generator creation logic.
 */
public class IdGeneratorFactory {

  /**
   * Create an ID generator for the specified strategy.
   * 
   * @param strategy the generation strategy
   * @return the appropriate ID generator
   * @throws DAMException if strategy is not supported
   */
  public static IdGenerator createGenerator(GenerationType strategy) {
    if (strategy == null) {
      throw new DAMException("GenerationType cannot be null");
    }

    return switch (strategy) {
      case IDENTITY -> new IdentityGenerator();
      case SEQUENCE -> new SequenceStyleGenerator();
      case UUID -> new UUIDGenerator();
      case TABLE -> throw new DAMException(
          "TABLE generation strategy not yet implemented. Use IDENTITY, SEQUENCE, or UUID instead.");
      case NONE -> throw new DAMException(
          "NONE strategy does not use auto-generation. Set ID value manually.");
    };
  }

  /**
   * Check if the given strategy requires auto-generation.
   * 
   * @param strategy the generation strategy
   * @return true if strategy auto-generates IDs
   */
  public static boolean requiresGeneration(GenerationType strategy) {
    return strategy != null && strategy != GenerationType.NONE;
  }
}
