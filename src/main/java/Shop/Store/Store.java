package Shop.Store;

import Shop.Commodity.ICommodity;
import Shop.Commodity.Commodity;
import Shop.Commodity.CommodityCategory;
import Shop.Commodity.CustomCommoditiesDataType;

import Shop.Exceptions.CashierAlreadyExistsException;

import Shop.employees.ICashier;

import Shop.transactions.Receipt;

// Generic
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Store implements IStore {
    private String name;
    private BigDecimal eatableMarkupPercentage;
    private BigDecimal nonEatableMarkupPercentage;
    private BigDecimal expiryDiscountPercentage;
    private int expiryDiscountThresholdDays;

    private BigDecimal revenue;

    private List<ICommodity> availableCommodities;
    private List<ICommodity> deliveredCommodities;
    private List<CustomCommoditiesDataType> soldCommodities;

    private List<ICashier> cashiers;
    private Queue<Receipt> receipts;

    private int receiptCount;

    public Store(String name, BigDecimal eatableMarkupPercentage, BigDecimal nonEatableMarkupPercentage,
                 BigDecimal expiryDiscountPercentage, int expiryDiscountThresholdDays) {
        this.name = name;
        this.eatableMarkupPercentage = eatableMarkupPercentage;
        this.nonEatableMarkupPercentage = nonEatableMarkupPercentage;
        this.expiryDiscountPercentage = expiryDiscountPercentage;
        this.expiryDiscountThresholdDays = expiryDiscountThresholdDays;

        this.revenue = BigDecimal.ZERO;

        this.availableCommodities = new ArrayList<>();
        this.deliveredCommodities = new ArrayList<>();
        this.soldCommodities = new ArrayList<>();

        this.cashiers = new ArrayList<>();
        this.receipts = new LinkedList<>();

        this.receiptCount = 0;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public BigDecimal getEatableMarkupPercentage() {
        return eatableMarkupPercentage;
    }

    public void setEatableMarkupPercentage(BigDecimal eatableMarkupPercentage) {
        this.eatableMarkupPercentage = eatableMarkupPercentage;
    }


    public BigDecimal getNonEatableMarkupPercentage() {
        return nonEatableMarkupPercentage;
    }

    public void setNonEatableMarkupPercentage(BigDecimal nonEatableMarkupPercentage) {
        this.nonEatableMarkupPercentage = nonEatableMarkupPercentage;
    }


    public BigDecimal getExpiryDiscountPercentage() {
        return expiryDiscountPercentage;
    }

    public void setExpiryDiscountPercentage(BigDecimal expiryDiscountPercentage) {
        this.expiryDiscountPercentage = expiryDiscountPercentage;
    }


    public int getExpiryDiscountThresholdDays() {
        return expiryDiscountThresholdDays;
    }

    public void setExpiryDiscountThresholdDays(int expiryDiscountThresholdDays) {
        this.expiryDiscountThresholdDays = expiryDiscountThresholdDays;
    }


    @Override
    public BigDecimal getRevenue() {
        return revenue;
    }

    @Override
    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }


    @Override
    public List<ICommodity> getAvailableCommodities() {
        return availableCommodities;
    }

    public void setAvailableCommodities(List<ICommodity> availableCommodities) {
        this.availableCommodities = availableCommodities;
    }


    @Override
    public List<ICommodity> getDeliveredCommodities() {
        return deliveredCommodities;
    }

    public void setDeliveredCommodities(List<ICommodity> deliveredCommodities) {
        this.deliveredCommodities = deliveredCommodities;
    }


    public List<CustomCommoditiesDataType> getSoldCommodities() {
        return soldCommodities;
    }

    public void setSoldCommodities(List<CustomCommoditiesDataType> soldCommodities) {
        this.soldCommodities = soldCommodities;
    }


    @Override
    public List<ICashier> getCashiers() {
        return cashiers;
    }

    public void setCashiers(List<ICashier> cashiers) {
        this.cashiers = cashiers;
    }


    public Queue<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(Queue<Receipt> receipts) {
        this.receipts = receipts;
    }


    @Override
    public int getReceiptCount() {
        return receiptCount;
    }

    @Override
    public void setReceiptCount(int receiptCount) {
        this.receiptCount = receiptCount;
    }
    // -----------------------------------------------------------------------------------------------------------------

    // --- Functions ---
    @Override
    public void addCommodity(ICommodity commodity) {
        ICommodity delivered = findCommodityById(deliveredCommodities, commodity.getId());

        if (delivered != null) {
            delivered.setQuantity(delivered.getQuantity() + commodity.getQuantity());

            ICommodity available = findCommodityById(availableCommodities, commodity.getId());
            if (available != null) {
                available.setQuantity(available.getQuantity() + commodity.getQuantity());
            }

        } else {
            BigDecimal markupPercentage;
            if (commodity.getCategory() == CommodityCategory.EATABLE) {
                markupPercentage = eatableMarkupPercentage;
            } else {
                markupPercentage = nonEatableMarkupPercentage;
            }

            BigDecimal multiplier = BigDecimal.ONE.add(
                    markupPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
            );

            BigDecimal newPrice = commodity.getSellingPrice()
                    .multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);

            commodity.setPrice(newPrice);

            availableCommodities.add(commodity);
            deliveredCommodities.add(new Commodity(commodity));
        }
    }

    @Override
    public void applyExpiryDiscount(ICommodity commodity) {
        if (commodity.getIsExpired() || commodity.getExpiryDate() == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = commodity.getExpiryDate();

        long daysUntilExpiry = today.until(expiryDate, ChronoUnit.DAYS);

        if (daysUntilExpiry <= expiryDiscountThresholdDays) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    expiryDiscountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
            );

            BigDecimal discountedPrice = commodity.getPrice().multiply(discountMultiplier);

            commodity.setPrice(discountedPrice.setScale(2, RoundingMode.HALF_UP));
        }
    }

    @Override
    public void checkAndSetExpiryStatus(ICommodity commodity) {
        if (commodity.getIsExpired() || commodity.getExpiryDate() == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = commodity.getExpiryDate();

        if (expiryDate.isBefore(today) || expiryDate.isEqual(today)) {
            commodity.setIsExpired(true);
        } else {
            commodity.setIsExpired(false);
        }
    }


    @Override
    public int getNextCommodityId() {
        if (deliveredCommodities.isEmpty()) {
            return 1;
        }

        return deliveredCommodities.getLast().getId() + 1;
    }

    @Override
    public int getNextCashierId() {
        if (cashiers.isEmpty()) {
            return 1;
        }

        return cashiers.getLast().getId() + 1;
    }


    @Override
    public void hireCashier(ICashier cashier) throws CashierAlreadyExistsException {
        for (ICashier existingCashier : cashiers) {
            if (existingCashier.getId() == cashier.getId()) {
                throw new CashierAlreadyExistsException(cashier.getId());
            }
        }
        cashiers.add(cashier);
    }


    @Override
    public BigDecimal calculateMonthlySalaries() {
        BigDecimal total = BigDecimal.ZERO;
        for (ICashier cashier : cashiers) {
            total = total.add(cashier.getSalary());
        }

        return total;
    }

    @Override
    public BigDecimal calculatePureRevenue() {
        BigDecimal totalSalaries = calculateMonthlySalaries();
        BigDecimal totalDeliveryCost = calculateTotalDeliveryCost();

        return revenue.subtract(totalSalaries).subtract(totalDeliveryCost);
    }

    @Override
    public BigDecimal calculateEarnedRevenue() {
        BigDecimal earnedRevenue = BigDecimal.ZERO;

        for (CustomCommoditiesDataType sold : soldCommodities) {
            ICommodity delivered = findCommodityById(deliveredCommodities, sold.getId());

            if (delivered != null) {
                BigDecimal profitPerUnit = sold.getPrice().subtract(delivered.getDeliveryPrice());
                BigDecimal totalProfit = profitPerUnit.multiply(new BigDecimal(sold.getQuantity()));
                earnedRevenue = earnedRevenue.add(totalProfit);
            }
        }

        return earnedRevenue;
    }

    @Override
    public BigDecimal calculateTotalDeliveryCost() {
        BigDecimal totalCost = BigDecimal.ZERO;

        for (ICommodity delivered : deliveredCommodities) {
            BigDecimal deliveryCost = delivered.getDeliveryPrice()
                    .multiply(new BigDecimal(delivered.getQuantity()));
            totalCost = totalCost.add(deliveryCost);
        }

        return totalCost;
    }


    // Helper Functions
    private ICommodity findCommodityById(List<ICommodity> list, int id) {
        for (ICommodity item : list) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}