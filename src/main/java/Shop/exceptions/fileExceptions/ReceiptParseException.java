package Shop.exceptions.fileExceptions;

public class ReceiptParseException extends Exception {
    public ReceiptParseException(String fileName, Throwable cause) {
        super("Failed to parse receipt file: " + fileName, cause);
    }
}
