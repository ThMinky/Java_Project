package Shop.helpers;

import Shop.commodities.CustomCommoditiesDataType;
import Shop.employees.ICashierService;
import Shop.exceptions.fileexceptions.*;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReceiptFileManager {

    private static final String FOLDER_PATH = "receipts";

    public static void writeToFile(Receipt receipt) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String fileName = "receipt_" + receipt.getId() + ".json";
        File directory = new File(FOLDER_PATH);

        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("Failed to create receipts directory.");
            return;
        }

        File file = new File(directory, fileName);
        try {
            mapper.writeValue(file, receipt);
            System.out.println("Receipt written to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write receipt: " + e.getMessage());
        }
    }

    public static Set<Receipt> readReceiptsFromFiles(Set<IStoreService> stores)
            throws ReceiptsDirectoryNotFoundException, NoReceiptFilesFoundException,
            StoreNotFoundException, CashierNotFoundException,
            ReceiptParseException, NoValidReceiptsException {

        Set<Receipt> receipts = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        File receiptDir = new File("receipts");
        if (!receiptDir.exists() || !receiptDir.isDirectory()) {
            throw new ReceiptsDirectoryNotFoundException();
        }

        File[] files = receiptDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            throw new NoReceiptFilesFoundException();
        }

        for (File file : files) {
            try {
                JsonNode node = mapper.readTree(file);

                int id = node.get("id").asInt();
                int storeId = node.get("storeId").asInt();
                int cashierId = node.get("cashierId").asInt();
                LocalDateTime issuedDateTime = LocalDateTime.parse(node.get("issuedDateTime").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                BigDecimal totalCost = new BigDecimal(node.get("totalCost").asText());
                BigDecimal change = new BigDecimal(node.get("change").asText());
                List<CustomCommoditiesDataType> commodities = parseCommodities(node.get("purchasedCommodities"));

                IStoreService store = findStoreById(stores, storeId);
                if (store == null) {
                    throw new StoreNotFoundException(storeId);
                }

                ICashierService cashier = findCashierById(store.getCashiers(), cashierId);
                if (cashier == null) {
                    throw new CashierNotFoundException(cashierId, storeId);
                }

                receipts.add(new Receipt(id, store, cashier, issuedDateTime, commodities, totalCost, change));

            } catch (IOException e) {
                throw new ReceiptParseException(file.getName(), e);
            }
        }

        if (receipts.isEmpty()) {
            throw new NoValidReceiptsException();
        }

        return receipts;
    }


    private static IStoreService findStoreById(Set<IStoreService> stores, int id) {
        return stores.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    private static ICashierService findCashierById(Set<ICashierService> cashiers, int id) {
        return cashiers.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    private static List<CustomCommoditiesDataType> parseCommodities(JsonNode arrayNode) {
        List<CustomCommoditiesDataType> list = new ArrayList<>();
        for (JsonNode item : arrayNode) {
            int commodityId = item.get("id").asInt();
            String name = item.get("name").asText();
            BigDecimal quantity = new BigDecimal(item.get("quantity").asText());
            BigDecimal price = new BigDecimal(item.get("price").asText());

            list.add(new CustomCommoditiesDataType(commodityId, name, quantity, price));
        }
        return list;
    }
}