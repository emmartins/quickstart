package org.jboss.as.quickstarts.threadracing.stage.websocket;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A web socket client that, when connected to the server, sends a random txt message, and then expects the server sends back the same message.
 *
 * @author Eduardo Martins
 */
@javax.websocket.ClientEndpoint
public class ClientEndpoint {

    /**
     * the txt msg sent to server
     */
    private transient String messageSent;

    /**
     * a count down latch used to release threads blocked, waiting for the client/server interaction to complete.
     */
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * the exception thrown if the client/server interaction does not succeeds
     */
    private transient Exception exception;

    /**
     * Session opened handler. A random txt message is sent to server.
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        try {
            messageSent = UUID.randomUUID().toString();
            session.getBasicRemote().sendText(messageSent);
        } catch (Throwable t) {
            processError(session, t);
        }
    }

    /**
     * The server msg handler. The session ends after receiving a message. Session succeeds only if a msg was sent to server, and it's the same message received.
     * @param messageReceived
     * @param session
     */
    @OnMessage
    public void processMessage(String messageReceived, Session session) {
        Exception exception = null;
        if (messageSent == null) {
            exception = new IllegalStateException("the client did not sent a message");
        } else if (!messageSent.equals(messageReceived)) {
            exception = new IllegalStateException("Message received does not matches the message sent. Received: " + messageReceived + ", sent: " + messageSent);
        }
        done(session, exception);
    }

    /**
     * The handler for errors, if invoked fails the session.
     * @param session
     * @param throwable
     */
    @OnError
    public void processError(Session session, Throwable throwable) {
        done(session, new IllegalStateException(throwable));
    }

    /**
     * The session is done. The optional exception, if provided, indicates the session did not succeed.
     * @param session
     * @param exception
     */
    private void done(Session session, Exception exception) {
        // save the exception
        this.exception = exception;
        // ensure session is closed
        try {
            session.close();
        } catch (Throwable ignore) {
        }
        // release any thread waiting for interaction to be done.
        countDownLatch.countDown();

    }

    /**
     * Awaits for the session to be done, blocking the invoking thread. A blocked thread will be released if the session is not done in 15 secs.
     * @throws Exception
     */
    public void awaitDone() throws Exception {
        countDownLatch.await(15, TimeUnit.SECONDS);
        if (exception != null) {
            throw exception;
        }
    }
}
