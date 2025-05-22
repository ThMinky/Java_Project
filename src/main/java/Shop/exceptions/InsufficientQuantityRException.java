package Shop.exceptions;

import java.math.BigDecimal;

public class InsufficientQuantityRException extends RuntimeException {
    public InsufficientQuantityRException(String commodityName, BigDecimal availableQuantity, BigDecimal wantedQuantity) {
        super("Insufficient quantity of commodity: " + commodityName +
                ". Only " + availableQuantity + " available, " +
                wantedQuantity.subtract(availableQuantity) + " more needed.");
    }
}