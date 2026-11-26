package at.technikum.swen3.ocrworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consumes OCR jobs from RabbitMQ and hands them to the {@link OcrProcessor}. */
@Component
public class OcrJobListener {
    private static final Logger log = LoggerFactory.getLogger(OcrJobListener.class);
    private final OcrProcessor processor;

    public OcrJobListener(OcrProcessor processor) { this.processor = processor; }

    @RabbitListener(queues = "${paperless.queue.ocr}")
    public void onMessage(OcrJobMessage job) {
        if (job == null || job.documentId() == null) {
            log.warn("Rejected invalid OCR job: {}", job);
            throw new InvalidOcrJobException("OCR job without document id");
        }
        processor.process(job);
    }
}
