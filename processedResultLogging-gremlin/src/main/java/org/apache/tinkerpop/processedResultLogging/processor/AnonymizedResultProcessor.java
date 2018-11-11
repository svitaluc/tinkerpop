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
package org.apache.tinkerpop.processedResultLogging.processor;

import org.apache.tinkerpop.processedResultLogging.result.ProcessedResult;

import java.util.Iterator;

/**
 * An {@link AnonymizedResultProcessor} extends {@link ResultProcessor} with an anonymized option of a processed result.
 */
public interface AnonymizedResultProcessor extends ResultProcessor {
    /**
     * Processes the original result in a form of an {@link Iterator} to create an anonymized processed result.
     * @param it - original iterator that produces required result
     * @return anonymized processed result which is an outcome of a specific method used in a specific implementation
     *      * of the {@link ResultProcessor}
     */
    public ProcessedResult processAnonymously(Iterator it);
}
