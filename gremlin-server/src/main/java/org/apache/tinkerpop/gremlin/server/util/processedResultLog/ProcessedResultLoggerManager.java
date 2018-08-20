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

import org.apache.tinkerpop.gremlin.server.GremlinServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.lang.Class;

public final class ProcessedResultLoggerManager {

    public static final ProcessedResultLoggerManager INST = new ProcessedResultLoggerManager();

    private Map<String, Class<? extends ProcessedResultLogger>> loggerMap = new HashMap<String, Class<? extends ProcessedResultLogger>>() {{
        put("path", PathLogger.class);
    }};

    private static final Logger logger = LoggerFactory.getLogger(GremlinServer.PROCESSED_RESULT_LOGGER_NAME);

    private ProcessedResultLoggerManager() {
    }

    public void log(Iterator it, String method) {
        try {
            Class<? extends  ProcessedResultLogger> prlClazz = loggerMap.get(method);
            if (prlClazz == null) {
                logger.info("Requested method " + method + " is not supported");
                return;
            }
            ProcessedResultLogger prl = prlClazz.newInstance();
            logger.info("Processed result with method " + method + ":\nPRL-CSV-START:\n" + prl.log(it) + "\nPRL-CSV-STOP");
        } catch (IllegalAccessException | InstantiationException e) {
            logger.info("Requested method " + method + " has wrong implementation");
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
        }
    }
}
