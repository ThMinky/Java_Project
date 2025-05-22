package Shop.exceptions;

import java.time.LocalDate;

public class CommodityExpiredDateRException extends RuntimeException {
    public CommodityExpiredDateRException(String itemName, LocalDate expiryDate) {
        super("The item \"" + itemName + "\" has expired on " + expiryDate + ".");
    }
}