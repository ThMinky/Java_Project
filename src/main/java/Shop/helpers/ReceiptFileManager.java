package Shop.helpers;

import Shop.receipts.Receipt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

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
}