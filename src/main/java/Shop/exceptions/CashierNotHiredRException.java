package Shop.exceptions;

public class CashierNotHiredRException extends RuntimeException {
    public CashierNotHiredRException(int cashierId) {
        super("Cashier with ID " + cashierId + " is not hired at this store.");
    }
}
