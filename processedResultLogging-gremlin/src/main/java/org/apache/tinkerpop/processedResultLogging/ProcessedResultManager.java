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
package org.apache.tinkerpop.processedResultLogging;
import org.apache.tinkerpop.processedResultLogging.formatter.BasicProcessedResultFormatter;
import org.apache.tinkerpop.processedResultLogging.formatter.LLOPJsonFormatter;
import org.apache.tinkerpop.processedResultLogging.formatter.ProcessedResultFormatter;
import org.apache.tinkerpop.processedResultLogging.processor.AnonymizedResultProcessor;
import org.apache.tinkerpop.processedResultLogging.processor.PathProcessor;
import org.apache.tinkerpop.processedResultLogging.processor.ResultProcessor;
import org.apache.tinkerpop.processedResultLogging.result.ProcessedResult;
import org.apache.tinkerpop.processedResultLogging.util.SimpleLogger;
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
 * ProcessedResultManager.INST.log(ctx, it);
 */
public final class ProcessedResultManager {

    public static final ProcessedResultManager INST = new ProcessedResultManager();
    public static final String PROCESSED_RESULT_LOGGER_NAME = "processed.result.org.apache.tinkerpop.gremlin.server";
    private final Logger serverProcessedResultLogger = LoggerFactory.getLogger(PROCESSED_RESULT_LOGGER_NAME);
    private final Logger localProcessedResultLogger = new SimpleLogger();
    private Logger logger = null;
    private ProcessedResultFormatter formatter = new BasicProcessedResultFormatter();
    private ResultProcessor processor = new PathProcessor();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ProcessedResultManager.Settings settings = new ProcessedResultManager.Settings();

    /**
     * Settings for the {@link ProcessedResultManager} implementation.
     */
    public static class Settings {
        /**
         * Enable processed result logging. Other settings will be ignored unless this is set to true.
         * Default to false when not specified, so no processed result will be logged regardless other
         * properties setting.
         */
        public boolean enabled = true;
        /**
         * Enable processed result logging. Other settings will be ignored unless this is set to true.
         * Default to false when not specified, so no processed result will be logged regardless other
         * properties setting.
         */
        public boolean asyncMode = false;
        /**
         * The fully qualified class name of the {@link ResultProcessor} implementation.
         * This class name will be used to load the implementation from the classpath.
         * Default to {@link PathProcessor} when not specified.
         */
        public String processor = PathProcessor.class.getName();
        /**
         * The fully qualified class name of the {@link ProcessedResultFormatter} implementation.
         * This class name will be used to load the implementation from the classpath.
         * Default to {@link BasicProcessedResultFormatter} when not specified.
         */
        public String formatter = LLOPJsonFormatter.class.getName();
        /**
         * Anonymizes sensitive data in log. Default to false when not specified.
         */
        public boolean anonymized = false;
        /**
         * Sets the mode for local/server logging.
         */
        public boolean localMode = true;
    }

    private void applySetting(){
        if (settings.localMode) logger = localProcessedResultLogger;
        else logger = serverProcessedResultLogger;
    }

    private ProcessedResultManager() {
    }

    public static void injectLocalSetting(ProcessedResultManager.Settings settings) {
        INST.settings = settings;
        INST.applySetting();
    }

    /**
     * Local version of log, gremlin-server context is N/A.
     * @param it the traversal Iterator
     */
    public void log(Iterator it) {
        this.log(null, it);
    }

    public void log(String query, Iterator it) {
        if (settings.asyncMode)
            executor.submit(() -> logAsync(query, it));
        else
            logAsync(query, it);
    }

    private void logAsync(String query, Iterator it) {
        // init processor
        if (!this.processor.getClass().getName().equals(settings.processor)) {
            try {
                Class processorClass = Class.forName(settings.processor);
                this.processor = ((Class<ResultProcessor>) processorClass).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Requested processor class: " + settings.formatter + " was not found or could not be instantiated", e);
                return;
            }
        }
        // init formatter
        if (!this.formatter.getClass().getName().equals(settings.formatter)) {
            try {
                Class newFormatter = Class.forName(settings.formatter);
                this.formatter = ((Class<ProcessedResultFormatter>) newFormatter).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Requested formatter class: " + settings.formatter + " was not found or could not be instantiated", e);
                return;
            }
        }
        // create processed result, log context and log the data with a formatter
        try {
            ProcessedResult result;
            if (settings.anonymized && processor instanceof AnonymizedResultProcessor) {
                result = ((AnonymizedResultProcessor) processor).processAnonymously(it);
            } else if (settings.anonymized) {
                logger.error("Requested processor class: " + settings.processor + " does not implement " + AnonymizedResultProcessor.class.getSimpleName());
                return;
            } else {
                result = processor.process(it);
            }
            logger.info(formatter.format(query, result));

            // an IllegalArgumentException thrown by a ResultProcessor. This exception is expected to sometimes occur,
            // f.e. when a result type doesn't correspond to an expected result type (f.e. other than a GraphTraversal type
            // in the case of a PathProcessor)
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());

            // an UnsupportedOperationException thrown by an AnonymizedContext which doesn't implement required methods yet
        } catch (UnsupportedOperationException e) {
            logger.error("Requested formatter logs data that are not yet supported in an anonymized output. Set anonymized property false or use another formatter.");

            // an unexpected exception
        }
    }
}
