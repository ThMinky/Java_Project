package Shop.exceptions;

import java.math.BigDecimal;

public class InsufficientQuantityException extends Exception {
    public InsufficientQuantityException(String commodityName, BigDecimal availableQuantity, BigDecimal wantedQuantity) {
        super("Insufficient quantity of commodity: " + commodityName +
                ". Only " + availableQuantity + " available, " +
                wantedQuantity.subtract(availableQuantity) + " more needed.");
    }
}
