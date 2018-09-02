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
package org.apache.tinkerpop.gremlin.server.processedResultLogging.formatter;

import org.apache.tinkerpop.gremlin.server.processedResultLogging.context.LogContext;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.ProcessedResult;


/**
 *  A {@link ProcessedResultFormatter} formats {@link LogContext} and {@link ProcessedResult} into a final log.
 *  The Formatter takes a LogContext and a ProcessedResult ond converts them to a string.
 *  Some formatters (such as the BasicProcessedResultFormatter) don't need to use information from a LogContext.
 */
public interface ProcessedResultFormatter {
    /**
     * Format the given LogContext and ProcessedResult and return the formatted string.
     * @param ctx - the context which pieces of information can be formatted
     * @param result - the processed result to be formatted
     * @return the formatted context information and processed result
     */
    public String format(LogContext ctx, ProcessedResult result);
}
