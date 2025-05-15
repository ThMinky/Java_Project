package Shop.cashiers;

import Shop.commodities.Commodity;
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

    // These are sellCommodities helpers but need to be in the interface so I can mock them.
    void validateCashier(IStoreService store, Cashier cashier) throws CashierNotHiredException;

    void validateCart(List<CustomCommoditiesDataType> cartCommodities) throws EmptyCartException;

    Commodity findCommodityById(IStoreService store, int id) throws CommodityNotFoundException;

    void validateStockAvailability(Commodity commodity, BigDecimal requestedQuantity) throws InsufficientQuantityException;

    BigDecimal calculateItemTotal(Commodity commodity, BigDecimal quantity);

    void updateAvailableStock(Commodity commodity, BigDecimal quantityPurchased);

    CustomCommoditiesDataType createPurchasedItem(Commodity commodity, BigDecimal quantity);

    void updateSoldCommodities(IStoreService store, CustomCommoditiesDataType purchased);

    void validateFunds(BigDecimal money, BigDecimal totalCost) throws InsufficientFundsException;

    Receipt generateReceipt(IStoreService store, ICashierService cashier, List<CustomCommoditiesDataType> items, BigDecimal totalCost, BigDecimal change);
    // =================================================================================================================
}