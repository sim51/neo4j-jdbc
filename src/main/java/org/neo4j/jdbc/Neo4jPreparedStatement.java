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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of PreparedStatement. Parameters in Cypher queries have to be done as {nr}, as calls to methods
 * here will be saved in a parameter map with "nr"-&gt;value, since JDBC does not support named parameters.
 */
public class Neo4jPreparedStatement extends AbstractPreparedStatement
{
    private String query;
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public Neo4jPreparedStatement( Neo4jConnection connection, String query )
    {
        super( connection );
        this.query = replacePlaceholders( query );
    }

    private static final String replacePlaceholders( final String input )
    {
        final char placeholder = '?';
        final char quote = '"';
        final char escape = '\\';
        final String format = "{%d}";
        int i = 1;
        boolean inQuote = false;
        boolean escaped = false;
        StringBuffer sb = new StringBuffer( input.length() );
        for ( char c : input.toCharArray() )
        {
            if ( inQuote )
            {
                if (escaped)
                {
                    escaped = false;
                    sb.append( escape );
                    sb.append( c );
                }
                else
                {
                    switch ( c )
                    {
                        case escape:
                            escaped = true;
                            break;
                        case quote:
                            inQuote = false;
                            // fall through
                        default:
                            sb.append( c );
                            break;
                    }
                }
            }
            else
            {
                switch ( c )
                {
                    case placeholder:
                        sb.append( String.format( format, i++ ) );
                        break;
                    case quote:
                        inQuote = true;
                        // fall through
                    default:
                        sb.append( c );
                        break;
                }
            }
        }
        return sb.toString();
    }

    @Override
    public ResultSet executeQuery() throws SQLException
    {
        resultSet = connection.executeQuery( query, parameters );
        return resultSet;
    }

    @Override
    public boolean execute() throws SQLException
    {
        resultSet = connection.executeQuery( query, parameters );
        return true;
    }

    @Override
    public int executeUpdate() throws SQLException
    {
        resultSet = connection.executeQuery( query, parameters );
        while ( resultSet.next() )
        {
            ;
        }
        return 0; // todo
    }

    private void add( int parameterIndex, Object value )
    {
        parameters.put( Integer.toString( parameterIndex ), value );
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        if ( resultSet == null )
        {
            execute();
        }

        return resultSet.getMetaData();
    }

    @Override
    public void setNull( int parameterIndex, int sqlType ) throws SQLException
    {
        add( parameterIndex, null );
        // todo is that the correct behaviour for cypher ?
        // parameters.remove(Integer.toString(parameterIndex));
    }

    @Override
    public void setBoolean( int parameterIndex, boolean value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setByte( int parameterIndex, byte value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setShort( int parameterIndex, short value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setInt( int parameterIndex, int value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setLong( int parameterIndex, long value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setFloat( int parameterIndex, float value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setDouble( int parameterIndex, double value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setBigDecimal( int parameterIndex, BigDecimal value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setString( int parameterIndex, String value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setBytes( int parameterIndex, byte[] value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void clearParameters() throws SQLException
    {
        parameters.clear();
    }

    @Override
    public void setObject( int parameterIndex, Object value, int targetSqlType ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setObject( int parameterIndex, Object value ) throws SQLException
    {
        add( parameterIndex, value );
    }

    @Override
    public void setNull( int parameterIndex, int sqlType, String typeName ) throws SQLException
    {
        add( parameterIndex, null );
        // parameters.remove(Integer.toString(parameterIndex));
    }

    @Override
    public void setObject( int parameterIndex, Object value, int targetSqlType, int scaleOrLength ) throws SQLException
    {
        add( parameterIndex, value );
    }
}
