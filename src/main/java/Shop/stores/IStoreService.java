package Shop.stores;

import Shop.commodities.Commodity;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.cashiers.ICashierService;
import Shop.exceptions.CommodityNotFoundException;
import Shop.receipts.Receipt;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface IStoreService {

    // Getters / Setters
    int getId();

    String getName();

    BigDecimal getRevenue();

    void setRevenue(BigDecimal revenue);

    List<Commodity> getAvailableCommodities();

    List<Commodity> getDeliveredCommodities();

    List<CustomCommoditiesDataType> getSoldCommodities();

    Set<ICashierService> getCashiers();

    Set<Receipt> getReceipts();

    int getReceiptCount();

    void setReceiptCount(int receiptCount);
    // -----------------

    // Commodity management
    void addCommodity(Commodity commodity);

    Commodity findCommodityById(List<Commodity> list, int id);

    Boolean applyExpiryDiscount(Commodity commodity);

    Boolean checkForExpired(Commodity commodity) throws CommodityNotFoundException;

    // ID generation
    int getNextCommodityId();

    int getNextCashierId();

    // Cashier management
    void hireCashier(ICashierService cashier);

    // Financial calculations
    BigDecimal calculateTotalDeliveryCost();

    BigDecimal calculateMonthlySalaries();

    BigDecimal calculateRevenue();

    BigDecimal calculatePureRevenue();
}