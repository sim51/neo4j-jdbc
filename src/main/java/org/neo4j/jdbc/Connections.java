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

import java.sql.SQLException;
import java.util.Properties;

import org.neo4j.jdbc.ext.DbVisualizerConnection;
import org.neo4j.jdbc.ext.IntelliJConnection;
import org.neo4j.jdbc.ext.LibreOfficeConnection;

/**
 * @author mh
 * @since 12.06.12
 */
public enum Connections
{
    OpenOffice()
            {
                protected boolean matches( Properties sysProps )
                {
                    return sysProps.containsKey( "org.openoffice.native" );
                }

                protected Neo4jConnection doCreate( Driver driver, String url, Properties p ) throws SQLException
                {
                    return new LibreOfficeConnection( driver, url, p );
                }
            }, IntelliJ()
        {
            @Override
            protected boolean matches( Properties sysProps )
            {
                return sysProps.getProperty( "user.dir" ).contains( "IntelliJ" );
            }

            @Override
            protected Neo4jConnection doCreate( Driver driver, String url, Properties p ) throws SQLException
            {
                return new IntelliJConnection( driver, url, p );
            }
        }, DbVisualizer()
        {
            @Override
            protected boolean matches( Properties sysProps )
            {
                return sysProps.containsKey( DB_VIS );
            }

            @Override
            protected Neo4jConnection doCreate( Driver driver, String url, Properties p ) throws SQLException
            {
                return new DbVisualizerConnection( driver, url, p );
            }
        }, Default()
        {
            @Override
            protected boolean matches( Properties sysProps )
            {
                return true;
            }

            @Override
            protected Neo4jConnection doCreate( Driver driver, String url, Properties p ) throws SQLException
            {
                return new Neo4jConnection( driver, url, p );
            }
        };

    public static final String DB_VIS = "dbvis.ScriptsTreeShowDetails";

    protected abstract boolean matches( Properties sysProps );

    public static Neo4jConnection create( Driver driver, String url, Properties p ) throws SQLException
    {
        final Properties sysProps = System.getProperties();
        for ( Connections connections : values() )
        {
            if ( connections.matches( sysProps ) )
            {
                final Neo4jConnection con = connections.doCreate( driver, url, p );
                return debug( con, hasDebug( p ) );
            }
        }
        throw new SQLException( "Couldn't create connection for " + url + " properties " + p );
    }

    public static boolean hasDebug( Properties properties )
    {
        return "true".equalsIgnoreCase( properties.getProperty( "debug", "false" ) );
    }

    @SuppressWarnings("unchecked")
    public static <T> T debug( T obj, boolean debug )
    {
        if ( debug )
        {
            Class[] interfaces = obj.getClass().getInterfaces();
            if ( interfaces != null && interfaces.length > 0 )
            {
                return (T) CallProxy.proxy( interfaces[0], obj );
            }
            return obj;

        }
        else
        {
            return obj;
        }
    }

    protected abstract Neo4jConnection doCreate( Driver driver, String url, Properties p ) throws SQLException;
}
