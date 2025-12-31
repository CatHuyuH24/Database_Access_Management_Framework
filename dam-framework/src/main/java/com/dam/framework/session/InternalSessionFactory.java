package com.dam.framework.session;

import com.dam.framework.mapping.EntityMetadata;

interface InternalSessionFactory {
    /**
     * Called by Session implementation class, session.close() to release connection
     * and cleanup tracking.
     */
    void sessionClosed(Session session);

    /**
     * Get metadata for the specified entity class.
     * 
     * @param entityClass the entity class
     * @return the metadata for the class, or null if not registered
     */
    EntityMetadata getMetadata(Class<?> entityClass);
}
