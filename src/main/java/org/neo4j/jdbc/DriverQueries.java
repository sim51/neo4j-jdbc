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

import org.neo4j.cypherdsl.expression.Expression;
import org.neo4j.cypherdsl.grammar.Execute;
import org.neo4j.cypherdsl.grammar.ExecuteWithParameters;
import org.neo4j.cypherdsl.grammar.QueryStringBasedExecute;
import org.neo4j.cypherdsl.query.clause.ReturnClause;

/**
 * This class contains all the Cypher queries that the driver needs to issue.
 */
public class DriverQueries
{
    public Execute getTables()
    {
        return new QueryStringBasedExecute( "MATCH (r:MetaDataRoot)-[:TYPE]->(type) RETURN type.type" );
//        return start(nodesById("n", 0)).
//                match(node("n").out("TYPE").node("type")).
//                returns(identifier("type").property("type"));
    }

    public Execute getColumns()
    {
        return new QueryStringBasedExecute( "MATCH (r:MetaDataRoot)-[:TYPE]->(type)-[:HAS_PROPERTY]->(property) " +
                "RETURN type.type, property.name, property.type" );
//        return start(nodesById("n", 0)).
//                match(node("n").out("TYPE").node("type").out("HAS_PROPERTY").node("property")).
//                returns(identifier("type").property("type"), identifier("property").property("name"),
// identifier("property").property("type"));
    }

    public ExecuteWithParameters getColumns( String typeName )
    {
        return new QueryStringBasedExecute( "MATCH (r:MetaDataRoot)-[:TYPE]->(type {type:{typeName}})" +
                "-[:HAS_PROPERTY]->(property) RETURN type.type, property.name, " +
                "property.type" ).parameter( "typeName", typeName );
//        return start(nodesById("n", 0)).
//                match(node("n").out("TYPE").node("type").out("HAS_PROPERTY").node("property")).
//                where(identifier("type").string("type").eq(param("typename"))).
//                returns(identifier("type").string("type"), identifier("property").string("name"),
// identifier("property").string("type")).parameter("typename", typeName);
    }

    public ExecuteWithParameters getData( String typeName, Iterable<Expression> returnProperties )
    {

        StringBuilder builder = new StringBuilder();
        new ReturnClause( returnProperties ).asString( builder );
        // builder.toString()
        return new QueryStringBasedExecute( "MATCH (r:MetaDataRoot)-[:TYPE]->(type {type:{typeName}})<-[:IS_A]->" +
                "(instance) " + builder.toString() ).parameter( "typeName", typeName );
//        return start(nodesById("n",0)).
//                match(node("n").out("TYPE").node("type").in("IS_A").node("instance")).
//                where(identifier("type").string("type").eq(param("typename"))).
//                returns(returnProperties).
//                parameter("typename", typeName);
    }
}
