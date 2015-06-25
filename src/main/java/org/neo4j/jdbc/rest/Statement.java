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

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * @author mh
 * @since 20.04.13
 */
public class Statement
{
    final String query;
    final Map<String, Object> params;

    Statement( String query, Map<String, Object> params )
    {
        this.query = query;
        this.params = params;
    }

    public ObjectNode toJson( ObjectMapper mapper )
    {
        ObjectNode queryNode = mapper.createObjectNode();
        queryNode.put( "statement", escapeQuery( query ) );
        if ( params != null && !params.isEmpty() )
        {
            queryNode.put( "parameters", JsonUtils.serialize( params, mapper ) );
        }
        return queryNode;
    }

    @Override
    public String toString()
    {
        return "query: " + query + "\nparams:" + params;
    }

    private String escapeQuery( String query )
    {
        return query.replace( '\"', '\'' ).replace( '\n', ' ' );
    }

    public static ArrayNode toJson( ObjectMapper mapper, Statement... statements )
    {
        ArrayNode result = mapper.createArrayNode();
        if ( statements == null || statements.length == 0 )
        {
            return result;
        }
        for ( Statement statement : statements )
        {
            result.add( statement.toJson( mapper ) );
        }
        return result;
    }
}
