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
 * Create PreparedStatement with JDBC requirements.
 */
class Neo4jPreparedStatementCreator
{
    public Neo4jPreparedStatementCreator()
    {
        super();
    }

    /**
     * Replace place holders to `?` marks.
     *
     * JDBC doesn't support named placeholders.
     * It replaces all named placeholders to `?` marks.
     * @param statement cypher statement which may include named placeholders.
     * @return statement without named placeholders.
     */
    public static final String replacePlaceholders( final String statement )
    {
        final char placeholder = '?';
        final char quote = '"';
        final char escape = '\\';
        final String format = "{%d}";
        int i = 1;
        boolean inQuote = false;
        boolean escaped = false;
        StringBuffer sb = new StringBuffer( statement.length() );
        for ( char c : statement.toCharArray() )
        {
            if ( inQuote )
            {
                if (escaped)
                {
                    escaped = false;
                    sb.append( escape );
                    sb.append( c );
                }
                else
                {
                    switch ( c )
                    {
                        case escape:
                            escaped = true;
                            break;
                        case quote:
                            inQuote = false;
                            // fall through
                        default:
                            sb.append( c );
                            break;
                    }
                }
            }
            else
            {
                switch ( c )
                {
                    case placeholder:
                        sb.append( String.format( format, i++ ) );
                        break;
                    case quote:
                        inQuote = true;
                        // fall through
                    default:
                        sb.append( c );
                        break;
                }
            }
        }
        return sb.toString();
    }

}
