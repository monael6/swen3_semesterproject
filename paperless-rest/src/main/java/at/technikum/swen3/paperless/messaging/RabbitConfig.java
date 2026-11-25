package at.technikum.swen3.paperless.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /** Durable queue: messages survive a RabbitMQ restart. Declared on startup if it does not exist. */
    @Bean
    Queue ocrQueue(@Value("${paperless.queue.ocr}") String queueName) {
        return new Queue(queueName, true);
    }

    /** Send messages as JSON (using Spring's ObjectMapper, e.g. dates as ISO strings). */
    @Bean
    MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
