package redis;

import jakarta.websocket.Session;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManage {
    public static final SessionManage install = new SessionManage();
    public final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    @Getter
    private final String endPointID = SessionID.ownerEndPointID;
    private SessionManage() {

    }
    public void onOpen(Session session) {
        String sessionId = SessionID.of(session.getId()).getSessionId();
        sessionMap.put(sessionId, session);
    }
    public static SessionManage me() {
        return install;
    }
    public Map<String, Session> getAll() {
        return sessionMap;
    }
    public String getSessionIDFormSession(Session session) {
        return SessionID.of(session.getId()).getSessionId();
    }
    public Session get(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionMap.get(sessionId);
    }
}
