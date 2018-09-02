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
package org.apache.tinkerpop.gremlin.server.processedResultLogging.processor;

import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.ProcessedResult;

import java.util.Iterator;

/**
 * A {@link ResultProcessor} processes the original result to create a {@link ProcessedResult}. It doesn't change
 * the original result. It runs a specific implementation of the {@link ResultProcessor} calling
 * a method on a copy of the result.
 */
public interface ResultProcessor {
    /**
     * Processes the original result in a form of an {@link Iterator} to create a processed result.
     * @param it - original iterator that produces required result
     * @return processed result which is an outcome of a specific method used in a specific implementation
     * of the {@link ResultProcessor}
     */
    public ProcessedResult process(Iterator it);


    /**
     * Common exceptions to use with a result processor.
     */
    public static class Exceptions {

        protected Exceptions() {
        }

        protected static IllegalArgumentException unsupportedResultTypeForGivenMethod() {
            return new IllegalArgumentException("Requested processor does not support a result type of the given query.");
        }
    }
}
