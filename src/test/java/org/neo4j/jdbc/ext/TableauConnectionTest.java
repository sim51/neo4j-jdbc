package org.neo4j.jdbc.ext;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.*;
import org.neo4j.jdbc.Connections;
import org.neo4j.jdbc.Neo4jJdbcTest;

import java.sql.ResultSet;
import java.sql.SQLException;

@Ignore
public class TableauConnectionTest extends Neo4jJdbcTest
{
    public TableauConnectionTest(Mode mode) throws SQLException
    {
        super( mode );
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void init() throws Exception
    {
        // Enabled tableau connection
        System.setProperty( Connections.TABLEAU, "true" );
    }

    @AfterClass
    public static void finish()
    {
        System.clearProperty( Connections.TABLEAU );
    }

    @Test
    public void testExecuteQueries() throws Exception
    {
        conn.createStatement().executeQuery("SELECT * FROM (   MATCH (p:Product) RETURN p.name ) 'TableauSQL'");
        conn.createStatement().executeQuery("SELECT * FROM (   MATCH (p:Product) RETURN p.name ) \"TableauSQL\"");
        conn.createStatement().executeQuery("SELECT * FROM (   MATCH (p:Product) RETURN p.name ) TableauSQL");
        conn.createStatement().executeQuery("SELECT * FROM (   MATCH (p:Product) RETURN p.name ) Custom_SQL_Query");
        conn.createStatement().executeQuery("SELECT TableauSQL.name AS name FROM ( MATCH (t:Product) RETURN t.name as name ) TableauSQL LIMIT 10000");
        conn.createStatement().executeQuery("SELECT 1 AS COL");
        conn.createStatement().executeQuery("SELECT 1");
    }

    @Test
    public void testOrderReturn() throws Exception
    {
        createData(gdb);

        ResultSet rs = conn.createStatement().executeQuery("SELECT TableauSQL.id, TableauSQL.name FROM (   MATCH (n) RETURN n.name  AS name, n.id AS id) 'TableauSQL'");
        rs.next();

        Assert.assertEquals("1", rs.getObject(1).toString());
        Assert.assertEquals("n1", rs.getObject(2).toString());
    }

    @Test
    public void should_fail_without_alias_return() throws Exception
    {
        createData(gdb);
        thrown.expect(SQLException.class);
        thrown.expectMessage("All return value must have an alias");

        conn.createStatement().executeQuery("SELECT TableauSQL.id, TableauSQL.name FROM (   MATCH (n) RETURN n.name  AS name, n.id) 'TableauSQL'");

    }

    private void createData( GraphDatabaseService gdb )
    {
        // Loading some data
        try ( Transaction tx = gdb.beginTx() )
        {
            Node n1 = gdb.createNode();
            n1.setProperty( "id", "1" );
            n1.setProperty( "name", "n1" );
            Node n2 = gdb.createNode();
            n2.setProperty( "id", "2" );
            n2.setProperty( "name", "n2" );
            tx.success();
        }
    }
}
