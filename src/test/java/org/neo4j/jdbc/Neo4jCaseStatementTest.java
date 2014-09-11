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

import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.tooling.GlobalGraphOperations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * TODO
 */
public class Neo4jCaseStatementTest extends Neo4jJdbcTest
{

    private long nodeId;

    public Neo4jCaseStatementTest( Mode mode ) throws SQLException
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
        Statement statement = conn.createStatement();
        statement.executeUpdate( SETUP_QUERY );
        ResultSet rs = statement.executeQuery( QUERY );
        assertTrue( rs.next() );
        assertEquals( -1, ((Number) rs.getObject( "pathLength" )).intValue() );
        assertEquals( -1, rs.getInt( "pathLength" ) );
        assertFalse( rs.next() );
    }

    String QUERY = "MATCH (person1:Person {id:1}), (person2:Person {id:8})\n" +
            "OPTIONAL MATCH path = shortestPath((person1)-[:KNOWS]-(person2))\n" +
            "RETURN CASE path IS NULL WHEN true THEN -1 ELSE length(path) END AS pathLength";
    String SETUP_QUERY = "CREATE\n" +
            "(p0:Person {id:0}),\n" +
            "(p1:Person {id:1}),\n" +
            "(p2:Person {id:2}),\n" +
            "(p3:Person {id:3}),\n" +
            "(p4:Person {id:4}),\n" +
            "(p5:Person {id:5}),\n" +
            "(p6:Person {id:6}),\n" +
            "(p7:Person {id:7}),\n" +
            "(p8:Person {id:8}),\n" +
            "(p0)-[:KNOWS]->(p1),\n" +
            "(p1)-[:KNOWS]->(p3),\n" +
            "(p1)<-[:KNOWS]-(p2),\n" +
            "(p3)-[:KNOWS]->(p2),\n" +
            "(p2)<-[:KNOWS]-(p4),\n" +
            "(p4)-[:KNOWS]->(p7),\n" +
            "(p4)-[:KNOWS]->(p6),\n" +
            "(p6)<-[:KNOWS]-(p5)\n" +
            "\n";
}
