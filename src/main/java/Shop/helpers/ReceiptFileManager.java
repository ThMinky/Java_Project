package Shop.helpers;

import Shop.cashiers.ICashierService;
import Shop.commodities.CustomCommoditiesDataType;
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

        File receiptDir = getReceiptDirectory("receipts");
        File[] receiptFiles = getReceiptFiles(receiptDir);

        ObjectMapper mapper = createObjectMapper();

        Set<Receipt> receipts = new HashSet<>();

        for (File file : receiptFiles) {
            receipts.add(parseReceipt(file, mapper, stores));
        }

        if (receipts.isEmpty()) {
            throw new NoValidReceiptsException();
        }

        return receipts;
    }

    private static File getReceiptDirectory(String path) throws ReceiptsDirectoryNotFoundException {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new ReceiptsDirectoryNotFoundException();
        }
        return dir;
    }

    private static File[] getReceiptFiles(File dir) throws NoReceiptFilesFoundException {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            throw new NoReceiptFilesFoundException();
        }
        return files;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    private static Receipt parseReceipt(File file, ObjectMapper mapper, Set<IStoreService> stores) throws ReceiptParseException,
            StoreNotFoundException, CashierNotFoundException {

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

            return new Receipt(id, store, cashier, issuedDateTime, commodities, totalCost, change);

        } catch (IOException e) {
            throw new ReceiptParseException(file.getName(), e);
        }
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

    private static IStoreService findStoreById(Set<IStoreService> stores, int id) {
        return stores.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    private static ICashierService findCashierById(Set<ICashierService> cashiers, int id) {
        return cashiers.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }
}