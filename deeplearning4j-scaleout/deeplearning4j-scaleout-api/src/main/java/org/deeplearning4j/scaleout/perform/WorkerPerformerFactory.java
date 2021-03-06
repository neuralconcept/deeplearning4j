/*
 * Copyright 2015 Skymind,Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.deeplearning4j.scaleout.perform;


import org.canova.api.conf.Configuration;

/**
 * Create a worker performer
 *
 * @author Adam Gibson
 */
public interface WorkerPerformerFactory {


    public final static String WORKER_PERFORMER = "org.deeplearning4j.scaleout.perform.workerperformer";

    /**
     * Create a worker performer
     * @return
     */
    public WorkerPerformer create();

    /**
     * Create based on the configuration
     * @param conf the configuration
     * @return the performer created based on the configuration
     */
    public WorkerPerformer create(Configuration conf);

}
