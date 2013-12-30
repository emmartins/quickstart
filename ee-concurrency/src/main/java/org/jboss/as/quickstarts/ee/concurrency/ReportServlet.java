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

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * TODO
 *
 * @author Eduardo Martins
 */
@WebServlet(value = "/report")
public class ReportServlet extends HttpServlet {

    @EJB
    private TraceBean traceBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        final PrintWriter writer = resp.getWriter();
        writer.println("<!DOCTYPE HTML>");
        writer.println("<html>");
        writer.println(" <head>");
        writer.println("  <title>Report Servlet</title>");
        writer.println(" </head>");
        writer.println(" <body>");
        writer.println(traceBean.getReport());
        writer.println(" </body>");
        writer.println("</html>");
        writer.close();
    }

}
