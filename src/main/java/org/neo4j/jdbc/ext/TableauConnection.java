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

import org.neo4j.jdbc.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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

        String cql = query;

        Pattern pattern = Pattern.compile("\\s*DROP TABLE.*");
        Matcher matcher = pattern.matcher(query);
        if (matcher.matches()) {
            return new ResultSetBuilder().newResultSet(debug(this));
        }

        pattern = Pattern.compile("\\s*CREATE LOCAL TEMPORARY TABLE.*");
        matcher = pattern.matcher(query);
        if (matcher.matches()) {
            return new ResultSetBuilder().newResultSet(debug(this));
        }

        pattern = Pattern.compile("\\s*SELECT 1.*");
        matcher = pattern.matcher(query);
        if (matcher.matches()) {
            return new ResultSetBuilder().newResultSet(debug(this));
        }

        pattern = Pattern.compile("\\s*SELECT .* FROM \\((.*)\\) .* WHERE 1=0.*");
        matcher = pattern.matcher(query);
        if (matcher.matches()) {
            return new ResultSetBuilder().newResultSet(debug(this));
        }

        //TODO: manage tableau LIMIT on queries
        pattern = Pattern.compile("\\s*SELECT .* FROM \\((.*)\\).*");
        matcher = pattern.matcher(query);
        if (matcher.matches()) {
            cql = matcher.group(1);
        }

        cql = makeReturnParamOrder(cql);

        // Adding original query as a comment (for debug)
        cql += "//" + query.replaceAll("\\n", " ");

        return super.executeQuery(cql, parameters);
    }

    private String makeReturnParamOrder(String query) throws SQLException {
        String cql = query;

        // Getting the RETURN part
        Pattern pattern = Pattern.compile("(.*)RETURN(.*)(ORDER BY|SKIP|LIMIT)?(.*)");
        Matcher matcher = pattern.matcher(query);
        if (matcher.matches()) {

            // Construct the columns list
            List<String> columns = new ArrayList<String>();
            String[] returnSeq = matcher.group(2).split(",");
            for (String column : returnSeq) {
                String[] varSeq = column.split("AS");
                if (varSeq.length > 1) {
                    columns.add(varSeq[1].trim());
                } else {
                    throw new SQLException("All return value must have an alias");
                }
            }

            // Order the list
            Collections.sort(columns);
            String returnOrder = " RETURN ";
            for (int i = 0; i < columns.size(); i++) {
                returnOrder += columns.get(i);
                if (i < (columns.size() - 1)) {
                    returnOrder += ", ";
                }
            }

            // Make a "with return" query
            cql = matcher.group(1) + "WITH" + matcher.group(2) + returnOrder;
            if(matcher.group(3) != null) {
                cql += matcher.group(3);
            }
            if(matcher.group(4) != null) {
                cql += matcher.group(4);
            }
        } else {
            throw new SQLException("There is no RETURN in the query");
        }

        return cql;
    }

}
