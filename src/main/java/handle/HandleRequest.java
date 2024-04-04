package handle;

import jakarta.websocket.Session;
import proto.Proto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public interface HandleRequest {
    public void onMessage(Proto.Packet packet, Session session, HashMap<String, Session> userSession) throws IOException;
    public void onOpen(Session session) throws IOException;
    public void onClose(Session session) throws IOException;
    public void onError(Session session, Throwable throwable) throws IOException;
}
