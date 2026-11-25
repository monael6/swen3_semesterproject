package at.technikum.swen3.paperless.shared;

import java.time.Instant;
import java.util.Map;

public record ApiError(Instant timestamp, int status, String message, Map<String, String> validationErrors) {
}
