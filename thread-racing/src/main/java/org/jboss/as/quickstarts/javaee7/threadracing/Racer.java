/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.threadracing;

import org.jboss.as.quickstarts.threadracing.stage.jms.JMSRaceStage;
import org.jboss.as.quickstarts.threadracing.stage.json.JsonRaceStage;
import org.jboss.as.quickstarts.threadracing.stage.websocket.WebSocketRaceStage;

import javax.inject.Inject;
import java.util.UUID;

/**
 * A racer, a runnable task to be executed by a thread.
 *
 * @author Eduardo Martins
 */
public class Racer implements Runnable {

    /**
     * cdi injection of the JMS race stage
     */
    @Inject
    private JMSRaceStage jmsRaceStage;

    /**
     *
     */
    private final WebSocketRaceStage webSocketRaceStage = new WebSocketRaceStage();

    /**
     *
     */
    private final JsonRaceStage jsonRaceStage = new JsonRaceStage();

    /**
     * the racer's registration , which the racer uses to "interact" with a race
     */
    private Race.Registration registration;

    /**
     * the racer's name
     */
    private final String name;

    /**
     * Mandatory bean's no args constructor, creates a random racer's name.
     */
    public Racer() {
        this("Racer" + UUID.randomUUID());
    }

    /**
     * Creates a racer with the specified name.
     * @param name
     */
    public Racer(String name) {
        this.name = name;
    }

    /**
     * Sets the race's registration.
     * @param registration
     */
    public void setRegistration(Race.Registration registration) {
        this.registration = registration;
    }

    @Override
    public void run() {
        try {
            // the racer is ready
            registration.ready();
            // race on, go
            go();
            // game over
            registration.done();
        } catch (Throwable t) {
            registration.aborted(t);
        }
    }

    /**
     * Execution of the race stages/tasks.
     * @throws Exception
     */
    private void go() throws Exception {
        // 1st stage, JMS 2.0
        jmsRaceStage.run(registration);
        System.out.println(name+" completed JMS stage");
        // 2nd stage, WebSockets 1.0
        webSocketRaceStage.run(registration);
        System.out.println(name+" completed WebSockets stage");
        // 3rd stage, Json 1.0
        jsonRaceStage.run(registration);
        System.out.println(name+" completed Json stage");
        // TODO other stages
    }

    /**
     * Retrieves the racer's name.
     * @return
     */
    public String getName() {
        return name;
    }
}
