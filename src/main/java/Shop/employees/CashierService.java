package Shop.employees;

import Shop.commodities.Commodity;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.*;
import Shop.stores.IStoreService;
import Shop.receipts.Receipt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CashierService implements ICashierService {
    private final Cashier cashier;

    public CashierService(Cashier cashier) {
        this.cashier = cashier;
    }

    // Getters / Setters
    @Override
    public int getId() {
        return cashier.getId();
    }

    @Override
    public String getName() {
        return cashier.getName();
    }

    @Override
    public BigDecimal getSalary() {
        return cashier.getSalary();
    }

    @Override
    public IStoreService getStore() {
        return cashier.getStore();
    }
    // -----------------

    @Override
    public Receipt sellCommodities(Cashier cashier, List<CustomCommoditiesDataType> cartCommodities, BigDecimal money)
            throws EmptyCartException, CommodityNotFoundException, InsufficientQuantityException,
            InsufficientFundsException, CashierNotHiredException {

        IStoreService store = cashier.getStore();

        if (!store.getCashiers().contains(cashier)) {
            throw new CashierNotHiredException(cashier.getId());
        }

        if (cartCommodities.isEmpty()) {
            throw new EmptyCartException();
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        List<CustomCommoditiesDataType> purchasedCommodities = new ArrayList<>();

        for (CustomCommoditiesDataType cartCommodity : cartCommodities) {
            Commodity available = findCommodityById(store, cartCommodity.getId());

            if (available.getQuantity() < cartCommodity.getQuantity()) {
                throw new InsufficientQuantityException(available.getName(), available.getQuantity(), cartCommodity.getQuantity());
            }

            BigDecimal itemTotal = available.getPrice().multiply(
                    new BigDecimal(cartCommodity.getQuantity()));
            totalCost = totalCost.add(itemTotal);

            updateAvailableCommodity(available, cartCommodity.getQuantity());

            CustomCommoditiesDataType purchased = new CustomCommoditiesDataType(available.getId(), available.getName(),
                    cartCommodity.getQuantity(), available.getPrice());
            purchasedCommodities.add(purchased);

            updateSoldCommodities(store, purchased);
        }

        if (money.compareTo(totalCost) < 0) {
            throw new InsufficientFundsException(totalCost, money);
        }

        BigDecimal change = money.subtract(totalCost);
        store.setRevenue(store.getRevenue().add(totalCost));

        Receipt receipt = generateReceipt(store, cashier, purchasedCommodities, totalCost, change);
        store.getReceipts().add(receipt);
        return receipt;
    }

    private Commodity findCommodityById(IStoreService store, int id) throws CommodityNotFoundException {
        for (ICommodity available : store.getAvailableCommodities()) {
            if (available.getId() == id) {
                return available;
            }
        }
        throw new CommodityNotFoundException(id);
    }

    private void updateAvailableCommodity(Commodity commodity, int quantityPurchased) {
        commodity.setQuantity(commodity.getQuantity() - quantityPurchased);
    }

    private void updateSoldCommodities(IStoreService store, CustomCommoditiesDataType purchased) {
        for (CustomCommoditiesDataType sold : store.getSoldCommodities()) {
            if (sold.getId() == purchased.getId()) {
                sold.setQuantity(sold.getQuantity() + purchased.getQuantity());
                return;
            }
        }
        store.getSoldCommodities().add(
                new CustomCommoditiesDataType(purchased.getId(), purchased.getName(), purchased.getQuantity(), purchased.getPrice()));
    }

    private Receipt generateReceipt(IStoreService store, Cashier cashier, List<CustomCommoditiesDataType> purchasedCommodities,
                                     BigDecimal totalCost, BigDecimal change) {
        int receiptId = store.getReceiptCount() + 1;
        store.setReceiptCount(receiptId);
        LocalDateTime now = LocalDateTime.now();
        return new Receipt(receiptId, cashier, now, purchasedCommodities, totalCost, change);
    }

    @Override
    public void writeReceiptToJsonFile(Receipt receipt) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String folderPath = "receipts";
        String fileName = "receipt_" + receipt.getId() + ".json";

        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try {
            mapper.writeValue(file, receipt);
            System.out.println("Receipt written to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write receipt: " + e.getMessage());
        }
    }


    // Equals and hashCode implementation
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashierService that = (CashierService) o;
        return this.cashier.getId() == that.cashier.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(cashier.getId());
    }
}