package Shop.employees;

import Shop.Exceptions.*;

import Shop.Commodity.CustomCommoditiesDataType;

import Shop.transactions.IReceipt;

// Generic
import java.math.BigDecimal;
import java.util.List;

public interface ICashier {

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    int getId();

    BigDecimal getSalary();
    // -----------------------------------------------------------------------------------------------------------------

    // --- Functions ---
    IReceipt sellCommodities(List<CustomCommoditiesDataType> requestedCommodities, BigDecimal money)
            throws EmptyCartException, CommodityNotFoundException, InsufficientQuantityException,
            InsufficientFundsException, CashierNotHiredException;
}
