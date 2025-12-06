package com.dam.framework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for DAM Framework.
 */
class DAMFrameworkTest {
    
    @Test
    void testAnnotationsExist() {
        // Verify annotation classes are available
        assertNotNull(com.dam.framework.annotations.Entity.class);
        assertNotNull(com.dam.framework.annotations.Table.class);
        assertNotNull(com.dam.framework.annotations.Id.class);
        assertNotNull(com.dam.framework.annotations.Column.class);
        assertNotNull(com.dam.framework.annotations.GeneratedValue.class);
    }
    
    @Test
    void testInterfacesExist() {
        // Verify core interfaces are available
        assertNotNull(com.dam.framework.session.Session.class);
        assertNotNull(com.dam.framework.session.SessionFactory.class);
        assertNotNull(com.dam.framework.query.Query.class);
        assertNotNull(com.dam.framework.dialect.Dialect.class);
        assertNotNull(com.dam.framework.transaction.Transaction.class);
    }
}
