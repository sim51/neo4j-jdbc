/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServer;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.server.modules.*;
import org.neo4j.server.web.WebServer;
import org.neo4j.test.ImpermanentGraphDatabase;
import org.neo4j.test.TestGraphDatabaseBuilder;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.neo4j.helpers.collection.MapUtil.stringMap;

/**
 * @author mh
 * @since 12.06.12
 */
@RunWith(Parameterized.class)
@Ignore
public abstract class Neo4jJdbcTest {
    public static final int PORT = 7475;
    protected static final String REFERENCE_NODE_ID_QUERY = "start n=node(0) return ID(n) as id";
    protected Neo4jConnection conn;
    protected static ImpermanentGraphDatabase gdb;
    private static WebServer webServer;
    protected final Mode mode;
    private Transaction tx;

    public enum Mode { embedded, server, server_tx, server_auth }
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.<Object[]>asList(new Object[]{Mode.embedded},new Object[]{Mode.server},new Object[]{Mode.server_auth},new Object[]{Mode.server_tx});
//        return Arrays.<Object[]>asList(new Object[]{Mode.server_tx});
//        return Arrays.<Object[]>asList(new Object[]{Mode.embedded});
    }

    @BeforeClass
    public static void before() {
        gdb = (ImpermanentGraphDatabase) new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    public Neo4jJdbcTest(Mode mode) throws SQLException {
        this.mode = mode;
        System.out.println("Mode "+mode);
        final Driver driver = new Driver();
        final Properties props = new Properties();
        gdb.cleanContent(true);
        switch (mode) {
            case embedded:
                props.put("db",gdb);
                conn = driver.connect("jdbc:neo4j:instance:db", props);
                break;
            case server:
                if (webServer==null) {
                    webServer = startWebServer(gdb,PORT,false);
                }
                props.setProperty(Driver.LEGACY,"true");
                conn = driver.connect("jdbc:neo4j://localhost:"+PORT, props);
                break;
            case server_tx:
                if (webServer==null) {
                    webServer = startWebServer(gdb,PORT,false);
                }
                conn = driver.connect("jdbc:neo4j://localhost:"+PORT, props);
                break;
            case server_auth:
                if (webServer==null) {
                    webServer = startWebServer(gdb, PORT,true);
                }
                props.put(Driver.USER,TestAuthenticationFilter.USER);
                props.put(Driver.PASSWORD,TestAuthenticationFilter.PASSWORD);
                props.setProperty(Driver.LEGACY, "true");
                conn = driver.connect("jdbc:neo4j://localhost:"+PORT, props);
                break;
        }
    }

    private WebServer startWebServer(GraphDatabaseAPI gdb, int port, boolean auth) {
        final ServerConfigurator config = new ServerConfigurator(gdb);
        config.configuration().setProperty(Configurator.WEBSERVER_PORT_PROPERTY_KEY,port);
        final WrappingNeoServer wrappingNeoServer = new WrappingNeoServer(gdb, config) {
            @Override
            protected Iterable<ServerModule> createServerModules()
            {
                return Arrays.asList(
                        new DiscoveryModule(webServer),
                        new RESTApiModule(webServer, database, configurator.configuration()),
                        new ThirdPartyJAXRSModule(webServer, configurator, this));
            }
        };
        final WebServer webServer = wrappingNeoServer.getWebServer();
        if (auth) webServer.addFilter(new TestAuthenticationFilter(), "/*");
        wrappingNeoServer.start();
        return webServer;
    }


    @AfterClass
    public static void after() {
        try {
            if (webServer!=null) {
                webServer.stop();
                webServer = null;
            }
            gdb.shutdown();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws SQLException, Exception {
        gdb.cleanContent(true);
    }

    protected String jdbcUrl() {
        return "jdbc:neo4j://localhost:" + PORT + "/";
    }

    @After
    public void tearDown() {
        try {
            if (conn != null) conn.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void createTableMetaData(GraphDatabaseService gdb, String typeName, String propName, String propType) {
        // CYPHER 1.7 START n=node(0)
        // MATCH (n)-[:TYPE]->(type)-[:HAS_PROPERTY]->(property)
        // WHERE type.type={typename}
        // RETURN type.type,property.name,property.type
        final Transaction tx = gdb.beginTx();
        final Node root = gdb.getReferenceNode();
        final Node type = gdb.createNode();
        type.setProperty("type",typeName);
        root.createRelationshipTo(type, DynamicRelationshipType.withName("TYPE"));
        final Node property = gdb.createNode();
        property.setProperty("name",propName);
        property.setProperty("type",propType);
        type.createRelationshipTo(property,DynamicRelationshipType.withName("HAS_PROPERTY"));
        tx.success();
        tx.finish();
    }

    protected void dumpColumns(ResultSet rs) throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        final int cols = meta.getColumnCount();
        for (int col=1;col<cols;col++) {
            System.out.println(meta.getColumnName(col));
        }
    }

    protected Version getVersion() {
        final String releaseVersion = gdb.getKernelData().version().getRevision();
        return new Version(releaseVersion);
    }

    protected Transaction begin() {
        return tx = gdb.beginTx();
    }
    protected void done() {
        if (tx!=null) {
            tx.success();
            tx.close();
        }
    }

}
