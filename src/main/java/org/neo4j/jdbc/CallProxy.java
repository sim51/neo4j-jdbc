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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/**
 * This implements driver debugging functionality. Method calls and results are logged to JUL.
 */
public class CallProxy
        implements InvocationHandler
{
    public static <T> T proxy( Class<T> clazz, T next )
    {
        return clazz.cast( Proxy.newProxyInstance( clazz.getClassLoader(), new Class[]{clazz},
                new CallProxy( next ) ) );
    }

    private static void log( final String str )
    {
        Logger.getLogger( Driver.class.getName() ).info( str );
    }

    private Object next;

    public CallProxy( Object next )
    {
        this.next = next;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
    {
        if ( !method.getDeclaringClass().equals( Object.class ) )
        {
            String call = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(";
            if ( args != null )
            {
                String comma = "";
                for ( Object arg : args )
                {
                    call += comma + (arg == null ? "null" : arg.toString());
                    comma = ", ";
                }
            }
            call += ")";

            log( call );
            try
            {
                final Object result = method.invoke( next, args );
                if ( !method.getReturnType().equals( Void.TYPE ) )
                {
                    log( "->" + result + "\n" );
                }
                return result;
            }
            catch ( InvocationTargetException e )
            {
                StringWriter str = new StringWriter();
                PrintWriter print = new PrintWriter( str, true );
                e.printStackTrace( print );
                throw e.getTargetException();
            }
        }
        else
        {
            return method.invoke( next, args );
        }
    }
}
