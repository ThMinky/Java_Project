package Shop.helpers;

import Shop.commodities.CustomCommoditiesDataType;
import Shop.receipts.Receipt;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ReceiptPrinter {

    public static void printReceipt(Receipt receipt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("=== RECEIPT ===");
        System.out.println("Receipt ID: " + receipt.getId());
        System.out.println("Cashier: " + receipt.getCashier().getName());
        System.out.println("Date: " + receipt.getIssuedDateTime().format(formatter));
        System.out.println("----------------------------");

        for (CustomCommoditiesDataType item : receipt.getPurchasedCommodities()) {
            BigDecimal totalItemPrice = item.getPrice().multiply(item.getQuantity());
            System.out.printf("%s - x%.2f - %.2f\n", item.getName(), item.getQuantity(), totalItemPrice);
        }

        System.out.println("----------------------------");
        System.out.printf("Total: %.2f\n", receipt.getTotalCost());
        System.out.printf("Change: %.2f\n", receipt.getChange());
        System.out.println("============================");
    }
}
