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

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class Neo4jPreparedStatementCreatorTest {

    public Neo4jPreparedStatementCreatorTest() throws SQLException
    {
        super();
    }

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void testCreateQuery() throws Exception
    {
        final String query = "MATCH (t: test{propName: {pn}}) RETURN t";
        assertEquals( query, Neo4jPreparedStatementCreator.replacePlaceholders( query ) );
    }

    @Test
    public void testCreateQueryWithQuestions() throws Exception
    {
        final String query = "MATCH (t: test{prop: ?, quote: \"? \\\"with ?\\\" to ?\"}) WHERE t.value = ? RETURN t";
        final String exp = "MATCH (t: test{prop: {1}, quote: \"? \\\"with ?\\\" to ?\"}) WHERE t.value = {2} RETURN t";
        assertEquals( exp, Neo4jPreparedStatementCreator.replacePlaceholders( query ) );
    }
}
