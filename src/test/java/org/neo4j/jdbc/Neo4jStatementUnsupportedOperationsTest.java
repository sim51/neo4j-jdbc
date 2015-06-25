/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.junit.Test;

public class Neo4jStatementUnsupportedOperationsTest extends Neo4jJdbcTest
{

    public Neo4jStatementUnsupportedOperationsTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testGetMaxFieldSizeIsUnsupported() throws Exception
    {
        conn.createStatement().getMaxFieldSize();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetMaxFieldSizeIsUnsupported() throws Exception
    {
        conn.createStatement().setMaxFieldSize( 1 );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testGetMaxRowsIsUnsupported() throws Exception
    {
        conn.createStatement().getMaxRows();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetMaxRowsIsUnsupported() throws Exception
    {
        conn.createStatement().setMaxRows( 1 );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetEscapeProcessingIsUnsupported() throws Exception
    {
        conn.createStatement().setEscapeProcessing( false );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testGetQueryTimeoutIsUnsupported() throws Exception
    {
        conn.createStatement().getQueryTimeout();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetQueryTimeoutIsUnsupported() throws Exception
    {
        conn.createStatement().setQueryTimeout( 0 );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testCancelIsUnsupported() throws Exception
    {
        conn.createStatement().cancel();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetCursorNameIsUnsupported() throws Exception
    {
        conn.createStatement().setCursorName( "shouldNotWork" );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testGetUpdateCountIsUnsupported() throws Exception
    {
        conn.createStatement().getUpdateCount();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetFetchDirectionIsUnsupported() throws Exception
    {
        conn.createStatement().setFetchDirection(ResultSet.FETCH_FORWARD);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetFetchSizeIsUnsupported() throws Exception
    {
        conn.createStatement().setFetchSize( 50 );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testGetFetchSizeIsUnsupported() throws Exception
    {
        conn.createStatement().getFetchSize();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testAddBatchIsUnsupported() throws Exception
    {
        conn.createStatement().addBatch( "Does not work" );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testClearBatchIsUnsupported() throws Exception
    {
        conn.createStatement().clearBatch();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testExecuteBatchIsUnsupported() throws Exception
    {
        conn.createStatement().executeBatch();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetPoolableIsUnsupported() throws Exception
    {
        conn.createStatement().setPoolable( false );
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testCloseOnCompletionIsUnsupported() throws Exception
    {
        conn.createStatement().closeOnCompletion();
    }

}
