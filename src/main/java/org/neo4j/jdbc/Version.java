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

/**
 * @author mh
 * @since 15.06.12
 */
public class Version
{
    private final String version;

    public Version( String version )
    {
        this.version = version;
    }

    public int getMajorVersion()
    {
        String[] versionParts = version.split( "\\." );
        return Integer.parseInt( versionParts[0] );
    }

    public int getMinorVersion()
    {
        String[] versionParts = version.split( "\\." );
        String minorVersion = versionParts[1].split( "-" )[0];
        return Integer.parseInt( minorVersion );
    }

    public String getVersion()
    {
        return version;
    }
}
