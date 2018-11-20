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
package org.apache.tinkerpop.processedResultLogging.result;

import com.google.gson.*;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link LLOProcessedResult} is a {@link ProcessedResult} of a type List<List<Object>>.
 */
public class LLOProcessedResult extends ProcessedResult {
    public static class Serializer implements JsonSerializer<LLOProcessedResult> {
        @Override
        public JsonElement serialize(LLOProcessedResult src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonResult = new JsonObject();
            jsonResult.addProperty("Q", "");
            JsonArray jsonQueryResults = new JsonArray();
            for (List<Object> path : src.result) {
                JsonArray jsonPath = new JsonArray();
                for (Object element : path) {
                    JsonObject jsonObject = new JsonObject();
                    if (element instanceof Vertex)
                        jsonObject.addProperty("v", (Number) ((Vertex) element).id());
                    else if (element instanceof Edge)
                        jsonObject.addProperty("e", (Number) ((Edge) element).id());
                    else
                        jsonObject.addProperty("unknownType", element.getClass().getSimpleName());
                    jsonPath.add(jsonObject);
                }
                jsonQueryResults.add(jsonPath);
            }
            jsonResult.add("R", jsonQueryResults);
            return jsonResult;
        }
    }

    private List<List<Object>> result;

    public LLOProcessedResult(List<List<Object>> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return result.stream().map(l -> l.stream().map(Object::toString)
                .collect(Collectors.joining(", "))).collect(Collectors.joining("\n"));
    }
}
