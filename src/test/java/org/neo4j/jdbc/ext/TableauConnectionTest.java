package org.neo4j.jdbc.ext;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.jdbc.Connections;
import org.neo4j.jdbc.Neo4jJdbcTest;

import java.sql.SQLException;

@Ignore
public class TableauConnectionTest extends Neo4jJdbcTest
{
    public TableauConnectionTest(Mode mode) throws SQLException
    {
        super( mode );
    }

    @BeforeClass
    public static void setDBVisualizer() throws Exception
    {
        System.setProperty( Connections.TABLEAU, "true" );
    }

    @AfterClass
    public static void removeDBVisualizer()
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
    }
}
