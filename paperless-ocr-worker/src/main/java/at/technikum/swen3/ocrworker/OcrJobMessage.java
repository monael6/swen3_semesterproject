package at.technikum.swen3.ocrworker;

import java.time.Instant;

/** Message received from the REST server after a document upload (same JSON fields). */
public record OcrJobMessage(Long documentId, String fileName, String contentType, Long fileSize,
                            Instant uploadedAt) {
}
