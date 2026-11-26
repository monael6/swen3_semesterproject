package at.technikum.swen3.paperless.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Sends OCR jobs as JSON to the RabbitMQ queue configured in "paperless.queue.ocr". */
@Component
public class RabbitOcrJobPublisher implements OcrJobPublisher {
    private static final Logger log = LoggerFactory.getLogger(RabbitOcrJobPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public RabbitOcrJobPublisher(RabbitTemplate rabbitTemplate,
                                 @Value("${paperless.queue.ocr}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    @Override
    public void publish(OcrJobMessage message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
            log.info("Sent OCR job for document id={} to queue '{}'", message.documentId(), queueName);
        } catch (AmqpException exception) {
            log.error("Could not send OCR job for document id={} to queue '{}'",
                    message.documentId(), queueName, exception);
            throw new QueuePublishException("Message queue is not available", exception);
        }
    }
}
