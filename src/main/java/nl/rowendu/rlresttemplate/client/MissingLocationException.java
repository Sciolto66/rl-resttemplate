package nl.rowendu.rlresttemplate.client;

public class MissingLocationException extends RuntimeException {
    public MissingLocationException(String message) {
        super(message);
    }
}
