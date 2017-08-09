/*
 *
 * Copyright (c) 2017 CA Technologies, Inc.  All rights reserved.
 *
 * This software and all information contained therein is confidential and proprietary
 * and shall not be duplicated, used, disclosed or disseminated in any way except as
 * authorized by the applicable license agreement, without the express written
 * permission of CA Technologies, Inc. All authorized reproductions must be marked
 * with this language.
 *
 * EXCEPT AS SET FORTH IN THE APPLICABLE LICENSE AGREEMENT, TO THE EXTENT
 * PERMITTED BY APPLICABLE LAW OR AS AGREED BY CA TECHNOLOGIES, INC. IN ITS
 * APPLICABLE LICENSE AGREEMENT, CA TECHNOLOGIES, INC. PROVIDES THIS SOFTWARE
 * "AS IS" WITHOUT WARRANTY OF ANY KIND, INCLUDING WITHOUT LIMITATION, ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NONINFRINGEMENT. IN NO EVENT WILL CA TECHNOLOGIES, INC.  BE LIABLE TO
 * THE END USER OR ANY THIRD PARTY FOR ANY LOSS OR DAMAGE, DIRECT OR
 * INDIRECT, FROM THE USE OF THIS SOFTWARE, INCLUDING WITHOUT LIMITATION,
 * LOST PROFITS, LOST INVESTMENT, BUSINESS INTERRUPTION, GOODWILL, OR LOST
 * DATA, EVEN IF CA TECHNOLOGIES, INC. IS EXPRESSLY ADVISED IN ADVANCE OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 */

package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wbeckwith.
 */
@Component
public class WebSocketSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketSubscribeListener.class);
    
    private final static String SALT = "DGE$5SGr@3VsHYUMas2323E4d57vfBfFSTRU@!DSH(*%FDSdfg13sgfsg";
    
    @Autowired
    RedisMessageListenerContainer redisMessageListenerContainer;
    
    @Autowired
    MessageListenerAdapter messageListenerAdapter;
    
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        MessageHeaders messageHeaders = sha.getMessageHeaders();
        for (String key : messageHeaders.keySet()) {
            Object value = messageHeaders.get(key);
            System.out.format("Header: %s = %s%n", key, value);
        }
    
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) sha.getMessageHeaders().get("simpUser");
        if (token != null) {
            User user = (User) token.getPrincipal();
            String userDestination = WebSocketConfig.getUserTestGenerationsQueue(user);
            String eventDestination = sha.getDestination();
            
            if (eventDestination.equals(userDestination)) {
                String routingKey = getRoutingKey(user.getUsername());
                ChannelTopic channelTopic = new ChannelTopic(routingKey);
                redisMessageListenerContainer.addMessageListener(messageListenerAdapter, channelTopic);
                log.info("Websocket subscribe event; sessionId={}; username={}; routingKey={}", sha.getSessionId(),
                    user.getUsername(), routingKey);
            }
        }
    }
    
    //Takes a key prefix, and converts it to md5 hashed string.
    public static String getRoutingKey(String keyPrefix) {
        if (keyPrefix == null) {
            throw new IllegalArgumentException("keyPrefix parameter is null!");
        }
        String message = keyPrefix;
        String md5 = "";
        
        message = message + SALT;//adding a salt to the string before it gets hashed.
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");//Create MessageDigest object for MD5
            digest.update(message.getBytes(), 0, message.length());//Update input string in message digest
            md5 = new BigInteger(1, digest.digest()).toString(16);//Converts message digest value in base 16 (hex)
            
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }
}
