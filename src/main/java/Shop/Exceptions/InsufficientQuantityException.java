package Shop.Exceptions;

public class InsufficientQuantityException extends Exception {
    public InsufficientQuantityException(String commodityName, int availableQuantity, int wantedQuantity) {
        super("Insufficient quantity of commodity: " + commodityName +
                ". Only " + availableQuantity + " available, " +
                (wantedQuantity - availableQuantity) + " more needed.");
    }
}