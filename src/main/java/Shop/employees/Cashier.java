package Shop.employees;

import Shop.Exceptions.*;

import Shop.Store.IStore;

import Shop.Commodity.ICommodity;
import Shop.Commodity.CustomCommoditiesDataType;

import Shop.transactions.IReceipt;
import Shop.transactions.Receipt;

// Generic
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Cashier implements ICashier {
    private int id;
    private String name;
    private BigDecimal salary;
    private IStore store;

    public Cashier(String name, int id, BigDecimal salary, IStore store) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.store = store;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }


    public IStore getStore() {
        return store;
    }

    public void setStore(IStore store) {
        this.store = store;
    }
    // -----------------------------------------------------------------------------------------------------------------

    // --- Functions ---
    // ------------------------------
    @Override
    public IReceipt sellCommodities(List<CustomCommoditiesDataType> cartCommodities, BigDecimal money)
            throws EmptyCartException, CommodityNotFoundException, InsufficientQuantityException,
            InsufficientFundsException, CashierNotHiredException {

        if (!store.getCashiers().contains(this)) {
            throw new CashierNotHiredException(this.id);
        }

        if (cartCommodities.isEmpty()) {
            throw new EmptyCartException();
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        List<CustomCommoditiesDataType> purchasedCommodities = new ArrayList<>();

        for (CustomCommoditiesDataType cartCommodity : cartCommodities) {
            ICommodity available = findCommodityById(cartCommodity.getId());

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

            updateSoldCommodities(purchased);
        }

        if (money.compareTo(totalCost) < 0) {
            throw new InsufficientFundsException(totalCost, money);
        }

        BigDecimal change = money.subtract(totalCost);
        store.setRevenue(store.getRevenue().add(totalCost));

        IReceipt receipt = generateReceipt(purchasedCommodities, totalCost, change);
        store.getReceipts().add(receipt);
        return receipt;
    }

    private ICommodity findCommodityById(int id) throws CommodityNotFoundException {
        for (ICommodity available : store.getAvailableCommodities()) {
            if (available.getId() == id) {
                return available;
            }
        }
        throw new CommodityNotFoundException(id);
    }

    private void updateAvailableCommodity(ICommodity commodity, int quantityPurchased) {
        commodity.setQuantity(commodity.getQuantity() - quantityPurchased);
    }

    private void updateSoldCommodities(CustomCommoditiesDataType purchased) {
        for (CustomCommoditiesDataType sold : store.getSoldCommodities()) {
            if (sold.getId() == purchased.getId()) {
                sold.setQuantity(sold.getQuantity() + purchased.getQuantity());
                return;
            }
        }
        store.getSoldCommodities().add(new CustomCommoditiesDataType(purchased.getId(), purchased.getName(), purchased.getQuantity(),
                purchased.getPrice()
        ));
    }

    public IReceipt generateReceipt(List<CustomCommoditiesDataType> purchasedCommodities, BigDecimal totalCost, BigDecimal change) {
        int receiptId = store.getReceiptCount() + 1;
        store.setReceiptCount(receiptId);

        LocalDateTime now = LocalDateTime.now();

        return new Receipt(receiptId, this, now, purchasedCommodities, totalCost, change);
    }
    // ------------------------------

    @Override
    public void writeReceiptToJsonFile(IReceipt receipt) {
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
}