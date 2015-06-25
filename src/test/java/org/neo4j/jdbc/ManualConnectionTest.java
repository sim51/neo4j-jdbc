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
import java.util.Properties;

import static org.junit.Assert.assertEquals;

import static org.neo4j.helpers.collection.MapUtil.map;

/**
 * @author mh
 * @since 03.12.12
 */
public class ManualConnectionTest
{
    public static void main( String[] args ) throws SQLException
    {
        final Driver driver = new Driver();
        final Properties props = new Properties();
        if ( args.length > 1 )
        {
            props.put( "user", args[1] );
        }
        if ( args.length > 2 )
        {
            props.put( "password", args[2] );
        }
        final String hostPort = args[0];
        Neo4jConnection conn = driver.connect( "jdbc:neo4j://" + hostPort, props );
        final long id = 0L;
        final ResultSet rs = conn.executeQuery( "match (n) where id(n)={id} return id(n) as id", map( "id",
                id ) );
        while ( rs.next() )
        {
            assertEquals( id, rs.getLong( "id" ) );
        }
    }
}
