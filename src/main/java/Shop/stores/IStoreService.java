package Shop.stores;

import Shop.commodities.Commodity;
import Shop.employees.ICashierService;

import java.math.BigDecimal;
import java.util.List;

public interface IStoreService {

    // Commodity management
    void addCommodity(Commodity commodity);

    Commodity findCommodityById(List<Commodity> list, int id);

    void applyExpiryDiscount(Commodity commodity);

    void checkForExpired(Commodity commodity);

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
