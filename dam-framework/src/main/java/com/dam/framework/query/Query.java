package com.dam.framework.query;

import java.util.List;

/**
 * Interface for building and executing database queries.
 * <p>
 * Provides a fluent API for constructing SELECT queries with various clauses.
 *
 * <pre>
 * {@code
 * List<User> users = session.createQuery(User.class)
 *         .where("age > ?", 18)
 *         .and("status = ?", "active")
 *         .orderBy("name", Order.ASC)
 *         .limit(10)
 *         .getResultList();
 * }
 * </pre>
 *
 * @param <T> the entity type being queried
 * @see com.dam.framework.session.Session#createQuery(Class)
 */
public interface Query<T> {

    /**
     * Specify the columns to select. If not called, all columns are selected.
     *
     * @param columns the column names to select
     * @return this Query for method chaining
     */
    Query<T> select(String... columns);

    /**
     * Add a WHERE condition.
     *
     * @param condition the condition with parameter placeholders (?)
     * @param params    the parameter values
     * @return this Query for method chaining
     */
    Query<T> where(String condition, Object... params);

    /**
     * Add an AND condition to the WHERE clause.
     *
     * @param condition the condition with parameter placeholders (?)
     * @param params    the parameter values
     * @return this Query for method chaining
     */
    Query<T> and(String condition, Object... params);

    /**
     * Add an OR condition to the WHERE clause.
     *
     * @param condition the condition with parameter placeholders (?)
     * @param params    the parameter values
     * @return this Query for method chaining
     */
    Query<T> or(String condition, Object... params);

    /**
     * Add GROUP BY clause.
     *
     * @param columns the columns to group by
     * @return this Query for method chaining
     */
    Query<T> groupBy(String... columns);

    /**
     * Add HAVING clause for filtering grouped results.
     *
     * @param condition the having condition
     * @param params    the parameter values
     * @return this Query for method chaining
     */
    Query<T> having(String condition, Object... params);

    /**
     * Add ORDER BY clause.
     *
     * @param column the column to order by
     * @param order  the sort order (ASC or DESC)
     * @return this Query for method chaining
     */
    Query<T> orderBy(String column, Order order);

    /**
     * Set the maximum number of results to return.
     *
     * @param limit the maximum number of results
     * @return this Query for method chaining
     */
    Query<T> limit(int limit);

    /**
     * Set the offset for pagination.
     *
     * @param offset the number of results to skip
     * @return this Query for method chaining
     */
    Query<T> offset(int offset);

    /**
     * Execute the query and return a single result.
     *
     * @return the exact single result
     * @throws com.dam.framework.exception.DAMException if multiple results or no
     *                                                  results exist
     * 
     */
    T getSingleResult();

    /**
     * Execute the query and return all results.
     *
     * @return list of results (empty list if no results)
     */
    List<T> getResultList();
}