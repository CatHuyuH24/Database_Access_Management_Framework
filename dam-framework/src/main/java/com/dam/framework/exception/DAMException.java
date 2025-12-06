package com.dam.framework.exception;

/**
 * Base exception for all DAM Framework exceptions.
 * <p>
 * This is a runtime exception to avoid cluttering code with try-catch blocks.
 */
public class DAMException extends RuntimeException {
    
    /**
     * Creates a new DAMException with the specified message.
     * 
     * @param message the detail message
     */
    public DAMException(String message) {
        super(message);
    }
    
    /**
     * Creates a new DAMException with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the underlying cause
     */
    public DAMException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a new DAMException with the specified cause.
     * 
     * @param cause the underlying cause
     */
    public DAMException(Throwable cause) {
        super(cause);
    }
}
