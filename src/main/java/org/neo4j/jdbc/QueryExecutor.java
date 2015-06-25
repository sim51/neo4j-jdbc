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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mh
 * @since 15.06.12
 */
public interface QueryExecutor
{
    ExecutionResult executeQuery( String query, Map<String, Object> parameters, boolean autoCommit ) throws Exception;

    void stop() throws Exception;

    Version getVersion();

    void commit() throws Exception;

    void rollback() throws Exception;

    public class Metadata
    {
        String label;
        Map<String, Object> props;
        Map<String, Metadata> rels;  // key == -[:%s {%s}]-> or -[:%s]-> or <-[:%s {%s}]-

        public String toString()
        {
            return String.format( "(:%s {%s})", label, props );
        }

        public Map<String, Object> toMap()
        {
            Map<String, Object> result = new LinkedHashMap<>();
            result.putAll( props );
            for ( Map.Entry<String, Metadata> entry : rels.entrySet() )
            {
                result.put( entry.getKey(), entry.getValue().toString() );
            }
            return result;
        }
    }
}
