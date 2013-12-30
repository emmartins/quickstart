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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A singleton bean that will persist the number of new requests per minute in the default datasource, and provide reports.
 * @author Eduardo Martins
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class TraceBean {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private ManagedScheduledExecutorService managedScheduledExecutorService;

    @Resource
    private UserTransaction userTransaction;

    private volatile long currentRequests = 0L;
    private volatile long currentMinutes = 0L;

    private ScheduledFuture<?> scheduledFuture;

    private volatile long todayRequests = 0L;
    private volatile long totalRequests = 0L;

    /**
     * Starts the trace bean timer task
     */
    @PostConstruct
    private synchronized void start() {
        // each period
        this.currentMinutes = TimeUnit.NANOSECONDS.toMinutes(System.nanoTime());
        // schedule a task to update datasource when there are no new currentRequests
        Runnable periodicTask = new Runnable() {
            @Override
            public void run() {
                try {
                    newTimeout();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        scheduledFuture = managedScheduledExecutorService.scheduleAtFixedRate(periodicTask, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Stops the trace bean timer task
     */
    @PreDestroy
    private synchronized void stop() {
        scheduledFuture.cancel(false);
        updateDataSource();
    }

    /**
     * A new request was handled.
     */
    public synchronized void newRequest() {
        updateDataSource();
        currentRequests++;
        todayRequests++;
        totalRequests++;
    }

    private synchronized void newTimeout() {
        updateDataSource();
    }

    private void updateDataSource() {
        final long minutes = TimeUnit.NANOSECONDS.toMinutes(System.nanoTime());
        if (minutes == this.currentMinutes) {
            // still on same minute, no update needed
            return;
        }
        if (currentRequests > 0) {
            try {
                userTransaction.begin();
                // persist the new periodic trace data
                final PeriodicTrace periodicTrace = new PeriodicTrace();
                periodicTrace.setTime(this.currentMinutes);
                periodicTrace.setRequests(this.currentRequests);
                em.persist(periodicTrace);
                userTransaction.commit();
            } catch (Throwable t) {
                t.printStackTrace();
                try {
                    userTransaction.rollback();
                } catch (Throwable t1) {
                    t1.printStackTrace();
                }
                return;
            }
            // update local report
            todayRequests = (Long) em.createQuery("SELECT coalesce(SUM(pt.requests),0) FROM PeriodicTrace pt WHERE pt.time > "+TimeUnit.MINUTES.toDays(minutes)).getSingleResult();
            totalRequests = (Long) em.createQuery("SELECT coalesce(SUM(pt.requests),0) FROM PeriodicTrace pt").getSingleResult();
            // reset currentRequests counter
            this.currentRequests = 0;
        }
        // set update time
        this.currentMinutes = minutes;
    }

    /**
     * Retrieves a html report for the traced requests.
     * @return
     */
    public synchronized String getReport() {
        return new StringBuilder()
                .append("<p>").append("Today Requests: ").append(Long.toString(todayRequests)).append("</p>")
                .append("<p>").append("Total Requests: ").append(Long.toString(totalRequests)).append("</p>")
                .toString();
    }
}
