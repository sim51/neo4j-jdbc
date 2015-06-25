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

import org.neo4j.jdbc.util.Closer;

import java.io.Closeable;
import java.util.*;

/**
 * Cypher execution result.
 */
public class ExecutionResult implements Iterable<Object[]>, Closeable
{
    public static final ExecutionResult EMPTY_RESULT = new ExecutionResult( Collections.<String>emptyList(),
            Collections.<Object[]>emptyList().iterator() );
    private List<String> columns;
    private Iterator<Object[]> result;
    private final boolean isLazy;

    public ExecutionResult( List<String> columns, Iterator<Object[]> result )
    {
        this.columns = columns;
        this.result = result;
        isLazy = !(result instanceof Collection);
    }

    public List<String> columns()
    {
        return columns;
    }

    public boolean isLazy()
    {
        return isLazy;
    }

    @Override
    public Iterator<Object[]> iterator()
    {
        return result;
    }

    @Override
    public String toString()
    {
        String result = "Columns:" + columns;
        result += "\n" + this.result;
        return result;
    }

    public Iterator<Object[]> getResult()
    {
        return result;
    }

    @Override
    public void close() {
        Closer.close(result);
    }
}
