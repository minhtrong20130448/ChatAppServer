package handle;

import dao.UserDAO;
import dao.bean.UserBean;
import jakarta.websocket.Session;
import proto.Proto;
import redis.SessionManage;
import redis.cache.FriendCache;
import redis.cache.SessionCache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

public class FriendHandle implements HandleRequest{
    @Override
    public void onMessage(Proto.Packet packet, Session session, HashMap<String, Session> userSession) throws IOException {
        if(packet.hasReqAddFriend()){
            Proto.ReqAddFriend reqAddFriend = packet.getReqAddFriend();
            Proto.PacketWrapper.Builder packetWrapper = Proto.PacketWrapper.newBuilder();
            if(!reqAddFriend.getUsername().equals("") && !reqAddFriend.getFriendname().equals("")){
                System.out.println("co user hoac friend");
                UserBean userFriend = UserDAO.getUser(reqAddFriend.getFriendname());
                if(userFriend != null){
                    System.out.println("thanh cong add friend");
                    FriendCache.me().sadd(reqAddFriend.getUsername(), reqAddFriend.getFriendname());
                    packetWrapper.addPacket(Proto.Packet.newBuilder().setResAddFriend(Proto.ResAddFriend.newBuilder().setStatus(200).setFriendname(reqAddFriend.getFriendname()).build()));
                }else {
                    packetWrapper.addPacket(Proto.Packet.newBuilder().setResAddFriend(Proto.ResAddFriend.newBuilder().setStatus(400).build()));
                }
            }else{
                System.out.println("null user hoac friend");
                packetWrapper.addPacket(Proto.Packet.newBuilder().setResAddFriend(Proto.ResAddFriend.newBuilder().setStatus(400).build()));
            }
            System.out.println("reqAddFriend.getUsername = " + reqAddFriend.getUsername());
            System.out.println("reqAddFriend.getFriendname = " + reqAddFriend.getFriendname());
            sendResponse(session, packetWrapper.build());
        }

        if(packet.hasReqLoadFriends()){
            //Lay ra ten cua user
            System.out.println("Load friends: " + packet.getReqLoadFriends().toString() + ", " + SessionManage.me().getSessionIDFormSession(session));
            //Lay ra danh sach ban be cua user
            Set<String> friends = FriendCache.me().smembers(packet.getReqLoadFriends().toString());
            Proto.FriendList.Builder friendList = Proto.FriendList.newBuilder();

            for (String friend : friends) {
                Proto.Friend.Builder friendBuilder = Proto.Friend.newBuilder();
                friendBuilder.setName(friend);
                friendList.addFriends(friendBuilder);
            }
            Proto.PacketWrapper packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setFriendlist(friendList).build()).build();
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
