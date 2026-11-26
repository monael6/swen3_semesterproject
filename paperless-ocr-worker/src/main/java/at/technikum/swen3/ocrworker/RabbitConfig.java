package at.technikum.swen3.ocrworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /** Same durable queue as in the REST server; declaring it twice is harmless. */
    @Bean
    Queue ocrQueue(@Value("${paperless.queue.ocr}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * Reads the JSON payload into the parameter type of the listener method.
     * The type header from the REST server (its own class name) is ignored,
     * so both services do not have to share Java classes.
     */
    @Bean
    MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}
