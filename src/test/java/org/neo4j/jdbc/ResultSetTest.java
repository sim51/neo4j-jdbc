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
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author mh
 * @since 13.06.12
 */
@RunWith(Parameterized.class)
public class ResultSetTest
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String DATE_STRING = "2014-12-24 13:54";
    private final ResultSet rs;
    private static List<Object> row;

    @SuppressWarnings("unchecked")
    @Parameterized.Parameters
    public static Collection<ResultSet[]> data() throws Exception
    {
        long testDate = parseDate( DATE_STRING ).getTime();
        row = Arrays.<Object>asList( "0", 1, (short) 2, 3L, (byte) 4, 5f, 6d, BigDecimal.valueOf( 7L ), null,
                new String[]{"array"}, Collections.singletonMap( "a", 1 ), Collections.singletonList( "list" ),
                testDate,testDate, testDate,"string"
                );

        List<Neo4jColumnMetaData> columns = asList(
                col( "String", Types.VARCHAR ),
                col( "int", Types.INTEGER ),
                col( "short", Types.SMALLINT ),
                col( "long", Types.BIGINT ),
                col( "byte", Types.TINYINT ),
                col( "float", Types.FLOAT ),
                col( "double", Types.DOUBLE ),
                col( "BigDecimal", Types.NUMERIC ),
                col( "Null", Types.NULL ),
                col( "Array", Types.ARRAY ),
                col( "Map", Types.STRUCT ),
                col( "List", Types.ARRAY ),
                col( "Date", Types.DATE ),
                col( "Time", Types.TIME ),
                col( "Timestamp", Types.TIMESTAMP ),
                col( "NChar", Types.NCHAR )
        );

        return Arrays.<ResultSet[]>asList( new ResultSet[]{new ListResultSet( columns,
                Arrays.<List<Object>>asList( row ), null )},
                new ResultSet[]{new IteratorResultSet( columns, Arrays.<Object[]>asList( row.toArray() ).iterator(),
                        null )}
        );
    }

    private static Date parseDate( String dateString ) throws ParseException
    {
        return new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).parse( dateString );
    }

    public ResultSetTest( ResultSet rs )
    {
        this.rs = rs;
    }

    @Test
    public void testGetByIndex() throws Exception
    {
        assertTrue( rs.next() );
        assertEquals( row.get( 0 ), rs.getString( 1 ) );
        assertEquals( row.get( 0 ), rs.getString( "String" ) );

        assertEquals( row.get( 1 ), rs.getInt( 2 ) );
        assertEquals( row.get( 1 ), rs.getInt( "int" ) );
        assertEquals( row.get( 2 ), rs.getShort( 3 ) );
        assertEquals( row.get( 2 ), rs.getShort( "short" ) );
        assertEquals( row.get( 3 ), rs.getLong( 4 ) );
        assertEquals( row.get( 3 ), rs.getLong( "long" ) );
        assertEquals( row.get( 4 ), rs.getByte( 5 ) );
        assertEquals( row.get( 4 ), rs.getByte( "byte" ) );
        assertEquals( row.get( 5 ), rs.getFloat( 6 ) );
        assertEquals( row.get( 5 ), rs.getFloat( "float" ) );
        assertEquals( row.get( 6 ), rs.getDouble( 7 ) );
        assertEquals( row.get( 6 ), rs.getDouble( "double" ) );
        assertEquals( row.get( 7 ), rs.getBigDecimal( 8 ) );
        assertEquals( row.get( 7 ), rs.getBigDecimal( "BigDecimal" ) );
        assertEquals( false, rs.wasNull() );
        assertEquals( row.get( 8 ), rs.getObject( "Null" ) );
        assertEquals( true, rs.wasNull() );
        assertEquals( row.get( 8 ), rs.getObject( 9 ) );
        assertEquals( true, rs.wasNull() );
        assertEquals( row.get( 9 ), rs.getObject( "Array" ) );
        assertEquals( row.get( 9 ), rs.getObject( 10 ) );
        assertEquals( OBJECT_MAPPER.writeValueAsString( row.get( 9 ) ), rs.getString( "Array" ) );
        assertEquals( OBJECT_MAPPER.writeValueAsString( row.get( 9 ) ), rs.getString( 10 ) );

        assertEquals( row.get( 10 ), rs.getObject( "Map" ) );
        assertEquals( row.get( 10 ), rs.getObject( 11 ) );
        assertEquals( OBJECT_MAPPER.writeValueAsString( row.get( 10 ) ), rs.getString( "Map" ) );
        assertEquals( OBJECT_MAPPER.writeValueAsString( row.get( 10 ) ), rs.getString( 11 ) );

        assertEquals( row.get( 11 ), rs.getObject( "List" ) );
        assertEquals( row.get( 11 ), rs.getObject( 12 ) );
        assertEquals( OBJECT_MAPPER.writeValueAsString( row.get( 11 ) ), rs.getString( "List" ) );
        assertEquals( OBJECT_MAPPER.writeValueAsString( row.get( 11 ) ), rs.getString( 12 ) );

        long testDateTime = parseDate( DATE_STRING ).getTime();
        assertEquals( new java.sql.Date( testDateTime ), rs.getDate( 13 ) );
        assertEquals( new java.sql.Date( testDateTime ), rs.getDate( "Time" ) );

        assertEquals( new Timestamp( testDateTime ), rs.getTimestamp( 14 ) );
        assertEquals( new Timestamp( testDateTime ), rs.getTimestamp( "Timestamp" ) );

        assertEquals( new Time( testDateTime ), rs.getTime( 14 ) );
        assertEquals( new Time( testDateTime ), rs.getTime( "Timestamp" ) );

        assertFalse( rs.next() );
    }

    private static Neo4jColumnMetaData col( String typeName, int type )
    {
        return new Neo4jColumnMetaData( typeName, typeName, type );
    }
}
