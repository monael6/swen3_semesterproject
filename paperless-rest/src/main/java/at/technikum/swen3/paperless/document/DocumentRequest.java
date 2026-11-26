package at.technikum.swen3.paperless.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

public record DocumentRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 255) String fileName,
        @NotBlank @Size(max = 100) String contentType,
        @Positive Long categoryId
) {
}