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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ImmutablePath;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.processor.util.ObjectAnonymizer;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.LLOProcessedResult;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.LSProcessedResult;
import org.apache.tinkerpop.gremlin.server.processedResultLogging.result.ProcessedResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

public class PathProcessor implements AnonymizedResultProcessor {

    private GraphTraversal logIt;

    public ProcessedResult process(Iterator it) throws IllegalArgumentException {
        init(it);
        List<List<Object>> resultList = new ArrayList<>();

        if (!logIt.hasNext()) {
            return new LLOProcessedResult(resultList);
        }
        while (logIt.hasNext()) {
            resultList.add(((ImmutablePath) logIt.next()).objects());
        }
        return new LLOProcessedResult(resultList);
    }

    public ProcessedResult processAnonymously(Iterator it) {
        init(it);
        List<String> resultList = new ArrayList<>();

        if (!logIt.hasNext()) {
            return new LSProcessedResult(resultList);
        }

        while (logIt.hasNext()) {
            List<Object> pathObjects = ((ImmutablePath) logIt.next()).objects();
            StringJoiner pathJoiner = new StringJoiner(",");
            for (int i = 0; i < pathObjects.size(); i++) {
                String anonymizedObject = ObjectAnonymizer.toString(pathObjects.get(i));
                if (anonymizedObject != null) {
                    pathJoiner.add(anonymizedObject);
                }
            }
            resultList.add(pathJoiner.toString());
        }
        return new LSProcessedResult(resultList);
    }

    private void init(Iterator it) throws IllegalArgumentException{
        if (!(it instanceof DefaultGraphTraversal)) {
            throw ResultProcessor.Exceptions.unsupportedResultTypeForGivenMethod();
        }
        logIt = ((DefaultGraphTraversal) it).clone().path();
    }
}
