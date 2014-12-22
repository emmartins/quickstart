package org.jboss.as.quickstarts.threadracing;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The awesome Java EE thread race.
 *
 * @author Eduardo Martins
 */
public class Race {

    private final CyclicBarrier start;
    private final CountDownLatch end;
    private final ConcurrentHashMap<Racer, Integer> results;
    private final AtomicInteger position = new AtomicInteger(0);
    private final Map<String, String> environment;

    /**
     * Creates a new race with the specified number of racers.
     * @param racers
     * @param environment
     */
    public Race(int racers, Map<String, String> environment) {
        this.environment = environment;
        this.start = new CyclicBarrier(racers + 1);
        this.end = new CountDownLatch(racers);
        this.results = new ConcurrentHashMap<>();
    }

    /**
     * Registers the specified racer in the race.
     * @param racer
     */
    public void register(Racer racer) {
        racer.setRegistration(new Registration(racer));
    }

    /**
     * Starts the race (when all racers are registered and ready). There is a 5 sec timeout for the race start to happen.
     * @throws Exception
     */
    public void start() throws Exception {
        start.await(5, TimeUnit.SECONDS);
    }

    /**
     * Retrieves the race results. If the race is still in progress this method will block and wait for the race to end. The wait has a timeout of 60 seconds.
     * @return a map containing the position of each racer that finished the race.
     * @throws Exception
     */
    public Map<Racer, Integer> getResults() throws Exception {
        end.await(60, TimeUnit.SECONDS);
        return Collections.unmodifiableMap(results);
    }

    /**
     * The racer's registration.
     */
    public class Registration {

        /**
         * the registration's racer
         */
        private final Racer racer;

        /**
         *
         * @param racer
         */
        private Registration(Racer racer) {
            this.racer = racer;
        }

        /**
         * The racer is reader to start the race.
         * @throws Exception
         */
        public void ready() throws Exception {
            start.await(5, TimeUnit.SECONDS);
        }

        /**
         * The racer has finished the race.
         */
        public void done() {
            int racerPosition = position.incrementAndGet();
            results.put(racer, racerPosition);
            System.out.println(racer.getName()+" finished the race, final position is "+racerPosition);
            end.countDown();
        }

        /**
         * The racer has aborted the race.
         */
        public void aborted(Throwable t) {
            System.out.println(racer.getName()+" aborted");
            t.printStackTrace();
            end.countDown();
        }

        /**
         * Retrieves the race's environment.
         * @return
         */
        public Map<String, String> getEnvironment() {
            return environment;
        }
    }
}
