package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    Jedis jedis() {
        Jedis jedis = new Jedis();
        return jedis;
    }
    @Bean
    public HelloMessageListener helloMessageListener() {
        return new HelloMessageListener();
    }
    @Bean
    public MessageListenerAdapter listenerAdapter(HelloMessageListener helloMessageListener) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(helloMessageListener);
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<>(HelloMessage.class));
        return messageListenerAdapter;
    }
    
    @Bean
    public RedisTemplate<String, HelloMessage> myRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, HelloMessage> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(HelloMessage.class));
        return redisTemplate;
    }
    
    @Bean
    HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }
}
