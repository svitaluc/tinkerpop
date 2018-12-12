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

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.PathStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ImmutablePath;
import org.apache.tinkerpop.processedResultLogging.result.LLOProcessedResult;
import org.apache.tinkerpop.processedResultLogging.result.ProcessedResult;
import org.apache.tinkerpop.processedResultLogging.util.ObjectAnonymizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link PathProcessor} calls a {@link GraphTraversal#path()} method on a copy of an original result.
 */
public class PathProcessor implements AnonymizedResultProcessor {

    private GraphTraversal logIt;

    public ProcessedResult process(Iterator it) throws IllegalArgumentException {
        init(it);
        List<List<Object>> resultList = new ArrayList<>();

        if (!logIt.hasNext()) {
//            System.out.println(logIt);
//            System.out.println(logIt.getClass().getName());
            return new LLOProcessedResult(resultList);
        }
        while (logIt.hasNext()) {
            Object o = logIt.next();
            if (!(o instanceof Path)) throw ResultProcessor.Exceptions.incorrectClassType(Path.class, o.getClass());
            Path p = (ImmutablePath) o;
//            System.out.println(Arrays.toString(p.objects().toArray()));
            resultList.add((p).objects());
        }
        return new LLOProcessedResult(resultList);
    }

    public ProcessedResult processAnonymously(Iterator it) {
        init(it);
        List<List<Object>> resultList = new ArrayList<>();

        if (!logIt.hasNext()) {
//            System.out.println(logIt);
//            System.out.println(logIt.getClass().getName());
            return new LLOProcessedResult(resultList);
        }
        while (logIt.hasNext()) {
            Object o = logIt.next();
            if (!(o instanceof Path)) throw ResultProcessor.Exceptions.incorrectClassType(Path.class, o.getClass());
            Path p = (ImmutablePath) o;
//            System.out.println(Arrays.toString(p.objects().toArray()));
            resultList.add(p.objects().stream().map(ObjectAnonymizer::toString).collect(Collectors.toList()));
        }
        return new LLOProcessedResult(resultList);

    }

    private void init(Iterator it) throws IllegalArgumentException {
        if (!(it instanceof DefaultGraphTraversal)) {
            throw ResultProcessor.Exceptions.unsupportedResultTypeForGivenMethod();
        }

        logIt = ((DefaultGraphTraversal) it).clone();
        if (!(((DefaultGraphTraversal) logIt).getEndStep() instanceof PathStep)) {
            logIt = logIt.path();
        }
    }
}
