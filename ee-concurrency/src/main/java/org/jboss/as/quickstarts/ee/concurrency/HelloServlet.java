/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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
package org.jboss.as.quickstarts.ee.concurrency;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <p>
 * A simple servlet taking advantage of EE Concurrency Utilities to trace info wrt requests.
 * </p>
 * 
 * <p>
 * The servlet is registered and mapped to /hello using the {@link WebServlet} annotation. The
 * {@link ManagedExecutorService} is provided through resource injection.
 * </p>
 * 
 * <p>
 * It shows how to detach the execution of an asynchronous task from the request processing thread, so the request is
 * handled quickly, and the thread is able to serve more client requests. The asynchronous tasks are executed using the
 * injected default managed executor service.
 * </p>
 *
 * @author Eduardo Martins
 */
@WebServlet(value = "/hello")
public class HelloServlet extends HttpServlet {

    @Resource
    private ManagedExecutorService managedExecutorService;

    @EJB
    private TraceBean traceBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        // indicate new request to the trace bean using an async task
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    traceBean.newRequest();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
        managedExecutorService.submit(runnable);
        // reply to client
        final PrintWriter writer = resp.getWriter();
        writer.println("<!DOCTYPE HTML>");
        writer.println("<html>");
        writer.println(" <head>");
        writer.println("  <title>Hello Servlet</title>");
        writer.println(" </head>");
        writer.println(" <body>");
        writer.println("<p>Hi there, can't chat now, sorry, but thanks for the visit!</p>");
        writer.println(" </body>");
        writer.println("</html>");
        writer.close();
    }

}
