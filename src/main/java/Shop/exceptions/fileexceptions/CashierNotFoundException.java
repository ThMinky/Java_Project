package Shop.exceptions.fileexceptions;

public class CashierNotFoundException extends Exception {
    public CashierNotFoundException(int cashierId, int storeId) {
        super("Cashier with ID " + cashierId + " not found in store ID " + storeId + ".");
    }
}
