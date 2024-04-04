package redis.cache;

import dao.bean.UserBean;
import proto.Proto;
import redis.RedisClusterHelper;
import redis.context.SessionContext;
import utils.CompressUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
// Luu tru thong tin cua tat ca nguoi dung dang online
public class SessionCache extends RedisClusterHelper implements ICache<SessionContext>  {
    private static final String GOLD_KEY = SessionContext.class + ":gold:";
    private static final String USER_KEY = SessionContext.class + ":user";
    private static final SessionCache install = new SessionCache();
    private static final ConcurrentHashMap<String, SessionContext> sessionContextMap = new ConcurrentHashMap<>();
    private static final HashMap<String, AtomicLong> goldMap = new HashMap<>();

    private SessionCache() {
    }
    public static SessionCache me() {
        return install;
    }

    @Override
    public boolean add(String sessionIDKey, SessionContext value) {
        sessionContextMap.put(sessionIDKey, value);
        return true;
    }
    @Override
    public boolean add(SessionContext value) {
        return this.add(value.getSessionID(), value);
    }
    @Override
    public SessionContext get(String sessionIDKey) {
        //getSessionContextFormSessionID
        long begin = System.currentTimeMillis();
        SessionContext sessionContext = sessionContextMap.get(sessionIDKey);
        if (sessionContext == null) {
            return null;
        }
        if (sessionContext.getUser() == null) return sessionContext;
        long gold = goldMap.containsKey(sessionIDKey) ? goldMap.get(sessionIDKey).get() : 0;
        sessionContext.setUser(sessionContext.getUser().toBuilder().setGold(gold).build());
        return sessionContext;
    }
    @Override
    public List<SessionContext> getAll() {
        return new ArrayList<>(getAllUserOnline().values());
    }
    public Map<String, SessionContext> getAllUserOnline() {
        Map<byte[], byte[]> map = getConnection().hgetAll(USER_KEY.getBytes());
        Map<String, SessionContext> result = new HashMap<>();
        map.forEach((k, v) -> {
            SessionContext sessionContext = CompressUtils.decompress(v, SessionContext.class);
            result.put(new String(k), sessionContext);
        });
        return result;
    }
    @Override
    public Set<String> getKeys() {
        return null;
    }
    @Override
    public SessionContext remove(String key) {
        if (!sessionContextMap.containsKey(key)) return null;
        SessionContext sessionContext = get(key);
        sessionContextMap.remove(key);
        return sessionContext;
    }
    @Override
    public boolean containsKey(String key) {
        return false;
    }
    @Override
    public void clear() {

    }
    @Override
    public String getKey(SessionContext value) {
        return null;
    }

    public void onLogin(Proto.User userProto, String sessionID) {
        if (userProto == null) return;
        //Them session context vao cache
        addUserOnline(userProto.getUserId(), sessionID);
    }
    public void logout(SessionContext sessionContext) {
        if (sessionContext == null || sessionContext.getUser() == null) return;
        //xoa session context khoi cache
        removeUserOnline(sessionContext.getUser().getUserId());
    }
    public void removeUserOnline(int userId) {
        this.removeUserOnline(String.valueOf(userId));
    }
    public void removeUserOnline(String userId) {
        getConnection().hdel(USER_KEY.getBytes(), userId.getBytes());
    }
    public void addUserOnline(Proto.User user, String sessionId) {
        addUserOnline(user.getUserId(), sessionId);
    }
    public void addUserOnline(int userId, String sessionId) {
        getConnection().hset(USER_KEY.getBytes(), String.valueOf(userId).getBytes(), CompressUtils.compress(sessionContextMap.get(sessionId)));
    }
    public String getSessionIdOnlineByUserID(int userId) {
        SessionContext userOnline = getUserOnline(userId);
        return userOnline == null ? null : userOnline.getSessionID();
    }
    public SessionContext getUserOnline(int userId) {
        byte[] data = getConnection().hget(USER_KEY.getBytes(), String.valueOf(userId).getBytes());
        if (data == null) return null;
        SessionContext decompress = CompressUtils.decompress(data, SessionContext.class);
        return decompress;
    }
    public Map<String, SessionContext> getSessionContextMap() {
        return sessionContextMap;
    }
}
