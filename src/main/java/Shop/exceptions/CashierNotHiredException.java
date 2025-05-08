package Shop.exceptions;

public class CashierNotHiredException extends Exception {
    public CashierNotHiredException(int cashierId) {
        super("Cashier with ID " + cashierId + " is not hired at this store.");
    }
}
