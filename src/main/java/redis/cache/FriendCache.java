package redis.cache;

import redis.RedisClusterHelper;

import java.util.List;
import java.util.Set;

public class FriendCache extends RedisClusterHelper implements ICache<String>{
    private static final String FRIEND_KEY = FriendCache.class + ":friend";
    private static final FriendCache install = new FriendCache();
    private FriendCache() {
    }

    public static FriendCache me() {
        return install;
    }
    @Override
    public boolean add(String key, String value) {
        return false;
    }

    @Override
    public boolean add(String value) {
        return false;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public List<String> getAll() {
        return null;
    }

    @Override
    public Set<String> getKeys() {
        return null;
    }

    @Override
    public String remove(String key) {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getKey(String value) {
        return null;
    }

    public void sadd(String userKey, String friendKey) {
        String key = FRIEND_KEY + ":" + userKey;
        getConnection().sadd(key, friendKey);
    }
    public void sren(String userKey, String friendKey) {
        String key = FRIEND_KEY + ":" + userKey;
        getConnection().srem(key, friendKey);
    }
    public Set<String> smembers(String userKey) {
        String key = FRIEND_KEY + ":" + userKey;
        return getConnection().smembers(key);
    }

    public static void main(String[] args) {
        System.out.println("Start:");
        FriendCache.me().sadd("minhtrong", "phung");
        FriendCache.me().sadd("minhtrong", "ngan");
        System.out.println(FriendCache.me().smembers("minhtrong"));
    }
}
