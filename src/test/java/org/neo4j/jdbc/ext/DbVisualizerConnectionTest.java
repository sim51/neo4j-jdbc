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
package org.neo4j.jdbc.ext;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.jdbc.Connections;
import org.neo4j.jdbc.Neo4jJdbcTest;

/**
 * @author mh
 * @since 12.06.12
 */
@Ignore
public class DbVisualizerConnectionTest extends Neo4jJdbcTest
{
    public DbVisualizerConnectionTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @BeforeClass
    public static void setDBVisualizer() throws Exception
    {
        System.setProperty( Connections.DB_VIS, "true" );
    }

    @AfterClass
    public static void removeDBVisualizer()
    {
        System.clearProperty( Connections.DB_VIS );
    }

    @Test
    public void testExecuteQuery() throws Exception
    {
        conn.createStatement().executeQuery( DbVisualizerConnection.COLUMNS_QUERY + " \"foo\"" );
    }
}
