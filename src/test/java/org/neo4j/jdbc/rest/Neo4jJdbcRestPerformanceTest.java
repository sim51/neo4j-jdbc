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
package org.neo4j.jdbc.rest;

import java.sql.SQLException;

import org.junit.Test;

import org.neo4j.jdbc.Neo4jJdbcPerformanceTestRunner;
import org.neo4j.jdbc.Neo4jJdbcTest;

/**
 * @author mh
 * @since 14.06.12
 */
//@Ignore("Perf-Test")
public class Neo4jJdbcRestPerformanceTest extends Neo4jJdbcTest
{

    private Neo4jJdbcPerformanceTestRunner runner;

    public Neo4jJdbcRestPerformanceTest( Neo4jJdbcTest.Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        runner = new Neo4jJdbcPerformanceTestRunner( gdb );
    }

    @Test
    public void testExecuteStatement() throws Exception
    {
        runner.executeMultiple( conn );
    }
}
