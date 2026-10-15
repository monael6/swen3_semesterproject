package at.technikum.swen3.paperless.document;

import java.time.Instant;

public record DocumentResponse(Long id, String title, String fileName, String contentType,
                               Instant createdAt, Long categoryId, String categoryName) {
}
