package handle;

import jakarta.websocket.Session;
import proto.Proto;
import redis.cache.MessageCache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatHandle implements HandleRequest{
    @Override
    public void onMessage(Proto.Packet packet, Session session, HashMap<String, Session> userSession) throws IOException {
        if(packet.hasReqMessage()){
            Proto.Message content = packet.getReqMessage();
            Proto.Message messRes = Proto.Message.newBuilder().setUserFrom(content.getUserFrom()).setUserTo(content.getUserTo()).setContent(content.getContent()).build();
            AtomicInteger onlineCount = new AtomicInteger(userSession.size());
            Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResMessage(messRes)).build();
            MessageCache.me().rpush(content.getUserFrom(), content.getUserTo(), content);
            MessageCache.me().rpush(content.getUserTo(), content.getUserFrom(), content);
            Session friendSession = userSession.get(content.getUserTo());
            sendResponse(session, packetWrapper);
            sendResponse(friendSession, packetWrapper);
        }
        if(packet.hasReqLoadMessage()){
            Proto.MessageList.Builder messageList = Proto.MessageList.newBuilder();
            messageList.addAllMessages(MessageCache.me().lrange(packet.getReqLoadMessage().getUsername(), packet.getReqLoadMessage().getFriendname(), 0, -1));
            Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setMessageList(messageList).build()).build();
            sendResponse(session, packetWrapper);
        }
    }
    private void sendResponse(Session session, Proto.PacketWrapper packetWrapper) throws IOException {
        if(session != null && session.isOpen()) {
            System.out.println("Send response: "+ packetWrapper.toString());
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(packetWrapper.toByteArray()));
        }else{
            System.out.println("Session is null or not open");
        }
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
