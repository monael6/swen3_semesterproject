package at.technikum.swen3.paperless.messaging;

import java.time.Instant;

/**
 * Message sent to RabbitMQ after a document upload.
 * The OCR worker uses it to know which document it has to process.
 */
public record OcrJobMessage(Long documentId, String fileName, String contentType, Long fileSize,
                            Instant uploadedAt) {
}
