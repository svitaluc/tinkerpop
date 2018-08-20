/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.server.util.processedResultLog;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public abstract interface ProcessedResultLogger {
    public String log(Iterator it);

    static String getStringResult(List<List<Object>> list) {
        return list.stream().map(l -> l.stream().map(Object::toString)
                .collect(Collectors.joining(", "))).collect(Collectors.joining("\n"));
    }

    public static class Exceptions {

        protected Exceptions() {
        }

        protected static IllegalArgumentException unsupportedResultTypeForGivenMethod() {
            return new IllegalArgumentException("Requested method is not supported by the result type of a given query.");
        }
    }
}
