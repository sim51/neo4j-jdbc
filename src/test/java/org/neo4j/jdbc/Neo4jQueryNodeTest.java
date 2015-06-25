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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import static org.hamcrest.CoreMatchers.is;

public class Neo4jQueryNodeTest extends Neo4jJdbcTest
{

    public Neo4jQueryNodeTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test
    public void testGetTables() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getTables( null, null, "%", null );

        while ( rs.next() )
        {
            System.out.println( rs.getString( "TABLE_NAME" ) );
        }
    }

    @Test
    public void testRetrieveNodes() throws SQLException
    {
        createData( gdb );
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( "match (n) optional match p=(n)-[r]-(m) return n,r,m,p,ID(n)," +
                "length(p),n.name as name limit 5" );
        int count = 0;
        ResultSetMetaData metaData = rs.getMetaData();
        int cols = metaData.getColumnCount();
        Assert.assertThat( cols, is( 7 ) );
        while ( rs.next() )
        {
            for ( int i = 1; i <= cols; i++ )
            {
                //String columnName = metaData.getColumnName(i);
                //System.out.println(columnName);
                System.out.print( rs.getObject( i ) + "\t" );
            }
            System.out.println();
            count++;
        }

        Assert.assertThat( count, is( 5 ) );
    }

    private void createData( GraphDatabaseService gdb )
    {
        try ( Transaction tx = gdb.beginTx() )
        {
            final Node n1 = gdb.createNode();
            n1.setProperty( "name", "n1" );
            final Node n2 = gdb.createNode();
            final Node n3 = gdb.createNode();
            final Node n4 = gdb.createNode();
            final Node n5 = gdb.createNode();
            final Relationship rel1 = n1.createRelationshipTo( n2, DynamicRelationshipType.withName( "REL" ) );
            rel1.setProperty( "name", "rel1" );
            tx.success();
        }
    }
}

