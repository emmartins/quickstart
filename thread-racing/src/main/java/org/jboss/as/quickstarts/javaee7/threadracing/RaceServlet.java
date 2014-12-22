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

import org.jboss.as.quickstarts.threadracing.legends.JimmieThronson;
import org.jboss.as.quickstarts.threadracing.legends.MichaelThrumacher;
import org.jboss.as.quickstarts.threadracing.legends.SebastienThroeb;
import org.jboss.as.quickstarts.threadracing.legends.ValentinoThrossi;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A servlet that runs a race when requested, returning back the results.
 *
 * @author Eduardo Martins
 */
@WebServlet("/race")
public class RaceServlet extends HttpServlet {

    /**
     * injection of the default managed thread factory instance, introduced by EE Concurrency 1.0 (JSR 236),
     * which apps may use to create threads, which tasks run with the invocation context present on the thread creation.
     */
    @Resource
    private ManagedThreadFactory threadFactory;

    /**
     * racer #1
     */
    @Inject
    private JimmieThronson racer1;

    /**
     * racer #2
     */
    @Inject
    private MichaelThrumacher racer2;

    /**
     * racer #3
     */
    @Inject
    private SebastienThroeb racer3;

    /**
     * racer #4
     */
    @Inject
    private ValentinoThrossi racer4;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            // race setup
            final Map<String, String> environment = new HashMap<>();
            environment.put(EnvironmentProperties.SERVER_NAME, req.getServerName());
            environment.put(EnvironmentProperties.SERVER_PORT, String.valueOf(req.getServerPort()));
            environment.put(EnvironmentProperties.WEB_APP_CONTEXT_PATH, req.getContextPath());
            final Race race = new Race(4, environment);
            race.register(racer1);
            race.register(racer2);
            race.register(racer3);
            race.register(racer4);
            // racers, start your engines (a.k.a. threads)
            threadFactory.newThread(racer1).start();
            threadFactory.newThread(racer2).start();
            threadFactory.newThread(racer3).start();
            threadFactory.newThread(racer4).start();
            // show the green flag, let's raceeee!
            race.start();
            // await for the results
            final Map<Racer, Integer> results = race.getResults();
            // send back the results
            final PrintWriter writer = resp.getWriter();
            writer.println("<html><head><title>Java EE Thread Racing</title></head><body>");
            writer.println("<h2>Official Race Results</h2>");
            writer.println("<ul>");
            printRacerResult(racer1, results.get(racer1), writer);
            printRacerResult(racer2, results.get(racer2), writer);
            printRacerResult(racer3, results.get(racer3), writer);
            printRacerResult(racer4, results.get(racer4), writer);
            writer.println("</ul>");
            writer.println("</body></html>");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void printRacerResult(Racer racer, Integer result, PrintWriter writer) {
        if (result != null) {
            writer.println("<li>"+racer.getName()+" finished in place number "+result+"</li>");
        } else {
            writer.println("<li>"+racer.getName()+" did not finish the race"+"</li>");
        }
    }
}
