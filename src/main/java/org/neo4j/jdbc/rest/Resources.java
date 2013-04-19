package org.neo4j.jdbc.rest;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.util.Series;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author mh
 * @since 12.06.12
 */
public class Resources {
    private ObjectMapper mapper = new ObjectMapper();
    private final Reference ref;
    private String user;
    private String password;
    private final Client client;

    Resources(String url, Client client) {
        this.client = client;
        ref = new Reference(new Reference(url), "/");
    }

    private Context createContext() {
        Context context = new Context();
        context.setClientDispatcher(client);
        return context;
    }


    public void setAuth(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public DiscoveryClientResource getDiscoveryResource() throws IOException {
        DiscoveryClientResource discovery = withAuth(new DiscoveryClientResource(createContext(), ref, mapper));
        discovery.readInformation();
        return discovery;

    }

    <T extends ClientResource> T withAuth(T resource) {
        if (hasAuth()) {
            resource.setChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
        }
        return resource;
    }

    private boolean hasAuth() {
        return user != null && password != null;
    }

    public ClientResource getCypherResource(String cypherPath) {
        return withAuth(new CypherClientResource(new Context(), cypherPath, mapper));
    }

    public TransactionClientResource getTransactionResource(String transactionPath) {
        return withAuth(new TransactionClientResource(new Context(), transactionPath));
    }

    public TransactionClientResource getTransactionResource(Reference transactionPath) {
        return withAuth(new TransactionClientResource(new Context(), transactionPath));
    }

    public JsonNode readJsonFrom(String uri) throws IOException {
        ClientResource resource = withAuth(new ClientResource(createContext(), uri));
        resource.getClientInfo().setAcceptedMediaTypes(streamingJson());
        return mapper.readTree(resource.get().getReader());
    }

    private String textField(JsonNode node, String field) {
        final JsonNode fieldNode = node.get(field);
        if (fieldNode == null) return null;
        return fieldNode.getTextValue();
    }

    public class DiscoveryClientResource extends ClientResource {
        private String version;
        private final ObjectMapper mapper;
        private String cypherPath;
        private String transactionPath;

        public DiscoveryClientResource(Context context, Reference ref, ObjectMapper mapper) {
            super(context, ref);
            this.mapper = mapper;
            getClientInfo().setAcceptedMediaTypes(streamingJson());
        }

        public String getVersion() {
            return version;
        }

        public void readInformation() throws IOException {
            // Get service root
            JsonNode discoveryInfo = mapper.readTree(get().getReader());

            final String dataUri = textField(discoveryInfo, "data");

            JsonNode serverData = readJsonFrom(dataUri);

            version = textField(serverData, "neo4j_version");
            transactionPath = textField(serverData, "transaction");

            cypherPath = obtainCypherPath(serverData);
        }

        private String obtainCypherPath(JsonNode serverData) {
            String cypherPath = textField(serverData, "cypher");
            if (cypherPath == null) {
                final JsonNode extensions = serverData.get("extensions");
                if (extensions != null) {
                    final JsonNode plugin = extensions.get("CypherPlugin");
                    if (plugin != null) cypherPath = textField(plugin, "execute_query");
                }
            }
            return cypherPath;
        }

        public String getCypherPath() {
            return cypherPath;
        }

        public String getTransactionPath() {
            return transactionPath;
        }
    }


    private static class CypherClientResource extends ClientResource {
        private final ObjectMapper mapper;

        public CypherClientResource(final Context context, String cypherPath, ObjectMapper mapper) {
            super(context, cypherPath);
            this.mapper = mapper;
            getClientInfo().setAcceptedMediaTypes(streamingJson());
        }

        @Override
        public void doError(Status errorStatus) {
            try {
                JsonNode node = mapper.readTree(getResponse().getEntity().getReader());
                JsonNode message = node.get("message");
                if (message != null)
                    super.doError(new Status(errorStatus.getCode(), message.toString(), message.toString(), errorStatus.getUri()));
            } catch (IOException e) {
                // Ignore
            }

            super.doError(errorStatus);
        }
    }

    public TransactionClientResource subResource(TransactionClientResource res,String segment) {
        return withAuth(res.subResource(segment));
    }

    public static class TransactionClientResource extends ClientResource {
        private final static JsonFactory JSON_FACTORY = new JsonFactory();

        public TransactionClientResource(final Context context, String path) {
            super(context, path);
            getClientInfo().setAcceptedMediaTypes(streamingJson());
        }
        public TransactionClientResource(final Context context, Reference path) {
            super(context, path);
            getClientInfo().setAcceptedMediaTypes(streamingJson());
        }

        public TransactionClientResource subResource(String segment) {
            return new TransactionClientResource(getContext(),getReference().addSegment(segment));
        }

        // todo multithreaded use
        public JsonParser obtainParser() throws SQLException {
            try {
            Reader reader = getResponse().getEntity().getReader();
            return JSON_FACTORY.createJsonParser(reader);
            } catch (IOException ioe) {
                throw new SQLTransientConnectionException("Error creating result parser",ioe);
            }
        }

        @Override
        public void doError(Status errorStatus) {
            try {
                Collection<Object> errors=findErrors(obtainParser());
                if (!errors.isEmpty())
                    super.doError(new Status(errorStatus.getCode(), "Error executing statement", errors.toString(), errorStatus.getUri()));
            } catch (SQLException e) {
                // Ignore
            } catch (IOException e) {
                // Ignore
            }

            super.doError(errorStatus);
        }

        private Collection<Object> findErrors(JsonParser parser) throws IOException {
            parser.nextToken(); // todo, parser can be anywhere should return to top-level first?
            if ("results".equals(parser.getCurrentName())) {
                parser.skipChildren();
                parser.nextToken();
            }
            List<Object> errors = Collections.emptyList();
            if ("errors".equals(parser.getCurrentName())) {
                if (JsonToken.START_ARRAY == parser.nextToken()) {
                    errors= parser.readValueAs(new TypeReference<Object>() {
                    });
                }
            }
            return errors;
        }
    }

    private static List<Preference<MediaType>> streamingJson() {
        final MediaType mediaType = streamingJsonType();
        return Collections.singletonList(new Preference<MediaType>(mediaType));
    }

    private static MediaType streamingJsonType() {
        final Series<Parameter> parameters = new Series<Parameter>(Parameter.class);
        parameters.add("stream", "true");
        return new MediaType(MediaType.APPLICATION_JSON.getName(), parameters);
    }
}
