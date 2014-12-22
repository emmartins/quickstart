package org.jboss.as.quickstarts.threadracing.stage.websocket;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;

/**
 * A stateless web socket server, which echoes any message received.
 * @author Eduardo Martins
 */
@javax.websocket.server.ServerEndpoint(ServerEndpoint.SERVER_ENDPOINT_URI)
public class ServerEndpoint {

    /**
     * the web socket uri, related to the web app root.
     */
    public static final String SERVER_ENDPOINT_URI = "/ws";

    /**
     * Handles received client messages, sending these back to the client.
     * @param message
     * @param session
     */
    @OnMessage
    public void processMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
