package redis;

import org.json.JSONObject;
import org.json.JSONString;
import proto.Proto;
import redis.clients.jedis.Connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;

public class Jedis {
    public Jedis() {
    }
    public static Jedis instance;
    public redis.clients.jedis.Jedis jedis;
    private static String url;
    public static Jedis me() {
        if (instance == null) {
            instance = new Jedis();
            url = RedisProperties.getURL();
        }
        return instance;
    }
    public Connection getConnection () {
        this.jedis = new redis.clients.jedis.Jedis("redis://default:gXadZBs6IHJvSHuNVN7YJQBlVbNPWw6h@redis-19394.c292.ap-southeast-1-1.ec2.cloud.redislabs.com:19394");
        return this.jedis.getConnection();
    }

    public void closeConnection() {
        if(java.util.Objects.nonNull(jedis)){
            this.jedis.close();
            this.jedis = null;
        }
    }
    public void set(String key, String value) {
        jedis.set(key, value);
    }
    public String get(String key) {
        return jedis.get(key);
    }
    public boolean containsKey(String key) {
        return jedis.exists(key);
    }

    public static void main(String[] args) {
        System.out.println(RedisProperties.getURL());
        try {
            Jedis jedis = Jedis.me();
            System.out.println(jedis.getUrl());
            jedis.getConnection();
            jedis.set("name", "trong2");
            System.out.println("name = " + jedis.get("name"));
            jedis.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class RedisProperties {
        private static final Properties prop = new Properties();

        static {
            try {
                File file = new File("/redis.properties");
                if (file.exists()) {
                    prop.load(new FileInputStream(file));
                } else {
                    prop.load(RedisClusterHelper.RedisProperties.class.getClassLoader().getResourceAsStream("redis.properties"));
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
        public static String getURL() {
            return prop.get("redis.url").toString();
        }
    }
    public String getUrl() {
        return url;
    }
}
