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
package org.apache.tinkerpop.processedResultLogging.formatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.tinkerpop.processedResultLogging.context.LogContext;
import org.apache.tinkerpop.processedResultLogging.result.LLOProcessedResult;
import org.apache.tinkerpop.processedResultLogging.result.ProcessedResult;

public class LLOPJsonFormatter implements ProcessedResultFormatter {
    private static Gson gson;
    static {
        gson = new GsonBuilder().registerTypeAdapter(LLOProcessedResult.class, new LLOProcessedResult.Serializer()).create();
    }
    @Override
    public String format(LogContext ctx, ProcessedResult result) throws Exception {
        if(!(result instanceof LLOProcessedResult))
            throw new Exception("The result for this formatter needs to be of type LLOProcessedResult");
        return gson.toJson(result);
    }
}
