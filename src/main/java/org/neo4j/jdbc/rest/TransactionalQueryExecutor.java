package org.neo4j.jdbc.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.neo4j.jdbc.ExecutionResult;
import org.neo4j.jdbc.QueryExecutor;
import org.neo4j.jdbc.Version;
import org.restlet.Client;
import org.restlet.data.CharacterSet;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
* @author mh
* @since 15.06.12
*/
public class TransactionalQueryExecutor implements QueryExecutor {
    protected final static Log log = LogFactory.getLog(TransactionalQueryExecutor.class);
    private final static Client client = new Client("HTTP");
    private final Resources.TransactionClientResource commitResource;

    private String url;
    private Resources.TransactionClientResource txResource;
    private ObjectMapper mapper = new ObjectMapper();
    private Version version;
    private final Resources resources;

    public TransactionalQueryExecutor(String connectionUrl, String user, String password) throws SQLException {
        try
                {
                    url = "http" + connectionUrl;
                    if (log.isInfoEnabled())log.info("Connecting to URL "+url);
                    resources = new Resources(url, client);

                    if (user!=null && password!=null) {
                        resources.setAuth(user, password);
                    }

                    Resources.DiscoveryClientResource discovery = resources.getDiscoveryResource();

                    version = new Version(discovery.getVersion());

                    String transactionPath = discovery.getTransactionPath();

                    txResource = resources.getTransactionResource(transactionPath);
                    commitResource = resources.subResource(txResource, "commit");
                } catch (IOException e)
                {
                    throw new SQLNonTransientConnectionException(e);
                }
    }

    Resources.TransactionClientResource transaction = null;

    static class Statement {
        final String query;
        final Map<String,Object> params;

        Statement(String query, Map<String, Object> params) {
            this.query = query;
            this.params = params;
        }

        public ObjectNode toJson(ObjectMapper mapper) {
            ObjectNode queryNode = mapper.createObjectNode();
            queryNode.put("statement", escapeQuery(query));
            if (params!=null && !params.isEmpty()) queryNode.put("parameters", parametersNode(params,mapper));
            return queryNode;
        }
        private String escapeQuery(String query) {
                query = query.replace('\"', '\'');
                query = query.replace('\n', ' ');
                return query;
            }

            private ObjectNode parametersNode(Map<String, Object> parameters, ObjectMapper mapper) {
              ObjectNode params = mapper.createObjectNode();
                for (Map.Entry<String, Object> entry : parameters.entrySet())
                {
                    final String name = entry.getKey();
                    final Object value = entry.getValue();
                    if (value==null) {
                        params.putNull(name);
                    } else if (value instanceof String)
                        params.put(name, value.toString());
                    else if (value instanceof Integer)
                        params.put(name, (Integer) value);
                    else if (value instanceof Long)
                        params.put(name, (Long) value);
                    else if (value instanceof Boolean)
                        params.put(name, (Boolean) value);
                    else if (value instanceof BigDecimal)
                        params.put(name, (BigDecimal) value);
                    else if (value instanceof Double)
                        params.put(name, (Double) value);
                    else if (value instanceof byte[])
                        params.put(name, (byte[]) value);
                    else if (value instanceof Float)
                        params.put(name, (Float) value);
                    else if (value instanceof Number) {
                        final Number number = (Number) value;
                        if (number.longValue()==number.doubleValue()) {
                            params.put(name, number.longValue());
                        } else {
                            params.put(name, number.doubleValue());
                        }
                    }
                }
                return params;
            }

        public static ArrayNode toJson(ObjectMapper mapper, Statement... statements) {
            if (statements==null || statements.length==0) return null;
            ArrayNode result = mapper.createArrayNode();
            for (Statement statement : statements) {
                result.add(statement.toJson(mapper));
            }
            return result;
        }
    }

