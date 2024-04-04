package redis.cache;

import proto.Proto;
import redis.RedisClusterHelper;
import utils.CompressUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageCache extends RedisClusterHelper implements ICache<String> {
    private static final String MESSAGE_KEY = MessageCache.class + ":message";
    private static final MessageCache install = new MessageCache();

    private MessageCache() {
    }

    public static MessageCache me() {
        return install;
    }

    @Override
    public boolean add(String key, String value) {
        getConnection().lpush(MESSAGE_KEY, value);
        return true;
    }

    @Override
    public boolean add(String value) {
        return this.add(MESSAGE_KEY, value);
    }

    @Override
    public String get(String sessionIDKey) {
        return getConnection().lpop(MESSAGE_KEY);
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
    public void rpush(String userFrom, String userTo, Proto.Message message) {
        String key = MESSAGE_KEY + ":" + userFrom + ":" + userTo;
        getConnection().rpush(key.getBytes(), CompressUtils.compress(message));
    }
    public List<Proto.Message> lrange(String userFrom, String userTo,int start, int stop) {
        String key = MESSAGE_KEY + ":" + userFrom + ":" + userTo;
        List<byte[]> bytes = getConnection().lrange(key.getBytes(), start, stop);
        List<Proto.Message> messages = new ArrayList<>();
        for (byte[] messageBytes : bytes) {
            messages.add(CompressUtils.decompress(messageBytes, Proto.Message.class));
        }
        return messages;
    }

    public static void main(String[] args) {
        System.out.println("Start:");
        MessageCache messageCache = MessageCache.me();
        messageCache.rpush("minhtrong", "Phung", Proto.Message.newBuilder().setUserFrom("minhtrong").setUserTo("Phung").setContent("Hello Phung").build());
        messageCache.rpush("Phung", "minhtrong", Proto.Message.newBuilder().setUserFrom("minhtrong").setUserTo("Phung").setContent("Hello Phung").build());
        messageCache.rpush("Phung", "minhtrong", Proto.Message.newBuilder().setUserFrom("Phung").setUserTo("minhtrong").setContent("Hi Trong").build());
        messageCache.rpush("minhtrong", "Phung", Proto.Message.newBuilder().setUserFrom("Phung").setUserTo("minhtrong").setContent("Hi Trong").build());
        messageCache.rpush("minhtrong", "Phung", Proto.Message.newBuilder().setUserFrom("minhtrong").setUserTo("Phung").setContent("How are you").build());
        messageCache.rpush("Phung", "minhtrong", Proto.Message.newBuilder().setUserFrom("minhtrong").setUserTo("Phung").setContent("How are you").build());
        System.out.println(messageCache.lrange("minhtrong", "Phung", 0, -1));
    }
}
