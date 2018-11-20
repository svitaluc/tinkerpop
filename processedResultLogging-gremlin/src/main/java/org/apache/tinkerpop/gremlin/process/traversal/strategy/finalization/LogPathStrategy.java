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
package org.apache.tinkerpop.gremlin.process.traversal.strategy.finalization;

import org.apache.tinkerpop.gremlin.process.computer.traversal.step.map.VertexProgramStep;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.PathStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.sideEffect.ProfileSideEffectStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.EmptyStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ProfileStep;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.AbstractTraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.server.Settings;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.processedResultLogging.ProcessedResultManager;
import org.apache.tinkerpop.processedResultLogging.formatter.LLOPJsonFormatter;

import java.util.Arrays;
import java.util.List;

/**
 * This strategy is used for local logging of {@link org.apache.tinkerpop.processedResultLogging.result.ProcessedResult} without the need to run separate gremlin-server instance.
 */
public final class LogPathStrategy extends AbstractTraversalStrategy<TraversalStrategy.FinalizationStrategy> implements TraversalStrategy.FinalizationStrategy {

    private static final LogPathStrategy INSTANCE = new LogPathStrategy();
    private static final String MARKER = Graph.Hidden.hide("gremlin.logPath");
    private ProcessedResultManager.Settings logSettings;

    private LogPathStrategy() {
        logSettings = new ProcessedResultManager.Settings();
        logSettings.enabled = true;
        logSettings.formatter = LLOPJsonFormatter.class.getName();
        ProcessedResultManager.injectLocalSetting(logSettings);
    }

    @Override
    public void apply(final Traversal.Admin<?, ?> traversal) {
        if(traversal instanceof DefaultGraphTraversal && !traversal.getEndStep().getLabels().contains(MARKER) && ! (traversal.getEndStep() instanceof PathStep)) {
            traversal.getStrategies().removeStrategies(LogPathStrategy.class);
//            System.out.println(Arrays.toString(traversal.getSteps().toArray()));
            ProcessedResultManager.INST.log(null,traversal);
        }

    }

    public static LogPathStrategy instance() {
        return INSTANCE;
    }
}
