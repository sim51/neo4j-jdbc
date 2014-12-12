/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class Neo4jStatementUnsupportedOperationsTest extends Neo4jJdbcTest
{

    public Neo4jStatementUnsupportedOperationsTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMaxFieldSizeIsUnsupported() throws Exception
    {
        conn.createStatement().getMaxFieldSize();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetMaxFieldSizeIsUnsupported() throws Exception
    {
        conn.createStatement().setMaxFieldSize( 1 );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMaxRowsIsUnsupported() throws Exception
    {
        conn.createStatement().getMaxRows();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetMaxRowsIsUnsupported() throws Exception
    {
        conn.createStatement().setMaxRows( 1 );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetEscapeProcessingIsUnsupported() throws Exception
    {
        conn.createStatement().setEscapeProcessing( false );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetQueryTimeoutIsUnsupported() throws Exception
    {
        conn.createStatement().getQueryTimeout();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetQueryTimeoutIsUnsupported() throws Exception
    {
        conn.createStatement().setQueryTimeout( 0 );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCancelIsUnsupported() throws Exception
    {
        conn.createStatement().cancel();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetCursorNameIsUnsupported() throws Exception
    {
        conn.createStatement().setCursorName( "shouldNotWork" );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetUpdateCountIsUnsupported() throws Exception
    {
        conn.createStatement().getUpdateCount();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetFetchDirectionIsUnsupported() throws Exception
    {
        conn.createStatement().setFetchDirection(ResultSet.FETCH_FORWARD);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetFetchSizeIsUnsupported() throws Exception
    {
        conn.createStatement().setFetchSize( 50 );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetFetchSizeIsUnsupported() throws Exception
    {
        conn.createStatement().getFetchSize();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetResultSetConcurrencyIsUnsupported() throws Exception
    {
        conn.createStatement().getResultSetConcurrency();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddBatchIsUnsupported() throws Exception
    {
        conn.createStatement().addBatch( "Does not work" );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClearBatchIsUnsupported() throws Exception
    {
        conn.createStatement().clearBatch();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExecuteBatchIsUnsupported() throws Exception
    {
        conn.createStatement().executeBatch();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetPoolableIsUnsupported() throws Exception
    {
        conn.createStatement().setPoolable( false );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCloseOnCompletionIsUnsupported() throws Exception
    {
        conn.createStatement().closeOnCompletion();
    }

}
