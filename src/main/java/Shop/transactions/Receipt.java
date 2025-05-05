package Shop.transactions;

import Shop.employees.Cashier;

import Shop.Commodity.CustomCommoditiesDataType;

// Generic
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "cashierName", "cashierId", "issuedDateTime", "purchasedCommodities", "totalCost", "change"})
public class Receipt implements IReceipt {
    private int id;

    @JsonIgnore
    private Cashier cashier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime issuedDateTime;

    private List<CustomCommoditiesDataType> purchasedCommodities;

    private BigDecimal totalCost;
    private BigDecimal change;

    public Receipt(int id, Cashier cashier, LocalDateTime issuedDateTime, List<CustomCommoditiesDataType> soldCommodities,
                   BigDecimal totalCost, BigDecimal change) {
        this.id = id;
        this.cashier = cashier;
        this.issuedDateTime = issuedDateTime;

        this.purchasedCommodities = soldCommodities;

        this.totalCost = totalCost;
        this.change = change;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
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
    // -----------------------------------------------------------------------------------------------------------------

    // --- Getters For JSON ---
    public int getCashierId() {
        return cashier.getId();
    }

    public String getCashierName() {
        return cashier.getName();
    }

    // --- Functions ---
    @Override
    public void printReceipt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("=== RECEIPT ===");
        System.out.println("Receipt ID: " + id);
        System.out.println("Cashier: " + cashier.getName());

        System.out.println("Date: " + issuedDateTime.format(formatter));

        System.out.println("----------------------------");

        for (CustomCommoditiesDataType item : purchasedCommodities) {
            BigDecimal totalItemPrice = item.getPrice().multiply(new BigDecimal(item.getQuantity()));

            System.out.printf("%s - x%d - %.2f\n", item.getName(), item.getQuantity(), totalItemPrice);
        }

        System.out.println("----------------------------");

        System.out.printf("Total: %.2f\n", totalCost);
        System.out.printf("Change: %.2f\n", change);
        System.out.println("============================");
    }
}