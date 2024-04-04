package handle;

import redis.RedisClusterHelper;
import redis.cache.ICache;

import java.util.List;
import java.util.Set;

public class CharacterCache extends RedisClusterHelper implements ICache<String> {
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
}
