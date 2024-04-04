package socket;

import handle.*;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import proto.Proto;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
@ServerEndpoint("/chat")
public class ChatServer {
    private static Set<Session> userSession= new HashSet<>();
    private static HashMap<String, Session> userNames = new HashMap<>();
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    @OnOpen
    public void onOpen(Session session) throws IOException {
        userSession.add(session);
//       synchronized (onlineCount) {
//            onlineCount.incrementAndGet();
//            for (Session s : userNames) {
//                s.getBasicRemote().sendText("Truy cập:"+String.valueOf(onlineCount.get()));
//            }
//
        System.out.println("Connected ... " + session.getId());
    }
    @OnMessage
    public void onMessage(byte[] message,  Session session) throws IOException {
         Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.parseFrom(message);
         packetWrapper.getPacketList().forEach(packet -> {
             switch (packet.getDataCase()) {
                 case REQLOGIN:
                     try {
                         new LoginHandle().onMessage(packet, session, userNames);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                     break;
                 case REQMESSAGE:
                 case REQLOADMESSAGE:
                     try {
                         new ChatHandle().onMessage(packet, session, userNames);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                     break;
                 case REQADDFRIEND:
                 case REQLOADFRIENDS:
                     try {
                         new FriendHandle().onMessage(packet, session, userNames);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                     break;
                 case REQSIGNIN:
                     Proto.ReqSignin reqSignin = packet.getReqSignin();
                     try {
                         new SignHandle().onMessage(packet, session, userNames);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                     break;
                 case REQLOGOUT:
                     try {
                         new LogoutHandle().onMessage(packet, session, userNames);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                     break;
                 default:
                     // Handle other cases if necessary
                     break;
             }
         });


    }
    @OnClose
    public void onClose(Session session) throws IOException {
        synchronized (onlineCount) {
            onlineCount.decrementAndGet();
            for (Session s : userSession) {
                if(!s.equals(session))
                    s.getBasicRemote().sendText("Truy cập:"+String.valueOf(onlineCount.get()));
            }
        }
        userSession.remove(session);
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    private void notifyMessageToRoom(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }
}
