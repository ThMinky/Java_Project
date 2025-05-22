package Shop.stores;

import Shop.cashiers.ICashierService;
import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.CommodityExpiredDateRException;
import Shop.exceptions.CommodityNotFoundException;
import Shop.receipts.Receipt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public class StoreServiceImp implements IStoreService {
    public Store store;
    public StoreServiceHelper helper;

    // Constructor
    public StoreServiceImp(Store store, StoreServiceHelper helper) {
        this.store = store;
        this.helper = helper;
    }

    // Getters / Setters
    @Override
    public int getId() {
        return store.getId();
    }

    @Override
    public String getName() {
        return store.getName();
    }

    @Override
    public EnumMap<CommodityCategory, BigDecimal> getMarkupPercentages() {
        return store.getMarkupPercentages();
    }

    @Override
    public BigDecimal getRevenue() {
        return store.getRevenue();
    }

    @Override
    public void setRevenue(BigDecimal revenue) {
        store.setRevenue(revenue);
    }

    @Override
    public List<Commodity> getAvailableCommodities() {
        return store.getAvailableCommodities();
    }

    @Override
    public List<Commodity> getDeliveredCommodities() {
        return store.getDeliveredCommodities();
    }

    @Override
    public List<CustomCommoditiesDataType> getSoldCommodities() {
        return store.getSoldCommodities();
    }

    @Override
    public Set<ICashierService> getCashiers() {
        return store.getCashiers();
    }

    @Override
    public Set<Receipt> getReceipts() {
        return store.getReceipts();
    }

    @Override
    public  void setReceipts(Set<Receipt> newReceipts){
        store.setReceipts(newReceipts);
    }

    @Override
    public int getReceiptCount() {
        return store.getReceiptCount();
    }

    @Override
    public void setReceiptCount(int receiptCount) {
        store.setReceiptCount(receiptCount);
    }


    @Override
    public int getNextCommodityId() {
        return store.getNextCommodityId();
    }

    @Override
    public int getNextCashierId() {
        return store.getNextCashierId();
    }
    // -----------------

    @Override
    public void addCommodity(Commodity commodity) {
        if (commodity.getExpiryDate() != null && commodity.getExpiryDate().isBefore(LocalDate.now())) {
            throw new CommodityExpiredDateRException(commodity.getName(), commodity.getExpiryDate());
        }

        Commodity delivered = helper.findCommodityById(store.getDeliveredCommodities(), commodity.getId());
        Commodity available = helper.findCommodityById(store.getAvailableCommodities(), commodity.getId());

        if (delivered != null) {
            delivered.setQuantity(delivered.getQuantity().add(commodity.getQuantity()));
        } else {
            store.getDeliveredCommodities().add(new Commodity(commodity));
        }

        if (available != null) {
            available.setQuantity(available.getQuantity().add(commodity.getQuantity()));
        } else {
            Commodity availableCopy = new Commodity(commodity);
            store.getAvailableCommodities().add(availableCopy);
        }
    }

    @Override
    public void hireCashier(ICashierService cashier) {
        store.getCashiers().add(cashier);
    }

    @Override
    public BigDecimal applyExpiryDiscount(Commodity commodity) {
        if (commodity.getExpiryDate() == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, commodity.getExpiryDate());

        BigDecimal baseSellingPrice = commodity.getDeliveryPrice().multiply(helper.calculateMarkupMultiplier(this, commodity)).setScale(2, RoundingMode.HALF_UP);

        if (daysUntilExpiry <= store.getExpiryDiscountThresholdDays()) {
            BigDecimal discountPercentage = store.getExpiryDiscountPercentage();
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            return baseSellingPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
        }

        return baseSellingPrice;
    }

    @Override
    public Boolean checkForExpired(Commodity commodity) throws CommodityNotFoundException, CommodityExpiredDateRException {
        Commodity existingCommodity = store.getAvailableCommodities().stream().filter(c ->
                c.getId() == commodity.getId()).findFirst().orElse(null);

        if (existingCommodity == null) {
            throw new CommodityNotFoundException(commodity.getId());
        }

        if (commodity.getExpiryDate() == null) return false;

        LocalDate today = LocalDate.now();

        if (!commodity.getExpiryDate().isAfter(today)) {
            store.getAvailableCommodities().removeIf(c -> c.getId() == commodity.getId());
            throw new CommodityExpiredDateRException(commodity.getName(), commodity.getExpiryDate());
        }

        return false;
    }

    @Override
    public BigDecimal calculateTotalDeliveryCost() {
        return store.getDeliveredCommodities().stream().map(commodity ->
                commodity.getDeliveryPrice().multiply(commodity.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateMonthlySalaries() {
        return store.getCashiers().stream().map(ICashierService::getSalary).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateRevenue() {
        return store.getSoldCommodities().stream().map(commodity ->
                commodity.getPrice().multiply(commodity.getQuantity())).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculatePureRevenue() {
        BigDecimal revenue = calculateRevenue();
        BigDecimal salaries = calculateMonthlySalaries();
        BigDecimal deliveryCosts = calculateTotalDeliveryCost();

        return revenue.subtract(salaries).subtract(deliveryCosts).setScale(2, RoundingMode.HALF_UP);
    }
}