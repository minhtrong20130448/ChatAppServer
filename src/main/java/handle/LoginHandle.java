package handle;

import dao.BaseDAO;
import dao.UserDAO;
import dao.bean.UserBean;
import jakarta.websocket.Session;
import proto.Proto;
import redis.Jedis;
import redis.SessionManage;
import redis.cache.SessionCache;
import redis.context.SessionContext;
import utils.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public class LoginHandle implements HandleRequest {
    @Override
    public void onMessage(Proto.Packet packet, Session session, HashMap<String, Session> userSession) throws IOException {
        Proto.ReqLogin reqLogin = packet.getReqLogin();
        UserBean userLoginBean = UserDAO.getUserLogin(reqLogin.getUsername());
        Proto.PacketWrapper.Builder packetWrapper = Proto.PacketWrapper.newBuilder();
        Proto.Packet.Builder packetBuilder = Proto.Packet.newBuilder();
        Proto.ResLogin.Builder resLoginBuilder = Proto.ResLogin.newBuilder();
        // check tai khoan/ mat khau dang login vao he thong
        if(userLoginBean == null || !userLoginBean.getPassword().equals(reqLogin.getPassword())) {
            resLoginBuilder.setStatus(400);
            packetWrapper.addPacket(packetBuilder.setResLogin(resLoginBuilder.build()));
            sendResponse(session, packetWrapper.build());
            System.out.println("Check sai tai khoan: " + reqLogin.getUsername() + " " + reqLogin.getPassword());
            return;
        }
        //check user login tren thiet bi khac
        if (checkLoginOtherDevice(userLoginBean)) {
            resLoginBuilder.setStatus(404);
            packetWrapper.addPacket(packetBuilder.setResLogin(resLoginBuilder.build()));
            sendResponse(session, packetWrapper.build());
            System.out.println("Dang nhap tren thiet bi khac");
            return;
        }

        if(SessionCache.me().get(SessionManage.me().getSessionIDFormSession(session)) != null){ //Kiem tra xem session co dang ton tai khong
            resLoginBuilder.setStatus(500);
            packetWrapper.addPacket(packetBuilder.setResLogin(resLoginBuilder.build()));
            sendResponse(session, packetWrapper.build());
            System.out.println("Dang dang nhap");
            return;
        }

        //Neu thanh cong
        System.out.println("Check dung tai khoan: "+ userLoginBean.toString());
        Proto.User userProto = Proto.User.newBuilder()
                .setUserId(userLoginBean.getId())
                .setUsername(userLoginBean.getUsername())
                .setPlayerName(StringUtils.defaultIfNull(userLoginBean.getPlayerName()))
                .setGender(userLoginBean.getGender())
                .setSponsor(userLoginBean.getSponsor())
                .setEmail(StringUtils.defaultIfNull(userLoginBean.getEmail()))
                .setPhone(StringUtils.defaultIfNull(userLoginBean.getPhone()))
                .build();

        //Lay ra sessionID cua session dang login
        String sessionId = SessionManage.me().getSessionIDFormSession(session);
        //Tao ra session context tu sessionID va userProto
        SessionContext sessionContext = SessionContext.builder()
                .sessionID(sessionId)
                .user(userProto)
                .roomId(123)
                .socketID("socketId")
                .isBot(false)
                .gold(100)
                .build();
        //Them session context vao session cache
        SessionCache.me().add(sessionContext);
        //SessionCache.me().onLogin(userProto, sessionId);
        //Gui thong bao cho client
        System.out.println("SessionContextMap: size: " + SessionCache.me().getSessionContextMap().size()+", " + SessionCache.me().getSessionContextMap());
        resLoginBuilder.setUsername(reqLogin.getUsername());
        resLoginBuilder.setStatus(200);
        packetWrapper.addPacket(packetBuilder.setResLogin(resLoginBuilder.build()).build());
        userSession.put(reqLogin.getUsername(), session);
        sendResponse(session, packetWrapper.build());

//        Proto.PacketWrapper packetWrapper = null;
//        Jedis jedis = Jedis.me();
//        jedis.getConnection();
//        if(jedis.containsKey("user:" + reqLogin.getUsername())) {
//            if(jedis.get("user:" + reqLogin.getUsername()).equals(reqLogin.getPassword())) {
//                packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResLogin(Proto.ResLogin.newBuilder().setStatus(200).setUsername(reqLogin.getUsername()).build())).build();
//            }else{
//                packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResLogin(Proto.ResLogin.newBuilder().setStatus(400).build())).build();
//            }
//        }else {
//            packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResLogin(Proto.ResLogin.newBuilder().setStatus(402).build())).build();
//        }
//        session.getAsyncRemote().sendBinary(ByteBuffer.wrap(packetWrapper.toByteArray()));

    }
    private Boolean checkLoginOtherDevice(UserBean userLogin) {
        //Kiem tra co luu trong session cache khong
       return false;
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
