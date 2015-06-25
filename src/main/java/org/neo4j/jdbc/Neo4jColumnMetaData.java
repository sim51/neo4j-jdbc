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
 * TODO
 */
public class Neo4jColumnMetaData
{
    private String name;
    private String typeName;
    private int dataType;

    public Neo4jColumnMetaData( String name, String typeName, int dataType )
    {
        this.name = name;
        this.typeName = typeName;
        this.dataType = dataType;
    }

    public String getName()
    {
        return name;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public int getDataType()
    {
        return dataType;
    }

    public String toString()
    {
        return name + ":" + typeName;
    }
}
