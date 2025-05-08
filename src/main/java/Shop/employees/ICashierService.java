package Shop.employees;

import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.*;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;

import java.math.BigDecimal;
import java.util.List;

public interface ICashierService {

    // Getters / Setters
    int getId();

    String getName();

    BigDecimal getSalary();

    IStoreService getStore();
    // -----------------

    Receipt sellCommodities(List<CustomCommoditiesDataType> cartCommodities, BigDecimal money)
            throws EmptyCartException, CommodityNotFoundException, InsufficientQuantityException,
            InsufficientFundsException, CashierNotHiredException;
}