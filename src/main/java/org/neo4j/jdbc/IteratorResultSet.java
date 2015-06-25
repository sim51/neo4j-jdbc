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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.neo4j.jdbc.util.ClosableIterator;

/**
 * ResultSet implementation that is backed by an Iterator.
 */
public class IteratorResultSet extends AbstractResultSet
{
    private Iterator<Object[]> data;
    private Object[] currentRow;
    private int row = -1;

    public IteratorResultSet( List<Neo4jColumnMetaData> columns, Iterator<Object[]> data, Neo4jConnection conn )
    {
        super( columns, conn );
        this.data = data;
        data.hasNext();
    }

    public IteratorResultSet( Neo4jConnection conn, List<String> columns, Iterator<Object[]> data )
    {
        super( conn, columns );
        this.data = data;
        data.hasNext();
    }

    @Override
    protected Object[] currentRow()
    {
        return currentRow;
    }

    @Override
    public boolean next() throws SQLException
    {
        if ( hasNext() )
        {
            currentRow = data.next();
            row++;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void close() throws SQLException
    {
        if ( data instanceof AutoCloseable )
        {
            try
            {
                ((AutoCloseable) data).close();
            }
            catch ( Exception e )
            {
                log.warn( "Couldn't close resultset", e );
            }
        }
        else if ( data instanceof ClosableIterator )
        {
            ((ClosableIterator) data).close();
        }
        super.close();
    }

    private boolean hasNext()
    {
        return data.hasNext();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException
    {
        return row == -1;
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        return !hasNext();
    }

    @Override
    public boolean isFirst() throws SQLException
    {
        return row == 0;
    }

    @Override
    public boolean isLast() throws SQLException
    {
        return !hasNext();
    }

    @Override
    public void beforeFirst() throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public void afterLast() throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public boolean first() throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public boolean last() throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public int getRow() throws SQLException
    {
        return row;
    }

    @Override
    public boolean absolute( int i ) throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public boolean relative( int i ) throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public boolean previous() throws SQLException
    {
        throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
    }

    @Override
    public void setFetchDirection( int i ) throws SQLException
    {
        if ( i != ResultSet.FETCH_FORWARD )
        {
            throw new SQLException( "Result set type is TYPE_FORWARD_ONLY" );
        }
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize( int i ) throws SQLException
    {
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        return 0;
    }

    @Override
    public int getType() throws SQLException
    {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return super.isClosed();
    }

    @Override
    public String toString()
    {
        return super.toString() + " current row " + row + ": " + Arrays.toString( currentRow );
    }

}
