package Shop.stores;

import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.employees.ICashierService;
import Shop.receipts.Receipt;

import java.math.BigDecimal;
import java.util.*;

public class Store {
    private int id;
    private String name;
    private BigDecimal eatableMarkupPercentage;
    private BigDecimal nonEatableMarkupPercentage;
    private BigDecimal expiryDiscountPercentage;
    private int expiryDiscountThresholdDays;

    private BigDecimal revenue = BigDecimal.ZERO;

    private List<Commodity> availableCommodities = new ArrayList<>();
    private List<Commodity> deliveredCommodities = new ArrayList<>();
    private List<CustomCommoditiesDataType> soldCommodities = new ArrayList<>();

    private Set<ICashierService> cashiers = new HashSet<>();
    private Set<Receipt> receipts = new HashSet<>();

    private int receiptCount;

    private Map<CommodityCategory, BigDecimal> markupPercentages = new EnumMap<>(CommodityCategory.class);

    public Store(int id, String name, BigDecimal eatableMarkupPercentage, BigDecimal nonEatableMarkupPercentage, BigDecimal expiryDiscountPercentage, int expiryDiscountThresholdDays) {
        this.id = id;
        this.name = name;
        this.eatableMarkupPercentage = eatableMarkupPercentage;
        this.nonEatableMarkupPercentage = nonEatableMarkupPercentage;
        this.expiryDiscountPercentage = expiryDiscountPercentage;
        this.expiryDiscountThresholdDays = expiryDiscountThresholdDays;

        this.markupPercentages = new EnumMap<>(CommodityCategory.class);
        this.markupPercentages.put(CommodityCategory.EATABLE, eatableMarkupPercentage);
        this.markupPercentages.put(CommodityCategory.NONEATABLE, nonEatableMarkupPercentage);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public List<Commodity> getAvailableCommodities() {
        return availableCommodities;
    }

    public void setAvailableCommodities(List<Commodity> availableCommodities) {
        this.availableCommodities = availableCommodities;
    }

    public List<Commodity> getDeliveredCommodities() {
        return deliveredCommodities;
    }

    public void setDeliveredCommodities(List<Commodity> deliveredCommodities) {
        this.deliveredCommodities = deliveredCommodities;
    }

    public List<CustomCommoditiesDataType> getSoldCommodities() {
        return soldCommodities;
    }

    public void setSoldCommodities(List<CustomCommoditiesDataType> soldCommodities) {
        this.soldCommodities = soldCommodities;
    }

    public Set<ICashierService> getCashiers() {
        return cashiers;
    }

    public void setCashiers(Set<ICashierService> cashiers) {
        this.cashiers = cashiers;
    }

    public Set<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(Set<Receipt> receipts) {
        this.receipts = receipts;
    }

    public int getReceiptCount() {
        return receiptCount;
    }

    public void setReceiptCount(int receiptCount) {
        this.receiptCount = receiptCount;
    }

    public Map<CommodityCategory, BigDecimal> getMarkupPercentages() {
        return markupPercentages;
    }

    public void setMarkupPercentages(Map<CommodityCategory, BigDecimal> markupPercentages) {
        this.markupPercentages = markupPercentages;
    }
    // -----------------
}