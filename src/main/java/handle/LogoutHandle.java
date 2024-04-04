package handle;

import jakarta.websocket.Session;
import proto.Proto;
import redis.SessionManage;
import redis.cache.SessionCache;
import redis.context.SessionContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class LogoutHandle implements HandleRequest{
    @Override
    public void onMessage(Proto.Packet packet, Session session, HashMap<String, Session> userSession) throws IOException {
        String sessionId = SessionManage.me().getSessionIDFormSession(session);
        SessionContext sessionContext = SessionCache.me().get(sessionId);
        //SessionCache.me().logout(sessionContext);
        SessionCache.me().remove(sessionId);
        userSession.remove(packet.getReqLogout().getUsername());
        System.out.println("SessionContextMap: size: " + SessionCache.me().getSessionContextMap().size()+", "+SessionCache.me().getSessionContextMap());
    }

    @Override
    public void onOpen(Session session) throws IOException {

    }

    @Override
    public void onClose(Session session) throws IOException {

    }

    @Override
    public void onError(Session session, Throwable throwable) throws IOException {

    }
}
