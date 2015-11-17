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
package org.neo4j.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class Neo4jPreparedStatementTest extends Neo4jJdbcTest {

    private String columnName = "propName";
    private String tableName = "test";
    private final String columnType = "String";

    public Neo4jPreparedStatementTest(Mode mode) throws SQLException
    {
        super(mode);
    }

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        createTableMetaData( gdb, tableName, columnName, columnType );
    }

    private Field getQueryField() throws Exception
    {
        final Class<Neo4jPreparedStatement> cls = Neo4jPreparedStatement.class;
        Field query = cls.getDeclaredField( "query" );
        query.setAccessible( true );
        return query;
    }

    @Test
    public void testCreateQuery() throws Exception
    {
        final String format = "MATCH (t: %s{%s: {pn}}) RETURN t";
        final String query = String.format( format, tableName, columnName );
        final PreparedStatement stmt = conn.prepareStatement( query );
        final Field field = getQueryField();
        assertEquals( query, field.get( stmt ) );
    }

    @Test
    public void testCreateQueryWithQuestions() throws Exception
    {
        final String query = "MATCH (t: test{prop: ?, quote: \"? \\\"with ?\\\" to ?\"}) WHERE t.value = ? RETURN t";
        final String exp = "MATCH (t: test{prop: {1}, quote: \"? \\\"with ?\\\" to ?\"}) WHERE t.value = {2} RETURN t";
        final PreparedStatement stmt = conn.prepareStatement( query );
        final Field field = getQueryField();
        assertEquals( exp, field.get( stmt ) );
    }
}
