package at.technikum.swen3.paperless.messaging;

/**
 * Sends OCR jobs to the message queue.
 * The business layer only knows this interface, not RabbitMQ (loose coupling).
 */
public interface OcrJobPublisher {
    /**
     * @throws QueuePublishException if the message broker cannot be reached
     */
    void publish(OcrJobMessage message);
}
