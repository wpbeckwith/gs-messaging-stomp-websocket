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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Set;

/**
 * Created by wbeckwith.
 */
@RestController
public class HelloController {
    
    @Autowired
    private RedisMessagePublisher messagePublisher;
    
    private SimpMessagingTemplate messagingTemplate;
    private Jedis jedis;
    
    public HelloController(SimpMessagingTemplate messagingTemplate, Jedis jedis) {
        this.messagingTemplate = messagingTemplate;
        this.jedis = jedis;
    }
    
    @PostMapping(value = "/hello2")
    public String hello2(@RequestBody HelloMessage message) throws Exception {
        Set<String> result = jedis.keys("*");
        for (String key: result) {
            System.out.println("Key: " + key);
        }
        System.out.println(message.getName() + " received.");
        Greeting g = new Greeting(message.getName());
        messagingTemplate.convertAndSend("/topic/greetings", g);
        return new Date().toString();
    }
    
    @PostMapping(value = "/helloRedis")
    public String helloRedis(@RequestBody HelloMessage message) throws Exception {
        
        System.out.println(message.getName() + " received.");
        messagePublisher.publish(message);
        return new Date().toString();
    }
}

