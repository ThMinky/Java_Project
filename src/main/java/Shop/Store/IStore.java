package Shop.Store;

import Shop.Commodity.ICommodity;
import Shop.Commodity.CustomCommoditiesDataType;

import Shop.employees.ICashier;
import Shop.Exceptions.CashierAlreadyExistsException;

// Generic
import java.math.BigDecimal;
import java.util.List;

public interface IStore {

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    public BigDecimal getRevenue();

    public void setRevenue(BigDecimal revenue);

    List<ICommodity> getAvailableCommodities();

    List<ICommodity> getDeliveredCommodities();

    List<CustomCommoditiesDataType> getSoldCommodities();

    List<ICashier> getCashiers();

    int getReceiptCount();

    void setReceiptCount(int receiptCount);
    // -----------------------------------------------------------------------------------------------------------------

    // --- Functions ---
    void addCommodity(ICommodity commodity);
    void applyExpiryDiscount(ICommodity commodity);
    void checkAndSetExpiryStatus(ICommodity commodity);

    int getNextCommodityId();
    int getNextCashierId();

    void hireCashier(ICashier cashier) throws CashierAlreadyExistsException;

    BigDecimal calculateMonthlySalaries();
    BigDecimal calculateTotalDeliveryCost();
    BigDecimal calculateEarnedRevenue();
    BigDecimal calculatePureRevenue();
}