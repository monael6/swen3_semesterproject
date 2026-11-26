package at.technikum.swen3.ocrworker;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;

/** The message cannot be processed (e.g. no document id). It is rejected and not requeued. */
public class InvalidOcrJobException extends AmqpRejectAndDontRequeueException {
    public InvalidOcrJobException(String message) { super(message); }
}
