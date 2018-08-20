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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ImmutablePath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PathLogger implements ProcessedResultLogger {

    public String log(Iterator it) throws IllegalArgumentException{
        if (!(it instanceof DefaultGraphTraversal)) {
            throw ProcessedResultLogger.Exceptions.unsupportedResultTypeForGivenMethod();
        }
        GraphTraversal logIt = ((DefaultGraphTraversal) it).clone().path();
        List<List<Object>> resultList = new ArrayList<>();

        if (!logIt.hasNext()) {
            return ProcessedResultLogger.getStringResult(resultList);
        }
        while(logIt.hasNext()) {
            resultList.add(((ImmutablePath)logIt.next()).objects());
        }
        return ProcessedResultLogger.getStringResult(resultList);
    }
}
