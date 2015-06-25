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

public class UserAgentBuilder
{
    public static final String USER_AGENT = "userAgent";
    private final Properties jdbcDriverProperties;

    public UserAgentBuilder( Properties jdbcDriverProperties )
    {
        this.jdbcDriverProperties = jdbcDriverProperties;
    }

    public String getAgent()
    {
        StringBuilder sb = new StringBuilder();
        if ( jdbcDriverProperties.containsKey( USER_AGENT ) )
        {
            sb.append( jdbcDriverProperties.getProperty( USER_AGENT ) ).append( " via " );
        }
        sb.append( getImplementationTitle() );
        sb.append( "/" );
        sb.append( getImplementationVersion() );
        return sb.toString();
    }

    private String getImplementationVersion()
    {
        String implementationVersion = getClass().getPackage().getImplementationVersion();
        if ( implementationVersion == null )
        {
            implementationVersion = "<unversioned>";
        }
        return implementationVersion;
    }

    private String getImplementationTitle()
    {
        String implementationTitle = getClass().getPackage().getImplementationTitle();
        if ( implementationTitle == null )
        {
            implementationTitle = "Neo4j JDBC Driver";
        }
        return implementationTitle;
    }
}
