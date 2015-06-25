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

import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO
 */
public class Neo4jDatabaseMetaDataTest extends Neo4jJdbcTest
{

    public Neo4jDatabaseMetaDataTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test
    public void testGetTables() throws SQLException
    {
        createTableMetaData( gdb );
        ResultSet rs = conn.getMetaData().getTables( null, null, "%", null );

        System.out.println( rs );
    }

    private void createTableMetaData( GraphDatabaseService gdb )
    {
        try ( Transaction tx = gdb.beginTx() )
        {
            final Node tables = gdb.createNode();
            final Node table = gdb.createNode();
            final Node column = gdb.createNode();
            tx.success();
        }
    }

    @Test
    public void testGetColumns() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getColumns( null, null, "%", null );

        System.out.println( rs );
    }
}
