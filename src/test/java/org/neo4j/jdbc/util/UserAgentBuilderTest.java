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
package org.neo4j.jdbc.util;

import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserAgentBuilderTest
{

    @Test
    public void testGetAgentEmptyProperties() throws Exception
    {
        Properties props = new Properties(  );
        assertEquals( "Neo4j JDBC Driver/<unversioned>", new UserAgentBuilder( props ).getAgent());
    }

    @Test
    public void testGetAgentWithProperties() throws Exception
    {
        Properties props = new Properties(  );
        props.put("userAgent", "mytool");
        assertEquals( "mytool via Neo4j JDBC Driver/<unversioned>", new UserAgentBuilder( props ).getAgent());
    }

}