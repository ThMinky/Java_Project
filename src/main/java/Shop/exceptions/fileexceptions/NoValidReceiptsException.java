package Shop.exceptions.fileexceptions;

public class NoValidReceiptsException extends Exception {
    public NoValidReceiptsException() {
        super("No valid receipts were loaded from files.");
    }
}
