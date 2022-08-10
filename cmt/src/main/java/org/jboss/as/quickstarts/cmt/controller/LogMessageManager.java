/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
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
package org.jboss.as.quickstarts.cmt.controller;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import javax.naming.NamingException;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

import org.jboss.as.quickstarts.cmt.ejb.LogMessageManagerEJB;
import org.jboss.as.quickstarts.cmt.model.LogMessage;

@Named("logMessageManager")
@RequestScoped
public class LogMessageManager {
    @Inject
    private LogMessageManagerEJB logMessageManager;

    public List<LogMessage> getLogMessages() throws SecurityException, IllegalStateException,
        NamingException, NotSupportedException, SystemException, RollbackException,
        HeuristicMixedException, HeuristicRollbackException {
        return logMessageManager.listLogMessages();
    }
}
