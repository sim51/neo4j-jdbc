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
