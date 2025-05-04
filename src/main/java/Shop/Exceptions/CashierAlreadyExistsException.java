package Shop.Exceptions;

public class CashierAlreadyExistsException extends Exception {
    public CashierAlreadyExistsException(int cashierId) {
        super("Cashier with ID " + cashierId + " is already working here.");
    }
}

