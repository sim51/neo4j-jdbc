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
package org.neo4j.jdbc.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;

/**
 * @author mh
 * @since 01.04.14
 */
public class Closer {

    protected final static Log log = LogFactory.getLog(Closer.class);

    public static void close(Object data) {
        if (data == null) return;
        if (data instanceof AutoCloseable || data instanceof Closeable) {
            try {
                ((AutoCloseable) data).close();
            } catch (Exception e) {
                log.warn("Couldn't close object "+data.getClass(),e);
            }
        } else if (data instanceof ClosableIterator) {
            ((ClosableIterator)data).close();
        } else if (data.getClass().getName().equals("org.neo4j.helpers.collection.ClosableIterator")) {
            try {
                data.getClass().getMethod("close").invoke(data);
            } catch (Exception e) {
                log.warn("Couldn't close object "+data.getClass(),e);
            }
        }
    }
}
