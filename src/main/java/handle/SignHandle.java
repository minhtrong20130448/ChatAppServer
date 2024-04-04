package handle;

import jakarta.websocket.Session;
import proto.Proto;
import redis.Jedis;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

public class SignHandle implements HandleRequest{

    @Override
    public void onMessage(Proto.Packet packet, Session session, HashMap<String, Session> userSession) throws IOException {
        Proto.ReqSignin reqSignin = packet.getReqSignin();
        System.out.println("reqSignin.getUsername() = " + reqSignin.getUsername());
        System.out.println("reqSignin.getPassword() = " + reqSignin.getPassword());
        Jedis jedis = Jedis.me();
        System.out.println(jedis.getConnection());
        Proto.PacketWrapper packetWrapper = null;
        if(jedis.containsKey("user:" + reqSignin.getUsername())) {
            session.getBasicRemote().sendText("User already exists");
            packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResSignin(Proto.ResSignin.newBuilder().setStatus(400).build())).build();
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(packetWrapper.toByteArray()));
        }else{
            jedis.set("user:" + reqSignin.getUsername(), reqSignin.getPassword());
            packetWrapper = Proto.PacketWrapper.newBuilder().addPacket(Proto.Packet.newBuilder().setResSignin(Proto.ResSignin.newBuilder().setStatus(200).build())).build();
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(packetWrapper.toByteArray()));
        }
        jedis.closeConnection();
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
