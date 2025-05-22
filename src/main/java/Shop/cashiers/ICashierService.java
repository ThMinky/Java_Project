package Shop.cashiers;

import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.CommodityNotFoundException;
import Shop.exceptions.InsufficientFundsException;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;

import java.math.BigDecimal;
import java.util.List;

public interface ICashierService {

    // Getters / Setters
    Cashier getCashier();

    void setCashier(Cashier cashier);

    CashierServiceHelper getHelper();

    void setHelper(CashierServiceHelper helper);

    int getId();

    String getName();

    BigDecimal getSalary();

    IStoreService getStore();
    // -----------------

    Receipt sellCommodities(List<CustomCommoditiesDataType> cartCommodities, BigDecimal money) throws CommodityNotFoundException, InsufficientFundsException;
}