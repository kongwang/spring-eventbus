/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kong.spring.eventbus.memory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Posts events in background.
 *
 * @author Markus
 */
final class BackgroundPoster implements Runnable, Poster {

    private final static ExecutorService execute = Executors.newCachedThreadPool();
    private final PendingPostQueue queue;
    private final MemoryEventBus eventBus;
    private volatile boolean executorRunning;

    BackgroundPoster(MemoryEventBus eventBus) {
        this.eventBus = eventBus;
        queue = new PendingPostQueue();
    }

    @Override
    public void enqueue(Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(event);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                execute.execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    PendingPost pendingPost = queue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll();
                            if (pendingPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }
                    eventBus.invokeSubscriber(pendingPost.event);
                }
            } catch (InterruptedException e) {

            }
        } finally {
            executorRunning = false;
        }
    }

}
