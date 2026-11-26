package at.technikum.swen3.paperless.shared;

/** Business-layer exception: the request conflicts with existing data (e.g. duplicate name). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
