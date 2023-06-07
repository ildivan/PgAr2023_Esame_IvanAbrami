package module42.error;

/**
 * Exception thrown in case of invalid input in fiscalcode and person packages.
 */
public class InvalidInputException extends RuntimeException{
    public InvalidInputException() {
        this("Input not valid.");
    }

    public InvalidInputException(String message) {
        super(message);
    }
}
