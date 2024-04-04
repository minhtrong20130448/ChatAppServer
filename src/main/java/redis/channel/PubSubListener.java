package redis.channel;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.redisson.api.listener.MessageListener;

import java.io.Serializable;

public class PubSubListener implements MessageListener<PubSubListener.Message> {

    @Override
    public void onMessage(CharSequence charSequence, Message message) {

    }

    @Data
    @Builder
    @ToString
    public static class Message implements Serializable {
        private String sessionID;
        private byte[] content;
    }

}
