package com.eldarian.activemq_replier;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableJms
public class Replier {

    private static final Logger logger = LoggerFactory.getLogger(Replier.class);

    @Autowired
    @Qualifier("replierTemplate")
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "RequestQueue")
    @Qualifier("replierListenerFactory")
    public void reply(Message message) throws JMSException {
        if (message instanceof TextMessage textMessage) {
            logger.info("Received request: " + textMessage.getText());
            Destination replyDestination = message.getJMSReplyTo();
            if (replyDestination != null) {
                jmsTemplate.send(replyDestination, session -> {
                    TextMessage replyMessage = session.createTextMessage("Hey, I'm replying to your message: " + textMessage.getText());
                    replyMessage.setJMSCorrelationID(message.getJMSCorrelationID());
                    return replyMessage;
                });
            }
        }
    }

}
