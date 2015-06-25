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
import java.sql.Statement;

import org.junit.Test;

public class Neo4jDatabaseLockingDataTest extends Neo4jJdbcTest
{

    public Neo4jDatabaseLockingDataTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test
    public void testTryLock() throws SQLException
    {
        createNode();
        for ( int i = 0; i < 15; i++ )
        {
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery( "MATCH (n:Root {name:'root'}) RETURN n" );
            if ( rs1.next() )
            {
                Object value = rs1.getObject( "n" );
                System.out.println( i + ". value = " + value );
            }
            rs1.close();
            stmt1.close();
        }
    }
}
