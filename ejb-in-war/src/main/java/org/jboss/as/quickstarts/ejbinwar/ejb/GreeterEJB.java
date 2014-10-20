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
package org.jboss.as.quickstarts.ejbinwar.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

/**
 * A simple Hello World EJB. The EJB does not use an interface.
 * 
 * @author paul.robinson@redhat.com, 2011-12-21
 */
@Singleton
public class GreeterEJB {
    /**
     * This method takes a name and returns a personalised greeting.
     * 
     * @param name the name of the person to be greeted
     * @return the personalised greeting.
     */
    public String sayHello(String name) {
        return "Hello " + name;
    }

    private static final String name = "java:jboss/exported/JNDIBinderBean";
    private static final String value = "JNDIBinderBean instantiated at " + new Date();

    @PostConstruct
    public void start() {
        try {
            new InitialContext().rebind(name, value);
            System.out.println("PostConstruct rebind ok");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        try {
            new InitialContext().unbind(name);
            System.out.println("PreDestroy unbind ok");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

}