    public Iterator<ExecutionResult> begin(Statement...statements) throws SQLException {
        if (transaction!=null) throw new SQLException("Already in transaction "+transaction);
        Representation result = txResource.post(Statement.toJson(mapper,statements));
        if (result.getLocationRef()!=null) {
            this.transaction = resources.getTransactionResource(result.getLocationRef());
        }
        return toResults(statements,txResource.obtainParser());
    }

    private ExecutionResult nextResult(final JsonParser parser) {
        try {
            skip(parser, JsonToken.START_OBJECT, "columns");
            List<String> columns = parser.readValueAs(List.class);
            skip(parser, "data", JsonToken.START_ARRAY);
            return new ExecutionResult(columns, new Iterator<Object[]>() {
                Object[] next = readObjectArray(parser);

                public boolean hasNext() {
                    return next != null;
                }

                public Object[] next() {
                    if (next == null) throw new NoSuchElementException();
                    Object[] result = next;
                    next = readObjectArray(parser);
                    return result;
                }

                public void remove() {
                }
            });
        } catch (IOException ioe) {
            throw new IllegalStateException("Unexpected error", ioe);
        }
    }

    private Object[] readObjectArray(JsonParser parser) {
        try {
            JsonToken token = parser.nextToken();
            if (token == JsonToken.START_ARRAY) return parser.readValueAs(List.class).toArray();
            if (token == null) return null;
            throw new IllegalStateException("Unexpected token "+token);
        } catch(IOException ioe) {
            throw new IllegalStateException("Unexpected error",ioe);
        }
    }

    private Iterator<ExecutionResult> toResults(Statement[] statements, final JsonParser parser) throws SQLException {
        try {
            skip(parser, JsonToken.START_OBJECT,"results"); // { "results"
            return new Iterator<ExecutionResult>() {
                ExecutionResult nextResult=nextResult(parser);
                public boolean hasNext() {
                    return nextResult!=null;
                }

                @Override
                public ExecutionResult next() {
                    if (nextResult==null) throw new NoSuchElementException();
                    ExecutionResult result=nextResult;
                    nextResult = nextResult(parser);
                    return result;
                }

                public void remove() { }
            };
        } catch(Exception ioe) {
            throw new SQLException("Error executing statements "+Statement.toJson(mapper,statements).toString());
        }
    }

    private void skip(JsonParser parser, Object...tokenOrField) throws IOException {
        for (Object expectedToken : tokenOrField) {
            JsonToken token = parser.nextToken();
            if (expectedToken == token || parser.getCurrentName().equals(expectedToken)) continue;
            return;
        }
    }

    public Iterator<ExecutionResult> commit(Statement...statements) throws SQLException {
        if ((statements==null || statements.length==0) && transaction==null) throw new SQLException("Not in transaction");
        Resources.TransactionClientResource resource = transaction == null ? commitResource : resources.subResource(transaction,"commit");
        Representation result = resource.post(Statement.toJson(mapper,statements));
        if (result.isAvailable()) {
            this.transaction = null;
            return toResults(statements,resource.obtainParser());
        }
        throw new IllegalStateException("No results for commit");
    }

    public void rollback() throws SQLException {
        if (transaction==null) throw new SQLException("Not in transaction");
        Representation result = transaction.delete();
        if (result.isAvailable()) {
            this.transaction = null;
        }
    }

    public Iterator<ExecutionResult> executeQueries(Statement...statements) throws Exception {
        if (transaction==null) return commit(statements);
        else {
            transaction.post(Statement.toJson(mapper, statements));
            return toResults(statements,transaction.obtainParser());
        }
    }

    public ExecutionResult executeQuery(String query, Map<String, Object> parameters) throws Exception {
        try {
            final Iterator<ExecutionResult> res = executeQueries(new Statement(query, parameters));
            if (res.hasNext()) return res.next();
            return null; // or throw Exception

        } catch (ResourceException e) {
            throw new SQLException(e.getStatus().getReasonPhrase(), e);
        }
    }

    @Override
    public void stop() throws Exception {
        ((Client) txResource.getNext()).stop();
    }

    @Override
    public Version getVersion() {
        return version;
    }
}
