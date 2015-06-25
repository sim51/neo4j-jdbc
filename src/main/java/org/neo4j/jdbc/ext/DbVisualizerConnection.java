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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.Neo4jConnection;

/**
 * DbVisualizer specific Neo4j connection. Contains workarounds to get it to work with DbVisualizer.
 */
public class DbVisualizerConnection
        extends Neo4jConnection
        implements Connection
{

    public static final String COLUMNS_QUERY = "$columns$";

    public DbVisualizerConnection( Driver driver, String url, Properties properties ) throws SQLException
    {
        super( driver, url, properties );
    }

    @Override
    public ResultSet executeQuery( String query, Map<String, Object> parameters ) throws SQLException
    {
        if ( query.contains( COLUMNS_QUERY ) )
        {
            int idx = query.indexOf( "\"" );
            int idx2 = query.indexOf( "\"", idx + 1 );
            final String type = query.substring( idx + 1, idx2 );

            String columnsQuery = super.tableColumns( type, "instance." );
//                return new ListResultSet("", columns,this);
//                query = query.replace(COLUMNS_QUERY, columnsQuery);
        }

        return super.executeQuery( query, parameters );
    }
}
