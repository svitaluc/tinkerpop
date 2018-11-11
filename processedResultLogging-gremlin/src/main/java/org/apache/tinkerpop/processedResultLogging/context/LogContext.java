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
package org.apache.tinkerpop.processedResultLogging.context;

import org.apache.tinkerpop.gremlin.server.Context;
import org.apache.tinkerpop.processedResultLogging.formatter.ProcessedResultFormatter;

/**
 * A {@link LogContext} encapsulates {@link Context} for easier usage and controlled behavior of anonymized logs.
 * A {@link LogContext} is used in {@link ProcessedResultFormatter} to provide information for a final log.
 */
public abstract class LogContext {
    /**
     * Original context containing all the related information that can be acquired with corresponding getter methods.
     */
    protected Context ctx;

    public LogContext(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Gets query that produced a corresponding result. Depending on the implementation of a {@link LogContext} class,
     * the returned object can be either in an original or an adjusted form to meet the objective of that subclass.
     * @return query in a requested form
     */
    public abstract Object getQuery();
}
