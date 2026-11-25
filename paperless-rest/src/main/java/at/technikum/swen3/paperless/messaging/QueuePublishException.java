package at.technikum.swen3.paperless.messaging;

/** Messaging-layer exception: a message could not be sent to RabbitMQ. */
public class QueuePublishException extends RuntimeException {
    public QueuePublishException(String message, Throwable cause) { super(message, cause); }
}
