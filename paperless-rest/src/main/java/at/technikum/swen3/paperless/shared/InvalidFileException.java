package at.technikum.swen3.paperless.shared;

/** Thrown when an uploaded file is missing, empty or not a PDF. */
public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) { super(message); }
    public InvalidFileException(String message, Throwable cause) { super(message, cause); }
}
