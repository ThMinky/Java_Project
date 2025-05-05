package Shop.transactions;

import java.io.IOException;

public interface IReceipt {

    // --- Functions ---
    void printReceipt();

    void writeReceiptToFile() throws IOException;
}