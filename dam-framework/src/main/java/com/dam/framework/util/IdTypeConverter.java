package com.dam.framework.util;

/**
 * Utility class for converting ID values to appropriate Java types.
 * <p>
 * Applies the Template Method pattern by providing a common conversion
 * algorithm that handles numeric ID type conversions consistently
 * across the framework (SequenceStyleGenerator, SessionImpl, etc.).
 * 
 * <h3>Supported Conversions:</h3>
 * <ul>
 * <li>Any Number → Long/long</li>
 * <li>Any Number → Integer/int</li>
 * <li>Pass-through for String, UUID, and other types</li>
 * </ul>
 * 
 * @see com.dam.framework.mapping.SequenceStyleGenerator
 * @see com.dam.framework.session.SessionImpl
 */
public final class IdTypeConverter {

    private IdTypeConverter() {
        // Utility class - prevent instantiation
    }

    /**
     * Convert an ID value to the target type.
     * <p>
     * This is the template method that provides consistent ID type conversion
     * across all ID generators and session operations.
     * 
     * @param value      the raw ID value (typically from database or generator)
     * @param targetType the expected Java type for the ID field
     * @return the converted value matching the target type
     */
    public static Object convert(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // Handle numeric conversions
        if (value instanceof Number number) {
            return convertNumber(number, targetType);
        }

        // For non-numeric types (String, UUID, etc.), return as-is
        return value;
    }

    /**
     * Convert a Number to the appropriate numeric ID type.
     * 
     * @param number     the number to convert
     * @param targetType the target numeric type
     * @return the converted number
     */
    private static Object convertNumber(Number number, Class<?> targetType) {
        // Long conversion
        if (targetType == Long.class || targetType == long.class) {
            return number instanceof Long ? number : number.longValue();
        }

        // Integer conversion
        if (targetType == Integer.class || targetType == int.class) {
            return number instanceof Integer ? number : number.intValue();
        }

        // Short conversion (less common but supported)
        if (targetType == Short.class || targetType == short.class) {
            return number instanceof Short ? number : number.shortValue();
        }

        // BigInteger/BigDecimal - return as-is if already correct type
        if (targetType.isInstance(number)) {
            return number;
        }

        // Default: return the number as-is
        return number;
    }

    /**
     * Check if the given type is a valid numeric ID type.
     * 
     * @param type the type to check
     * @return true if the type can hold numeric IDs
     */
    public static boolean isNumericIdType(Class<?> type) {
        return type == Long.class || type == long.class ||
                type == Integer.class || type == int.class ||
                type == Short.class || type == short.class ||
                Number.class.isAssignableFrom(type);
    }

    /**
     * Check if the given type is compatible with UUID generation.
     * 
     * @param type the type to check
     * @return true if the type can hold UUID values
     */
    public static boolean isUuidCompatibleType(Class<?> type) {
        return type == String.class || type == java.util.UUID.class;
    }
}
