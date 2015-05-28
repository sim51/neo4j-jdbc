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

package org.neo4j.jdbc.ext;

import org.neo4j.cypherdsl.grammar.ExecuteWithParameters;
import org.neo4j.jdbc.Driver;
import org.neo4j.jdbc.Neo4jConnection;
import org.neo4j.jdbc.ResultSetBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tableau specific Neo4j connection. Contains workarounds to get it to work with Tableau.
 */
public class TableauConnection extends Neo4jConnection implements Connection {

    public TableauConnection(Driver driver, String url, Properties properties) throws SQLException {
        super(driver, url, properties);
    }

    @Override
    public ResultSet executeQuery(String query, Map<String, Object> parameters) throws SQLException {

        {
            Pattern pattern = Pattern.compile("\\s*SELECT 1.*");
            Matcher matcher = pattern.matcher(query);
            if (matcher.matches()) {
                return new ResultSetBuilder().newResultSet(debug(this));
            }
        }

        {
            Pattern pattern = Pattern.compile("\\s*SELECT .* FROM \\((.*)\\) .* WHERE 1=0\\s*");
            Matcher matcher = pattern.matcher(query);
            if (matcher.matches()) {
                return new ResultSetBuilder().newResultSet(debug(this));
            }
        }

        //TODO: manage LIMIT on queries
        Pattern pattern = Pattern.compile("\\s*SELECT .* FROM \\((.*)\\) .*");
        Matcher matcher = pattern.matcher(query);
        if (matcher.matches()) {
            query = matcher.group(1);
        }

        return super.executeQuery(query, parameters);
    }

}
