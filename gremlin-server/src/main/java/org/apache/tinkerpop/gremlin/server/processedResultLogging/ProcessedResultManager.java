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
package org.apache.tinkerpop.gremlin.server.processedResultLogging;

import org.apache.tinkerpop.gremlin.server.Context;
import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.formatter.BasicProcessedResultFormatter;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.formatter.ProcessedResultFormatter;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.AnonymizedResultProcessor;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.PathProcessor;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.ResultProcessor;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.ProcessedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public final class ProcessedResultManager {

    public static final ProcessedResultManager INST = new ProcessedResultManager();
    private static final Logger processedResultLogger = LoggerFactory.getLogger(GremlinServer.PROCESSED_RESULT_LOGGER_NAME);
    private ProcessedResultFormatter formatter = new BasicProcessedResultFormatter();
    private ResultProcessor processor = new PathProcessor();

    private ProcessedResultManager() {

    }

    public void log(Context ctx, Iterator it) {
        Settings.ProcessedResultLogSettings settings = ctx.getSettings().processedResultLog;
        try {
            if (!this.processor.getClass().getName().equals(settings.processor)) {
                try {

                    Class processorClass = Class.forName(settings.processor);
                    this.processor = ((Class<ResultProcessor>) processorClass).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    processedResultLogger.error("Requested processor class: " + settings.formatter + " was not found or could not be instantiated", e);
                    return;
                }
            }
            if (!this.formatter.getClass().getName().equals(settings.formatter)) {
                try {
                    Class newFormatter = Class.forName(settings.formatter);
                    this.formatter = ((Class<ProcessedResultFormatter>) newFormatter).getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    processedResultLogger.error("Requested formatter class: " + settings.formatter + " was not found or could not be instantiated", e);
                    return;
                }
            }
            ProcessedResult result;
            if (settings.anonymized && processor instanceof AnonymizedResultProcessor) {
                result = ((AnonymizedResultProcessor) processor).processAnonymously(it);
            } else if (settings.anonymized) {
                processedResultLogger.error("Requested processor class: " + settings.processor + " does not implement " + AnonymizedResultProcessor.class.getSimpleName());
                return;
            } else {
                result = processor.process(it);
            }
            processedResultLogger.info(formatter.format(ctx, result));
        } catch (IllegalArgumentException e) {
            processedResultLogger.warn(e.getMessage());
        }
        catch (Exception e) {
            processedResultLogger.error("Unexpected exception", e);
        }
    }
}
