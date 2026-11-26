package at.technikum.swen3.paperless.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.net.ConnectException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RabbitOcrJobPublisherTest {
    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final RabbitOcrJobPublisher publisher = new RabbitOcrJobPublisher(rabbitTemplate, "ocr-jobs");
    private final OcrJobMessage message =
            new OcrJobMessage(1L, "invoice.pdf", "application/pdf", 42L, Instant.now());

    @Test
    void sendsMessageToConfiguredQueue() {
        publisher.publish(message);
        verify(rabbitTemplate).convertAndSend("ocr-jobs", message);
    }

    @Test
    void wrapsBrokerErrorsInQueuePublishException() {
        doThrow(new AmqpConnectException(new ConnectException("refused")))
                .when(rabbitTemplate).convertAndSend(eq("ocr-jobs"), any(Object.class));

        assertThrows(QueuePublishException.class, () -> publisher.publish(message));
    }
}
