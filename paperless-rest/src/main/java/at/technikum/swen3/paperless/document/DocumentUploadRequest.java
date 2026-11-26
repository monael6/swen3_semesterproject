package at.technikum.swen3.paperless.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Form fields sent together with the uploaded file (multipart/form-data).
 * File name, content type and size are taken from the file itself.
 */
public record DocumentUploadRequest(
        @NotBlank @Size(max = 200) String title,
        @Positive Long categoryId
) {
}
