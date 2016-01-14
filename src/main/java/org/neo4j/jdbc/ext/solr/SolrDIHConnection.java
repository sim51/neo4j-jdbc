/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.jdbc.ext.solr;

import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.Neo4jConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * SolR specific Neo4j connection. Contains workarounds to get it to work with SolR.
 */
public class SolrDIHConnection
        extends Neo4jConnection
        implements Connection {

    public SolrDIHConnection(Driver driver, String url, Properties properties) throws SQLException {
        super(driver, url, properties);
    }

    @Override
    public Statement createStatement() throws SQLException
    {
        return debug( new Neo4jSolrStatement( this ) );
    }

    @Override
    public Statement createStatement( int resultSetType, int resultSetConcurrency ) throws SQLException
    {
        return debug( new Neo4jSolrStatement( this ) );
    }

    @Override
    public Statement createStatement( int resultSetType, int resultSetConcurrency, int resultSetHoldability ) throws
            SQLException
    {
        return debug( new Neo4jSolrStatement( this ) );
    }

}
