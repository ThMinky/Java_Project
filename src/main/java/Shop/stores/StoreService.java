package Shop.stores;

import Shop.commodities.Commodity;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.employees.ICashierService;
import Shop.exceptions.CommodityNotFoundException;
import Shop.receipts.Receipt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

public class StoreService implements IStoreService {
    private final Store store;

    public StoreService(Store store) {
        this.store = store;
    }

    // Getters / Setters
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
    public int getReceiptCount() {
        return store.getReceiptCount();
    }

    @Override
    public void setReceiptCount(int receiptCount) {
        store.setReceiptCount(receiptCount);
    }
    // -----------------

    @Override
    public void addCommodity(Commodity commodity) {
        Commodity delivered = findCommodityById(store.getDeliveredCommodities(), commodity.getId());
        Commodity available = findCommodityById(store.getAvailableCommodities(), commodity.getId());

        if (delivered != null) {
            delivered.setQuantity(delivered.getQuantity().add(commodity.getQuantity()));
        } else {
            store.getDeliveredCommodities().add(new Commodity(commodity)); // Make copy if needed
        }

        if (available != null) {
            available.setQuantity(available.getQuantity().add(commodity.getQuantity()));
        } else {
            BigDecimal markupPercentage = store.getMarkupPercentages().getOrDefault(commodity.getCategory(), BigDecimal.ZERO);

            BigDecimal multiplier = BigDecimal.ONE.add(markupPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            BigDecimal newPrice = commodity.getSellingPrice().multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

            Commodity availableCopy = new Commodity(commodity);
            availableCopy.setSellingPrice(newPrice);

            store.getAvailableCommodities().add(availableCopy);
        }
    }

    @Override
    public Commodity findCommodityById(List<Commodity> list, int id) {
        for (Commodity item : list) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Boolean applyExpiryDiscount(Commodity commodity) {
        if (commodity.getExpiryDate() == null) return false;

        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, commodity.getExpiryDate());

        if (daysUntilExpiry <= store.getExpiryDiscountThresholdDays()) {
            BigDecimal discount = commodity.getSellingPrice().multiply(store.getExpiryDiscountPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal newPrice = commodity.getSellingPrice().subtract(discount).setScale(2, RoundingMode.HALF_UP);

            commodity.setSellingPrice(newPrice);
            return true;
        }

        return false;
    }

    @Override
    public Boolean checkForExpired(Commodity commodity) throws CommodityNotFoundException {
        Commodity existingCommodity = store.getAvailableCommodities().stream().filter(c -> c.getId() == commodity.getId()).findFirst().orElse(null);

        if (existingCommodity == null) {
            throw new CommodityNotFoundException(commodity.getId());
        }

        if (commodity.getExpiryDate() == null) return false;

        LocalDate today = LocalDate.now();

        if (!commodity.getExpiryDate().isAfter(today)) {
            store.getAvailableCommodities().removeIf(c -> c.getId() == commodity.getId());
            return true;
        }

        return false;
    }


    @Override
    public int getNextCommodityId() {
        return store.getDeliveredCommodities().size() + 1;
    }

    @Override
    public int getNextCashierId() {
        return store.getCashiers().size() + 1;
    }


    @Override
    public void hireCashier(ICashierService cashier) {
        store.getCashiers().add(cashier);
    }


    @Override
    public BigDecimal calculateTotalDeliveryCost() {
        BigDecimal totalDeliveryCost = BigDecimal.ZERO;

        for (Commodity commodity : store.getDeliveredCommodities()) {
            BigDecimal commodityDeliveryCost = commodity.getDeliveryPrice().multiply(commodity.getQuantity());
            totalDeliveryCost = totalDeliveryCost.add(commodityDeliveryCost);
        }

        return totalDeliveryCost.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateMonthlySalaries() {
        BigDecimal totalSalaries = BigDecimal.ZERO;

        for (ICashierService cashier : store.getCashiers()) {
            totalSalaries = totalSalaries.add(cashier.getSalary());
        }

        return totalSalaries.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateRevenue() {
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (CustomCommoditiesDataType commodity : store.getSoldCommodities()) {
            BigDecimal itemRevenue = commodity.getPrice().multiply(commodity.getQuantity());
            totalRevenue = totalRevenue.add(itemRevenue);
        }

        return totalRevenue.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculatePureRevenue() {
        BigDecimal revenue = calculateRevenue();
        BigDecimal salaries = calculateMonthlySalaries();
        BigDecimal deliveryCosts = calculateTotalDeliveryCost();

        BigDecimal pureRevenue = revenue.subtract(salaries).subtract(deliveryCosts);
        return pureRevenue.setScale(2, RoundingMode.HALF_UP);
    }
}