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
import org.apache.tinkerpop.gremlin.server.processedResultLogging.context.AnonymizedContext;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.context.LogContext;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.context.OriginalContext;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.formatter.BasicProcessedResultFormatter;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.formatter.ProcessedResultFormatter;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.AnonymizedResultProcessor;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.PathProcessor;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.ResultProcessor;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.ProcessedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents an entry point to processedResultLogging package.
 * In order to create a processed result log, use a {@link ProcessedResultManager}. It is a singleton class
 * which instance can call a log method.
 * En example usage:
 *    ProcessedResultManager.INST.log(ctx, it);
 */
public final class ProcessedResultManager {

    public static final ProcessedResultManager INST = new ProcessedResultManager();
    private static final Logger processedResultLogger = LoggerFactory.getLogger(GremlinServer.PROCESSED_RESULT_LOGGER_NAME);
    private ProcessedResultFormatter formatter = new BasicProcessedResultFormatter();
    private ResultProcessor processor = new PathProcessor();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private ProcessedResultManager() {
    }

    public void log(Context ctx, Iterator it) {
        executor.submit(() -> logAsync(ctx, it));
    }

    private void logAsync(Context ctx, Iterator it) {
        // init processor
        Settings.ProcessedResultLogSettings settings = ctx.getSettings().processedResultLog;
        if (!this.processor.getClass().getName().equals(settings.processor)) {
            try {
                Class processorClass = Class.forName(settings.processor);
                this.processor = ((Class<ResultProcessor>) processorClass).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                processedResultLogger.error("Requested processor class: " + settings.formatter + " was not found or could not be instantiated", e);
                return;
            }
        }
        // init formatter
        if (!this.formatter.getClass().getName().equals(settings.formatter)) {
            try {
                Class newFormatter = Class.forName(settings.formatter);
                this.formatter = ((Class<ProcessedResultFormatter>) newFormatter).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                processedResultLogger.error("Requested formatter class: " + settings.formatter + " was not found or could not be instantiated", e);
                return;
            }
        }
        // create processed result, log context and log the data with a formatter
        try{
            ProcessedResult result;
            LogContext logCtx;
            if (settings.anonymized && processor instanceof AnonymizedResultProcessor) {
                result = ((AnonymizedResultProcessor) processor).processAnonymously(it);
                logCtx = new AnonymizedContext(ctx);
            } else if (settings.anonymized) {
                processedResultLogger.error("Requested processor class: " + settings.processor + " does not implement " + AnonymizedResultProcessor.class.getSimpleName());
                return;
            } else {
                result = processor.process(it);
                logCtx = new OriginalContext(ctx);
            }
            processedResultLogger.info(formatter.format(logCtx, result));

          // an IllegalArgumentException thrown by a ResultProcessor. This exception is expected to sometimes occur,
          // f.e. when a result type doesn't correspond to an expected result type (f.e. other than a GraphTraversal type
          // in the case of a PathProcessor)
        } catch (IllegalArgumentException e) {
            processedResultLogger.warn(e.getMessage());

          // an UnsupportedOperationException thrown by an AnonymizedContext which doesn't implement required methods yet
        } catch (UnsupportedOperationException e) {
            processedResultLogger.error("Requested formatter logs data that are not yet supported in an anonymized output. Set anonymized property false or use another formatter.");

          // an unexpected exception
        } catch (Exception e) {
            processedResultLogger.error("Unexpected exception", e);
        }
    }
}
