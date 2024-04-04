package redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.api.RedissonClient;
import redis.clients.jedis.*;
import redis.clients.jedis.providers.ClusterConnectionProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static utils.CompressUtils.compress;
import static utils.CompressUtils.decompress;

public class RedisClusterHelper {
    private static JedisCluster jedisCluster;
    private static RedissonClient redissonClient;
    private static ClusterConnectionProvider provider;
    protected RedisClusterHelper() {
    }

    protected JedisCluster getConnection(){
        if(jedisCluster == null){
            var poolConfig = new GenericObjectPoolConfig<Connection>(); // cung cap cau hinh chung cho cac pool ket noi
            poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(60)); // thoi gian giua cac lan chay tinh nang loai bo ket noi
            poolConfig.setTestOnBorrow(true); // kiem tra ket noi truoc khi lay ra
            poolConfig.setTestOnReturn(true); // kiem tra ket noi truoc khi tra lai pool
            poolConfig.setMaxWait(Duration.ofMillis(2000)); // thoi gian toi da cho phep cho ket noi
            poolConfig.setMaxTotal(80); // so luong ket noi toi da
            poolConfig.setMaxIdle(35); // so luong ket noi toi da duoc giu trong pool
            //poolConfig.setMinIdle(35); // so luong ket noi toi thieu duoc giu trong pool
            Set<HostAndPort> nodes = new HashSet<>(); // Them dia chi cho cac redis node
            nodes.add(new HostAndPort(RedisProperties.getHost(), RedisProperties.getPort()));
            //nodes.add(new HostAndPort("host-node2", port));
            provider = new ClusterConnectionProvider(nodes, DefaultJedisClientConfig.builder().build(), poolConfig);
            try {
                jedisCluster = new JedisCluster(provider, 30, Duration.ofMillis(1000)); //provider cung cấp thông tin kết nối voi các node redis, 30 là số lần toi da thử kết nối lai, Duration.ofMillis(1000) là thời gian chờ toi da
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return jedisCluster;
    }
    public boolean containsKey(String key, Class<?> c) {
        try{
            return getConnection().sismember(c.getCanonicalName(), key);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    //set key
    protected <T extends Serializable> void set(String key, T v) {
        if(Objects.isNull(v)){
            throw new RuntimeException("Redis set nulll value key = " + key);
        }
        getConnection().set(determineObjKey(key, v.getClass()), compress(v));
    }
    //get key
    protected <T extends Serializable> T get(String key, Class<T> tClass) {
        try {
            return decompress(getConnection().get(determineObjKey(key, tClass)), tClass);
        } catch (Exception e) {
            getConnection().srem(tClass.getCanonicalName(), key);
            return null;
        }
    }
    //chuyen doi khoa thanh mang byte
    protected <T> byte[] determineObjKey(String key, Class<T> c) {
        System.out.println("key: "+(c.getCanonicalName() + ":" + key));
        return (c.getCanonicalName() + ":" + key).getBytes();
    }
    //delete key
    protected <T extends Serializable> T delete(String key, Class<T> tClass) {
        T t = get(key, tClass);
        if (t == null) return null;
        getConnection().del(determineObjKey(key, tClass));
        return t;
    }
    //Set
    //lưu một đối tượng vào một tập hợp set
    protected <T extends Serializable> void saveObj(String key, T v) { //chua hieu
        getConnection().sadd(v.getClass().getCanonicalName(), key);
        this.set(key, v);
    }
    //lay ra tat ca cac doi tuong trong mot tap hop (mot lop)
    protected <T extends Serializable> Map<String, T> getAllObj(final Class<T> c) {
        List<byte[]> keys = getConnection().smembers(c.getCanonicalName()).stream().map(k -> determineObjKey(k, c)).collect(Collectors.toList());
        return get(keys, c);
    }
    private <T extends Serializable> Map<String, T> get(List<byte[]> keys, Class<T> c) {
        final ClusterPipeline pipeline = createOneTimePipeline();
        List<List<Object>> collect = keys.stream().map(k -> List.of(k, pipeline.get(k))).collect(Collectors.toList());
        pipeline.sync();
        pipeline.close();
        if (collect.size() == 0) return new HashMap<>();
        Stream<List<Object>> stream = collect.stream().filter(Objects::nonNull)
                .filter(objects -> nonNull(objects.get(0)) && nonNull(objects.get(1)));

        return stream.collect(Collectors.toMap(
                keyAndValue -> {
                    try {
                        return new String((byte[]) keyAndValue.get(0));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                keyAndValue -> {
                    try {
                        return decompress(((Response<byte[]>) keyAndValue.get(1)).get(), c);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
    }
    private ClusterPipeline createOneTimePipeline() {
        return new ClusterPipeline(provider);
    }
    //class RedisProperties dung de lay thong tin tu file redis.properties

    public static class RedisProperties {
        private static final Properties prop = new Properties();

        static {
            try {
                File file = new File("/redis.properties");
                if (file.exists()) {
                    prop.load(new FileInputStream(file));
                } else {
                    prop.load(RedisProperties.class.getClassLoader().getResourceAsStream("redis.properties"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public static String getHost() {
            return prop.get("redis.host").toString();
        }
        public static int getPort() {
            return Integer.parseInt(prop.get("redis.port").toString());
        }
        public static String getPassword() {
            return prop.get("redis.password").toString();
        }
    }
    public static void main(String[] args) {
            try {
                RedisClusterHelper redisClusterHelper = new RedisClusterHelper();
//                redisClusterHelper.getConnection();
//                redisClusterHelper.set("name", "trong2");
                System.out.println("name = " + redisClusterHelper.determineObjKey("name", String.class));
            }catch ( Exception e){
                e.printStackTrace();
            }
    }
}
