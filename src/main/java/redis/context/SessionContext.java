package redis.context;

import jakarta.websocket.Session;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import proto.Proto;

import java.io.Serializable;
// lop nay dinh nghia phien lam viec cua nguoi dung
@Data
@Builder
@Setter
@Getter
public class SessionContext implements Serializable {
    private String sessionID;
    private Session session;
    private Proto.User user;
    private int roomId;
    private String socketID;
    private boolean isBot;
    private long gold;
}
