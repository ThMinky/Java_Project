package Shop.exceptions;

public class ExpiredDateRException extends RuntimeException {
    public ExpiredDateRException(String itemName, String expiryDate) {
        super("The item \"" + itemName + "\" has expired on " + expiryDate + ".");
    }
}