package org.flymine.objectstore;

import java.util.List;

import org.flymine.objectstore.query.Query;
import org.flymine.objectstore.query.Results;

import org.flymine.sql.query.ExplainResult;

/**
 * Gets the Results of a Query from an underlying store.
 *
 * @author Andrew Varley
 */
public interface ObjectStore
{

    /**
     * Execute a Query on this ObjectStore
     *
     * @param q the Query to execute
     * @return the results of the Query
     * @throws ObjectStoreException if an error occurs during the running of the Query
     */
    public Results execute(Query q) throws ObjectStoreException;

    /**
     * Execute a Query on this ObjectStore, asking for a certain range of rows to be returned.
     * This will usually only be called by the Results object returned from
     * <code>execute(Query q)</code>.
     *
     * @param q the Query to execute
     * @param start the start row
     * @param limit the maximum number of rows to return
     * @return a List of ResultRows
     * @throws ObjectStoreException if an error occurs during the running of the Query
     */
    public List execute(Query q, int start, int limit) throws ObjectStoreException;


    /**
     * Runs an EXPLAIN on the query without ant LIMIT or OFFSET.
     *
     * @param q the query to estimate rows for
     * @return parsed results of EXPLAIN
     * @throws ObjectStoreException if an error occurs explining the query
     */
    public ExplainResult estimate(Query q) throws ObjectStoreException;

    /**
     * Runs an EXPLAIN for the given query with specified start and limit parameters.  This
     * gives estimated time for a single 'page' of the query.
     *
     * @param q the query to explain
     * @param start first row required, numbered from zero
     * @param limit the maximum number og rows to return
     * @return parsed results of EXPLAIN
     * @throws ObjectStoreException if an error occurs explining the query
     */
    public ExplainResult estimate(Query q, int start, int limit) throws ObjectStoreException;

    /**
     * Execute a COUNT(*) on a query, returns the number of row the query will produce
     *
     * @param q Flymine Query on which to run COUNT(*)
     * @return the number of row to be produced by query
     */
    public int count(Query q);

}
