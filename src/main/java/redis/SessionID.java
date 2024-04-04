package redis;

import dao.BaseDAO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import proto.Proto;
import redis.cache.SessionCache;
import redis.context.SessionContext;
import utils.StringUtils;

@Getter
@Builder
@ToString
public class SessionID {
    static final String ownerEndPointID = String.valueOf(System.currentTimeMillis());
//    static final String ownerEndPointID = UUID.randomUUID().toString();

     String endPointID;
     String sessionID;

    public SessionID(String endPointID, String sessionID) {
        this.endPointID = endPointID;
        this.sessionID = sessionID;
    }
    public static SessionID of(String endPointID, String sessionID) {
        return SessionID.builder().endPointID(endPointID).sessionID(sessionID).build();
    }

    public static SessionID of(String sessionID) {
        return SessionID.builder().endPointID(ownerEndPointID).sessionID(sessionID).build();
    }
    //Chuyen doi thanh mot SessionID dinh danh duy nhat tu mot sessionId
    public static SessionID parseUUID(String sessionId) {
        String[] sessionIdDetail = sessionId.split(":");
        if (sessionIdDetail.length != 2) {
            return null;
        }
        return SessionID.builder().endPointID(sessionIdDetail[0]).sessionID(sessionIdDetail[1]).build();
    }
    //Lay ra sessionId
    public String getSessionId() {
        return this.endPointID + ":" + this.sessionID;
    }
    public static void main(String[] args) {
        try {
            System.out.println(SessionManage.me().getEndPointID());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
