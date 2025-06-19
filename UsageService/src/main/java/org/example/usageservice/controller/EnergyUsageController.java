package org.example.usageservice.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class EnergyUsageController {

    private final RabbitTemplate rabbit;

    public EnergyUsageController(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @GetMapping("/send-producer")
    public void sendProducerMessage(@RequestParam("hour") String hour,
                                    @RequestParam("kwh") double kwh) {
        String message = "PRODUCER;" + hour + ";" + kwh;
        rabbit.convertAndSend("producer_queue", message);
    }

    @GetMapping("/send-user")
    public void sendUserMessage(@RequestParam("hour") String hour,
                                @RequestParam("kwh") double kwh) {
        String message = "USER;" + hour + ";" + kwh;
        rabbit.convertAndSend("user_queue", message);
    }
}
