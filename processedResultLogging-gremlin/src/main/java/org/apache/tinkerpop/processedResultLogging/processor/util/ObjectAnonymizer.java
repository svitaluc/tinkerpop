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
package org.apache.tinkerpop.processedResultLogging.processor.util;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * An {@link ObjectAnonymizer} creates an anonymized version of an object. It expects the object to be either instance of
 *    - an {@link Edge}
 *    - a {@link Vertex}
 *    - other
 * It anonymizes only an {@link Edge} or a {@link Vertex} object and returns null for other object types.
 */
public class ObjectAnonymizer {
    /**
     * Creates anonymized string of an Object. An object is anonymized as:
     *    - an {@link Edge}: "e:<id>"
     *    - a {@link Vertex}: "v:<id>"
     *    - other: null
     * @param object - an {@link Edge}, a {@link Vertex} or another type of Object
     * @return an anonymized string value of an object, null if the input object isn't an {@link Edge} or a {@link Vertex}
     */
    public static String toString(Object object) {
        if (object instanceof Edge) {
            return "e:" + ((Edge) object).id();
        }
        else if (object instanceof Vertex) {
            return "v:" + ((Vertex) object).id();
        }
        return null;
    }
}
