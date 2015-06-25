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
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * @author mh
 * @since 27.04.13
 */
public class TransactionTest extends Neo4jJdbcTest
{

    public TransactionTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test
    public void testCreateNodeWithAutoCommit() throws Exception
    {
        final ResultSet rs = conn.executeQuery( "create (n {name:'Andres'}) return id(n)", null );
        rs.next();
        final long nodeId = rs.getLong( 1 );
        begin();
        assertEquals( nodeId, gdb.getNodeById( nodeId ).getId() );
        done();
    }

    @Test
    public void testAutoCommit() throws Exception
    {
        conn.setAutoCommit( true );
        final ResultSet rs = conn.executeQuery( "create (n {name:'Andres'}) return id(n)", null );
        rs.next();
        final long nodeId = rs.getLong( 1 );
        begin();
        assertEquals( nodeId, gdb.getNodeById( nodeId ).getId() );
        done();
    }

    @Test
    public void testManualCommit() throws Exception
    {
        try
        {
            conn.setAutoCommit( false );
            final ResultSet rs = conn.executeQuery( "create (n {name:'Andres'}) return id(n)", null );
            rs.next();
            final long nodeId = rs.getLong( 1 );
            assertNodeExists( nodeId, true );
            assertNodeVisibleInOtherTransaction( nodeId, false );
            conn.commit();
            assertNodeVisibleInOtherTransaction( nodeId, true );
            assertNodeExists( nodeId, true );
        }
        catch ( SQLException sqle )
        {
            if ( sqle.getMessage().contains( "not supported" ) )
            {
                return;
            }
            throw sqle;
        }
    }

    @Test
    public void testSetAutoCommitOnCommitsAccordingToSpec() throws Exception
    {
        try
        {
            conn.setAutoCommit( false );
            final ResultSet rs = conn.executeQuery( "create (n {name:'Andres'}) return id(n)", null );
            rs.next();
            final long nodeId = rs.getLong( 1 );
            assertNodeExists( nodeId, true );
            assertNodeVisibleInOtherTransaction( nodeId, false );
            conn.setAutoCommit( true );
            assertNodeVisibleInOtherTransaction( nodeId, true );
            assertNodeExists( nodeId, true );
        }
        catch ( SQLException sqle )
        {
            if ( sqle.getMessage().contains( "not supported" ) )
            {
                return;
            }
            throw sqle;
        }
    }

    @Test
    public void testManualRollback() throws Exception
    {
        try
        {
            conn.setAutoCommit( false );
            final ResultSet rs = conn.executeQuery( "create (n {name:'Andres'}) return id(n)", null );
            rs.next();
            final long nodeId = rs.getLong( 1 );
            assertNodeExists( nodeId, true );
            assertNodeVisibleInOtherTransaction( nodeId, false );
            conn.rollback();
            assertNodeVisibleInOtherTransaction( nodeId, false );
            assertNodeExists( nodeId, false );
        }
        catch ( SQLException sqle )
        {
            if ( sqle.getMessage().contains( "not supported" ) )
            {
                return;
            }
            throw sqle;
        }
    }

    private void assertNodeExists( long nodeId, boolean exists ) throws SQLException
    {
        // work around the automatic node-by-id lookup in cypher
        final ResultSet found = conn.executeQuery( "match (n) where has(n.name) AND id(n) = {id} return id(n)",
                map( "id", nodeId ) );
        assertEquals( "node exists " + nodeId, exists, found.next() );
        if ( exists )
        {
            assertEquals( exists, nodeId == found.getLong( 1 ) );
        }
    }

    private void assertNodeVisibleInOtherTransaction( final long nodeId, final boolean exists ) throws
            InterruptedException
    {
        final AtomicBoolean valid = new AtomicBoolean();
        final Thread t = new Thread()
        {
            public void run()
            {
                try
                {
                    assertNodeExists( nodeId, exists );
                    valid.set( true );
                }
                catch ( SQLException e )
                {
                    // ignore
                }
            }
        };
        t.start();
        t.join();
        assertEquals( "Node found in a separate thread", true, valid.get() );
    }

    @After
    public void unsetManualCommitMode() throws Exception
    {
        conn.setAutoCommit( true );
    }
}
