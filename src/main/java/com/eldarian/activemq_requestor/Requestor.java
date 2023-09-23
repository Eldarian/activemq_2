package com.eldarian.activemq_requestor;

import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Requestor {

    private static final Logger logger = LoggerFactory.getLogger(Requestor.class);

    @Autowired
    @Qualifier("requestorTemplate")
    private JmsTemplate jmsTemplate;

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) throws JMSException {
        String replyString = "error";
        TemporaryQueue temporaryQueue = jmsTemplate.execute(Session::createTemporaryQueue);
        if (temporaryQueue != null) {
            Message reply = jmsTemplate.sendAndReceive("RequestQueue", session -> session.createTextMessage(message));

            if (reply instanceof TextMessage replyTextMessage) {
                logger.info("Received reply: " + replyTextMessage.getText());
                replyString = replyTextMessage.getText();
            }
        } else {
            logger.info("no queue");
        }
        return replyString;
    }
}
