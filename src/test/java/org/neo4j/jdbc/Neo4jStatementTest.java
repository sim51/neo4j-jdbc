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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.tooling.GlobalGraphOperations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * TODO
 */
public class Neo4jStatementTest extends Neo4jJdbcTest
{

    private long nodeId;

    public Neo4jStatementTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        nodeId = createNode();
    }

    @Test
    public void testExecuteStatement() throws Exception
    {
        final ResultSet rs = conn.createStatement().executeQuery( nodeByIdQuery( nodeId ) );
        assertTrue( rs.next() );
        assertEquals( nodeId, ((Number) rs.getObject( "id" )).intValue() );
        assertEquals( nodeId, rs.getLong( "id" ) );
        assertEquals( nodeId, ((Number) rs.getObject( 1 )).intValue() );
        assertEquals(nodeId, rs.getLong(1));
        assertFalse( rs.next() );
    }

    @Test(expected = SQLException.class)
    public void testPreparedStatementMissingParameter() throws Exception
    {
        final PreparedStatement ps = conn.prepareStatement("start n=node({1}) return ID(n) as id");
        final ResultSet rs = ps.executeQuery();
        rs.next();
    }

    @Test
    public void testExecutePreparedStatement() throws Exception
    {
        final PreparedStatement ps = conn.prepareStatement( "start n=node({1}) return ID(n) as id" );
        ps.setLong( 1, nodeId );
        final ResultSet rs = ps.executeQuery();
        assertTrue( rs.next() );
        assertEquals( nodeId, ((Number) rs.getObject( "id" )).intValue() );
        assertEquals( nodeId, rs.getLong( "id" ) );
        assertEquals( nodeId, ((Number) rs.getObject( 1 )).intValue() );
        assertEquals( nodeId, rs.getLong( 1 ) );
        assertFalse(rs.next());
    }

    @Test
    public void testCreateNodeStatement() throws Exception
    {
        final PreparedStatement ps = conn.prepareStatement( "create (n:User {name:{1}})" );
        ps.setString(1, "test");
        // TODO int count = ps.executeUpdate();
        int count = 0;
        ps.executeUpdate();
        begin();
        for ( Node node : GlobalGraphOperations.at( gdb ).getAllNodesWithLabel( DynamicLabel.label( "User" ) ) )
        {
            assertEquals( "test", node.getProperty( "name" ) );
            count++;
        }
        done();
        assertEquals(1, count);
    }

    @Test
    public void testCreateNodeStatementWithMapParam() throws Exception
    {
        final PreparedStatement ps = conn.prepareStatement( "create (n:User {1})" );
        ps.setObject(1, map("name", "test"));
        // TODO int count = ps.executeUpdate();
        int count = 0;
        ps.executeUpdate();
        begin();
        for ( Node node : GlobalGraphOperations.at( gdb ).getAllNodesWithLabel( DynamicLabel.label( "User" ) ) )
        {
            assertEquals( "test", node.getProperty( "name" ) );
            count++;
        }
        done();
        assertEquals( 1, count );
    }

    @Test(expected = SQLException.class)
    public void testCreateOnReadonlyConnection() throws Exception
    {
        conn.setReadOnly(true);
        conn.createStatement().executeUpdate("create (n {name:{1}})");
    }

    @Test(expected = SQLDataException.class)
    public void testColumnZero() throws Exception
    {
        final ResultSet rs = conn.createStatement().executeQuery( nodeByIdQuery( nodeId ) );
        assertTrue( rs.next() );
        assertEquals(nodeId, rs.getObject(0));
        assertFalse(rs.next());
    }

    @Test(expected = SQLDataException.class)
    public void testColumnLargerThan() throws Exception
    {
        final ResultSet rs = conn.createStatement().executeQuery( nodeByIdQuery( nodeId ) );
        rs.next();
        rs.getObject(2);
    }

    @Test(expected = SQLException.class)
    public void testInvalidColumnName() throws Exception
    {
        final ResultSet rs = conn.createStatement().executeQuery( nodeByIdQuery( nodeId ) );
        rs.next();
        rs.getObject( "foo" );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMaxFieldSizeIsUnsupported() throws Exception
    {
        conn.createStatement().getMaxFieldSize();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetMaxFieldSizeIsUnsupported() throws Exception
    {
        conn.createStatement().setMaxFieldSize(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMaxRowsIsUnsupported() throws Exception
    {
        conn.createStatement().getMaxRows();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetMaxRowsIsUnsupported() throws Exception
    {
        conn.createStatement().setMaxRows(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetEscapeProcessingIsUnsupported() throws Exception
    {
        conn.createStatement().setEscapeProcessing(false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetQueryTimeoutIsUnsupported() throws Exception
    {
        conn.createStatement().getQueryTimeout();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetQueryTimeoutIsUnsupported() throws Exception
    {
        conn.createStatement().setQueryTimeout(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCancelIsUnsupported() throws Exception
    {
        conn.createStatement().cancel();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetCursorNameIsUnsupported() throws Exception
    {
        conn.createStatement().setCursorName("shouldNotWork");
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
    public void testGetFetchDirectionIsUnsupported() throws Exception
    {
        conn.createStatement().getFetchDirection();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetFetchSizeIsUnsupported() throws Exception
    {
        conn.createStatement().setFetchSize(50);
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
        conn.createStatement().addBatch("Doesn't work'");
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
