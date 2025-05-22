package Shop.stores;

import Shop.cashiers.ICashierService;
import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.CommodityNotFoundException;
import Shop.receipts.Receipt;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public interface IStoreService {

    // Getters / Setters
    int getId();

    String getName();

    EnumMap<CommodityCategory, BigDecimal> getMarkupPercentages();

    BigDecimal getRevenue();

    void setRevenue(BigDecimal revenue);

    List<Commodity> getAvailableCommodities();

    List<Commodity> getDeliveredCommodities();

    List<CustomCommoditiesDataType> getSoldCommodities();

    Set<ICashierService> getCashiers();

    Set<Receipt> getReceipts();

    void setReceipts(Set<Receipt> newReceipts);

    int getReceiptCount();

    void setReceiptCount(int receiptCount);


    int getNextCommodityId();

    int getNextCashierId();
    // -----------------

    void addCommodity(Commodity commodity);

    void hireCashier(ICashierService cashier);

    BigDecimal applyExpiryDiscount(Commodity commodity);

    Boolean checkForExpired(Commodity commodity) throws CommodityNotFoundException;

    BigDecimal calculateTotalDeliveryCost();

    BigDecimal calculateMonthlySalaries();

    BigDecimal calculateRevenue();

    BigDecimal calculatePureRevenue();
}