package hello;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {


    @MessageMapping("/hello") // /app/hello
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + message.getName() + "!");
    }
    
    @MessageMapping("/helloMyRedis") // /app/helloMyRedis
    @SendToUser("/queue/test_generations")
    public Greeting helloMyRedis(HelloMessage message) throws Exception {
        return new Greeting("Hello Redis User, " + message.getName() + "!");
    }
    
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable t) {
        return t.getMessage();
    }
}
