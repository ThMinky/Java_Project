package Shop.transactions;

import Shop.employees.Cashier;

import Shop.Commodity.CustomCommoditiesDataType;


// Generic
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Receipt implements IReceipt {
    private int id;
    private Cashier cashier;
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

    @Override
    public void writeReceiptToFile() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String folderPath = "receipts";
        String fileName = "receipt_" + id + ".txt";

        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File receiptFile = new File(folder, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(receiptFile))) {
            writer.write("=== RECEIPT ===\n");
            writer.write("Receipt ID: " + id + "\n");
            writer.write("Cashier: " + cashier.getName() + "\n");

            writer.write("Date: " + issuedDateTime.format(formatter) + "\n");
            writer.write("----------------------------\n");

            for (CustomCommoditiesDataType item : purchasedCommodities) {
                BigDecimal totalItemPrice = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
                writer.write(String.format("%s - x%d - %.2f\n", item.getName(), item.getQuantity(), totalItemPrice));
            }

            writer.write("----------------------------\n");
            writer.write(String.format("Total: %.2f\n", totalCost));
            writer.write(String.format("Change: %.2f\n", change));
            writer.write("============================\n");

            System.out.println("Receipt written to: " + receiptFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to write receipt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}