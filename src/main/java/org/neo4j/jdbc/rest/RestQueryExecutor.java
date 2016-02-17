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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.neo4j.jdbc.ExecutionResult;
import org.neo4j.jdbc.QueryExecutor;
import org.neo4j.jdbc.Version;
import org.restlet.Response;
import org.restlet.data.CharacterSet;
import org.restlet.data.Header;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;
import org.restlet.util.Series;

/**
 * @author mh
 * @since 15.06.12
 */
public class RestQueryExecutor implements QueryExecutor
{
    protected final static Log log = LogFactory.getLog( RestQueryExecutor.class );
    public static final String HEADERS = "org.restlet.http.headers";

    private ClientResource cypherResource;
    private ObjectMapper mapper = new ObjectMapper();
    private Version version;
    private final Resources.DiscoveryClientResource discovery;

    public RestQueryExecutor( Resources resources ) throws SQLException
    {
        try
        {
            discovery = resources.getDiscoveryResource();

            version = new Version( discovery.getVersion() );

            String cypherPath = discovery.getCypherPath();

            cypherResource = resources.getCypherResource( cypherPath );
        }
        catch ( IOException e )
        {
            throw new SQLNonTransientConnectionException( e );
        }
    }

    public ExecutionResult executeQuery( String query, Map<String, Object> parameters,
                                         boolean autoCommit ) throws Exception
    {
        if ( !autoCommit )
        {
            throw new SQLException( "Manual commit mode not supported over REST" );
        }
        ClientResource resource = null;
        try
        {
            ObjectNode queryNode = queryParameter( query, parameters );

            resource = new ClientResource( cypherResource );
            Series<Header> headers = getHeaders( resource );
            headers.add( "X-Stream", "true" );
            Representation rep = resource.post( queryNode.toString() );
            rep.setCharacterSet( new CharacterSet( "UTF-8" ) );
            JsonNode node = mapper.readTree( rep.getReader() );
            final ResultParser parser = new ResultParser( node );
            return new ExecutionResult( parser.getColumns(), parser.streamData() );
        }
        catch ( ResourceException e )
        {
            String msg = extractErrorMessage( resource );
            if ( msg != null )
            {
                throw new SQLException( msg, e );
            }
            throw new SQLException( e.getStatus().getReasonPhrase(), e );
        }
        catch ( JsonProcessingException e )
        {
            throw new SQLException( e );
        }
        catch ( IOException e )
        {
            throw new SQLException( e );
        }
    }

    private Series<Header> getHeaders( ClientResource resource )
    {
        Series<Header> headers = (Series<Header>) resource.getRequestAttributes().get( HEADERS );
        if ( headers == null )
        {
            headers = new Series<>( Header.class );
            resource.getRequestAttributes().put( HEADERS, headers );
        }
        return headers;
    }

    /**
     * When a REST error occurs, the JSON can contain an error message
     */
    private String extractErrorMessage( ClientResource resource )
    {
        try
        {
            if ( resource == null )
            {
                return null;
            }
            Response resp = resource.getResponse();
            if ( resp == null )
            {
                return null;
            }
            Representation rep = resp.getEntity();
            rep.setCharacterSet( new CharacterSet( "UTF-8" ) );

            JsonNode node = mapper.readTree( rep.getReader() );
            if ( node == null )
            {
                return null;
            }
            JsonNode msg = node.findValue( "message" );
            if ( msg == null )
            {
                return null;
            }
            return msg.getTextValue();
        }
        catch ( Exception ex )
        {
            return null;
        }
    }

    @Override
    public void stop() throws Exception
    {
        ((Filter) cypherResource.getNext()).stop();
    }

    @Override
    public Version getVersion()
    {
        return version;
    }

    private ObjectNode queryParameter( String query, Map<String, Object> parameters )
    {
        ObjectNode queryNode = mapper.createObjectNode();
        queryNode.put( "query", JsonUtils.escapeQuery( query ) );
        if ( parameters != null )
        {
            queryNode.put( "params", JsonUtils.serialize( parameters, mapper ) );
        }
        return queryNode;
    }

    @Override
    public void commit() throws Exception
    {
        // no op
    }

    @Override
    public void rollback() throws Exception
    {
        // no op
    }
}
