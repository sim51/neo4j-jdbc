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
package org.neo4j.cypherdsl.grammar;

import java.util.LinkedHashMap;
import java.util.Map;

import org.neo4j.cypherdsl.query.Query;

/**
 * @author mh
 * @since 20.12.13
 */
public class QueryStringBasedExecute implements Execute, ExecuteWithParameters
{

    private final String query;
    private final Map<String, Object> params = new LinkedHashMap<>();

    @Override
    public Query toQuery()
    {
        throw new UnsupportedOperationException();
    }

    public QueryStringBasedExecute( String query )
    {
        this.query = query;
    }

    @Override
    public ExecuteWithParameters parameter( String name, Object value )
    {
        params.put( name, value );
        return this;
    }

    @Override
    public ExecuteWithParameters parameters( Map<String, Object> parameters )
    {
        params.putAll( parameters );
        return this;
    }

    @Override
    public void asString( StringBuilder builder )
    {
        builder.append( query );
    }

    @Override
    public Map<String, Object> getParameters()
    {
        return params;
    }

    @Override
    public String toString()
    {
        return query;
    }
}
