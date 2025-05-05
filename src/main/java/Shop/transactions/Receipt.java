package Shop.transactions;

import Shop.employees.Cashier;

import Shop.Commodity.CustomCommoditiesDataType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


// Generic
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    // Write receipt in txt
//    @Override
//    public void writeReceiptToFile() {
//        String folderPath = "receipts";
//        String fileName = "receipt_" + id + ".json";
//
//        File folder = new File(folderPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//
//        File receiptFile = new File(folder, fileName);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        try {
//            mapper.writeValue(receiptFile, this);
//            System.out.println("Receipt written to: " + receiptFile.getAbsolutePath());
//        } catch (IOException e) {
//            System.err.println("Failed to write receipt: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @Override
    public void writeReceiptToFile() throws IOException {
        Files.createDirectories(Paths.get("receipts"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String, Object> jsonData = new LinkedHashMap<>();
        jsonData.put("id", this.id);

        jsonData.put("cashierId", this.cashier.getId());
        jsonData.put("cashierName", this.cashier.getName());

        jsonData.put("issuedDateTime", this.issuedDateTime);

        List<Map<String, Object>> commoditiesData = new ArrayList<>();
        for (CustomCommoditiesDataType item : this.purchasedCommodities) {
            Map<String, Object> itemData = new LinkedHashMap<>();
            itemData.put("name", item.getName());
            itemData.put("quantity", item.getQuantity());
            itemData.put("price", item.getPrice());
            commoditiesData.add(itemData);
        }
        jsonData.put("purchasedCommodities", commoditiesData);

        jsonData.put("totalCost", this.totalCost);
        jsonData.put("change", this.change);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File("receipts/receipt_" + this.id + ".json"), jsonData);
    }
}