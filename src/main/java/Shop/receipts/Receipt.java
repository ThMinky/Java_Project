package Shop.receipts;

import Shop.commodities.CustomCommoditiesDataType;
import Shop.employees.ICashierService;
import Shop.stores.IStoreService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"id", "storeName", "storeId", "cashierName", "cashierId", "issuedDateTime", "purchasedCommodities", "totalCost", "change"})
public class Receipt {
    private int id;

    @JsonIgnore
    private ICashierService cashier;

    @JsonIgnore
    private IStoreService store;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime issuedDateTime;

    private List<CustomCommoditiesDataType> purchasedCommodities;

    private BigDecimal totalCost;
    private BigDecimal change;

    public Receipt(int id, IStoreService store, ICashierService cashier, LocalDateTime issuedDateTime, List<CustomCommoditiesDataType> soldCommodities,
                   BigDecimal totalCost, BigDecimal change) {
        this.id = id;
        this.store = store;
        this.cashier = cashier;
        this.issuedDateTime = issuedDateTime;

        this.purchasedCommodities = soldCommodities;

        this.totalCost = totalCost;
        this.change = change;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ICashierService getCashier() {
        return cashier;
    }

    public void setCashier(ICashierService cashier) {
        this.cashier = cashier;
    }

    public LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

    public List<CustomCommoditiesDataType> getPurchasedCommodities() {
        return purchasedCommodities;
    }

    public void setPurchasedCommodities(List<CustomCommoditiesDataType> purchasedCommodities) {
        this.purchasedCommodities = purchasedCommodities;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getChange() {
        return change;
    }

    public void setChange(BigDecimal change) {
        this.change = change;
    }
    // -----------------

    // Getters for JSON
    public int getStoreId() {
        return store.getId();
    }

    public String getStoreName() {
        return store.getName();
    }

    public int getCashierId() {
        return cashier.getId();
    }

    public String getCashierName() {
        return cashier.getName();
    }
}