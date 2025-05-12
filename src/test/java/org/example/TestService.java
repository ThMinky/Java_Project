package org.example;

import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.employees.Cashier;
import Shop.employees.CashierService;
import Shop.employees.ICashierService;
import Shop.exceptions.*;
import Shop.exceptions.fileexceptions.*;
import Shop.helpers.ReceiptFileManager;
import Shop.helpers.ReceiptPrinter;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;
import Shop.stores.Store;
import Shop.stores.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestService {
    private static int storeIdCounter = 0;

    Set<IStoreService> stores;

    Store storeData;
    IStoreService store;

    Cashier cashierData;
    ICashierService cashier;

    Commodity commodity;

    List<CustomCommoditiesDataType> cart;

    @BeforeEach
    public void setUp() {
        stores = new HashSet<>();

        storeData = new Store(storeIdCounter + 1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10), 3);
        store = new StoreService(storeData);
        stores.add(store);

        cashierData = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        cashier = new CashierService(cashierData);
        store.hireCashier(cashier);

        commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        cart = new ArrayList<>();
        cart.add(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(), BigDecimal.valueOf(1), commodity.getSellingPrice()));
    }

    @Test
    public void hiringCashier() {
        Cashier newCashierData = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1500), store);
        ICashierService newCashier = new CashierService(newCashierData);

        store.hireCashier(newCashier);

        assertTrue(store.getCashiers().contains(newCashier));
    }

    @Test
    public void addingCommodityToStore() {
        Commodity newCommodity = new Commodity(store.getNextCommodityId(), "Milk", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(newCommodity);

        assertEquals(2, store.getAvailableCommodities().size());
        assertTrue(store.getAvailableCommodities().stream().anyMatch(c -> c.getId() == commodity.getId()));

        assertEquals(2, store.getDeliveredCommodities().size());
        assertTrue(store.getDeliveredCommodities().stream().anyMatch(c -> c.getId() == commodity.getId()));
    }

    @Test
    public void addingAlreadyExistingCommodityToStore() {
        store.addCommodity(commodity);

        assertEquals(BigDecimal.valueOf(20), store.getAvailableCommodities().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(20), store.getDeliveredCommodities().get(0).getQuantity());
    }


    @Test
    public void checkAndSetExpiryStatus() {
        Commodity expiringToday = new Commodity(3, "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(1), LocalDate.now());
        store.addCommodity(expiringToday);

        try {
            boolean isExpired = store.checkForExpired(expiringToday);

            assertTrue(isExpired);
        } catch (CommodityNotFoundException e) {
            fail("Commodity was not found in the store: " + e.getMessage());
        }
    }

    @Test
    public void applyExpiryDiscount() {
        Commodity discountableCommodity = new Commodity(2, "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(1), LocalDate.now().plusDays(1));
        store.addCommodity(discountableCommodity);

        store.applyExpiryDiscount(discountableCommodity);

        BigDecimal expectedPrice = BigDecimal.valueOf(9).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPrice, discountableCommodity.getSellingPrice());
    }

    @Test
    public void sellingCommodities() {
        Commodity newCommodity = new Commodity(store.getNextCommodityId(), "Soap", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(2), BigDecimal.valueOf(4), BigDecimal.valueOf(5), LocalDate.now().plusDays(100));
        store.addCommodity(newCommodity);

        cart.add(new CustomCommoditiesDataType(newCommodity.getId(), newCommodity.getName(), BigDecimal.valueOf(2), newCommodity.getSellingPrice()));

        try {
            cashier.sellCommodities(cart, BigDecimal.valueOf(20));
        } catch (CashierNotHiredException e) {
            fail("Cashier Not Hired Exception! " + e.getMessage());
        } catch (EmptyCartException e) {
            fail("Empty Cart Exception! " + e.getMessage());
        } catch (CommodityNotFoundException e) {
            fail("Commodity Not Found Exception! " + e.getMessage());
        } catch (InsufficientQuantityException e) {
            fail("Insufficient Quantity Exception! " + e.getMessage());
        } catch (InsufficientFundsException e) {
            fail("Insufficient Funds Exception! " + e.getMessage());
        }

        assertEquals(BigDecimal.valueOf(9), store.getAvailableCommodities().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(3), store.getAvailableCommodities().get(1).getQuantity());
    }

    @Test
    public void printAndSaveReceipt() {
        Receipt receipt = null;

        try {
            receipt = cashier.sellCommodities(cart, BigDecimal.valueOf(15));
        } catch (CashierNotHiredException e) {
            fail("Cashier Not Hired Exception! " + e.getMessage());
        } catch (EmptyCartException e) {
            fail("Empty Cart Exception! " + e.getMessage());
        } catch (CommodityNotFoundException e) {
            fail("Commodity Not Found Exception! " + e.getMessage());
        } catch (InsufficientQuantityException e) {
            fail("Insufficient Quantity Exception! " + e.getMessage());
        } catch (InsufficientFundsException e) {
            fail("Insufficient Funds Exception! " + e.getMessage());
        }

        if (receipt != null) {
            ReceiptFileManager.writeToFile(receipt);
            ReceiptPrinter.printReceipt(receipt);
        }
    }

    @Test
    public void readAndPrintReceipt() {
        assertEquals(0, store.getReceipts().size());

        try {
            Set<Receipt> receipts = ReceiptFileManager.readReceiptsFromFiles(stores);

            for (Receipt receipt : receipts) {
                if (receipt.getStoreId() == store.getId()) {
                    store.getReceipts().add(receipt);
                    ReceiptPrinter.printReceipt(receipt);
                }
            }

            assertEquals(1, store.getReceipts().size());

        } catch (ReceiptsDirectoryNotFoundException e) {
            System.err.println("Error: Receipts directory not found. " + e.getMessage());
        } catch (NoReceiptFilesFoundException e) {
            System.err.println("Error: No receipt files found. " + e.getMessage());
        } catch (StoreNotFoundException e) {
            System.err.println("Error: Store not found - ID: " + e.getMessage());
        } catch (CashierNotFoundException e) {
            System.err.println("Error: Cashier not found - ID: " + e.getMessage());
        } catch (ReceiptParseException e) {
            System.err.println("Error: Failed to parse receipt file: " + e.getMessage());
        } catch (NoValidReceiptsException e) {
            System.err.println("Error: No valid receipts found. " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Test
    public void deliveredCommoditiesRemainUnchangedAfterSelling() {
        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(BigDecimal.valueOf(10), store.getDeliveredCommodities().get(0).getQuantity());
    }


    @Test
    public void storeRevenueUpdate() {
        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(BigDecimal.valueOf(2.20).setScale(2, RoundingMode.HALF_UP),
                store.getRevenue().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateMonthlySalaries() {
        Cashier newCashierData = new Cashier("BobTheBuilder", store.getNextCashierId(), BigDecimal.valueOf(2000), store);
        ICashierService newCashier = new CashierService(newCashierData);

        assertDoesNotThrow(() -> store.hireCashier(newCashier));

        BigDecimal expected = BigDecimal.valueOf(3800);
        BigDecimal actual = store.calculateMonthlySalaries();

        assertEquals(0, actual.compareTo(expected));
    }

    @Test
    public void calculatePureRevenue() {
        Commodity newCommodity = new Commodity(store.getNextCommodityId(), "GoldBar", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(0), BigDecimal.valueOf(2000), BigDecimal.valueOf(1), null);
        store.addCommodity(newCommodity);

        cart.add(new CustomCommoditiesDataType(newCommodity.getId(), newCommodity.getName(), BigDecimal.valueOf(1), newCommodity.getSellingPrice()));

        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20000)));

        BigDecimal expected = BigDecimal.valueOf(392.20);
        BigDecimal actual = store.calculatePureRevenue();

        assertEquals(0, actual.compareTo(expected));
    }

}