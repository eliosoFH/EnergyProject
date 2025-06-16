package org.example.communityenergyuser.message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class Listener {

    private final RabbitTemplate rabbit;

    public Listener(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }
    /**
    @RabbitListener(queues = "com_energy_user")
    public void readFromEchoIn(String message){
        System.out.println(message);
    }
    */
}
