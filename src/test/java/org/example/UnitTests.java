package org.example;

import Shop.cashiers.Cashier;
import Shop.cashiers.CashierServiceHelper;
import Shop.cashiers.CashierServiceImp;
import Shop.cashiers.ICashierService;
import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.CommodityNotFoundException;
import Shop.helpers.ReceiptFileManager;
import Shop.helpers.ReceiptPrinter;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;
import Shop.stores.Store;
import Shop.stores.StoreServiceHelper;
import Shop.stores.StoreServiceImp;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    @Test
    public void hiringCashier() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Cashier newCashierData = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1500), store);
        CashierServiceHelper cashierHelper = new CashierServiceHelper();
        ICashierService newCashier = new CashierServiceImp(newCashierData, cashierHelper);

        store.hireCashier(newCashier);

        assertTrue(store.getCashiers().contains(newCashier));
    }

    @Test
    public void addingCommodityToStore() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        Commodity newCommodity = new Commodity(store.getNextCommodityId(), "Milk", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(newCommodity);

        assertEquals(2, store.getAvailableCommodities().size());
        assertTrue(store.getAvailableCommodities().stream().anyMatch(c -> c.getId() == commodity.getId()));

        assertEquals(2, store.getDeliveredCommodities().size());
        assertTrue(store.getDeliveredCommodities().stream().anyMatch(c -> c.getId() == commodity.getId()));
    }

    @Test
    public void addingAlreadyExistingCommodityToStore() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);
        store.addCommodity(commodity);

        assertEquals(BigDecimal.valueOf(20), store.getAvailableCommodities().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(20), store.getDeliveredCommodities().get(0).getQuantity());
    }

    @Test
    public void checkAndSetExpiryStatus() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Commodity expiringToday = new Commodity(3, "Apple", CommodityCategory.EATABLE, BigDecimal.valueOf(1),
                BigDecimal.valueOf(1), LocalDate.now());
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
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Commodity discountableCommodity = new Commodity(2, "Apple", CommodityCategory.EATABLE, BigDecimal.valueOf(10),
                BigDecimal.valueOf(1), LocalDate.now().plusDays(1));
        store.addCommodity(discountableCommodity);

        storeHelper.calculateMarkupMultiplier(store, discountableCommodity);

        BigDecimal expectedPrice = BigDecimal.valueOf(9.90).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPrice, store.applyExpiryDiscount(discountableCommodity));
    }

    @Test
    public void sellingCommodities() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Cashier cashierData = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashier = new CashierServiceImp(cashierData, helper);
        store.hireCashier(cashier);

        Commodity apple = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        Commodity soap = new Commodity(store.getNextCommodityId(), "Soap", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), LocalDate.now().plusDays(100));

        store.addCommodity(apple);
        store.addCommodity(soap);

        List<CustomCommoditiesDataType> cart = new ArrayList<>();
        cart.add(new CustomCommoditiesDataType(apple.getId(), apple.getName(), BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, apple)));
        cart.add(new CustomCommoditiesDataType(soap.getId(), soap.getName(), BigDecimal.valueOf(2), storeHelper.calculateMarkupMultiplier(store, soap)));

        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(BigDecimal.valueOf(9), store.getAvailableCommodities().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(3), store.getAvailableCommodities().get(1).getQuantity());
    }

    @Test
    public void deliveredCommoditiesRemainUnchangedAfterSelling() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);


        Cashier cashierData = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashier = new CashierServiceImp(cashierData, helper);
        store.hireCashier(cashier);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomCommoditiesDataType> cart = List.of(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(BigDecimal.valueOf(10), store.getDeliveredCommodities().get(0).getQuantity());
    }

    @Test
    public void storeRevenueUpdate() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);


        Cashier cashierData = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashier = new CashierServiceImp(cashierData, helper);
        store.hireCashier(cashier);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(2), BigDecimal.valueOf(1), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomCommoditiesDataType> cart = List.of(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(BigDecimal.valueOf(2.20).setScale(2, RoundingMode.HALF_UP),
                store.getRevenue().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateMonthlySalaries() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);


        CashierServiceHelper helper = new CashierServiceHelper();

        Cashier cashier1 = new Cashier("Alice", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        ICashierService cashierService1 = new CashierServiceImp(cashier1, helper);
        store.hireCashier(cashierService1);

        Cashier cashier2 = new Cashier("BobTheBuilder", store.getNextCashierId(), BigDecimal.valueOf(2000), store);
        ICashierService cashierService2 = new CashierServiceImp(cashier2, helper);
        store.hireCashier(cashierService2);

        BigDecimal expected = BigDecimal.valueOf(3800);
        BigDecimal actual = store.calculateMonthlySalaries();

        assertEquals(0, actual.compareTo(expected));
    }

    @Test
    public void calculatePureRevenue() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1000), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "GoldBar", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(10000), BigDecimal.valueOf(1), null);
        store.addCommodity(commodity);

        List<CustomCommoditiesDataType> cart = List.of(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashierService.sellCommodities(cart, BigDecimal.valueOf(20000)));

        BigDecimal expected = BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = store.calculatePureRevenue().setScale(2, RoundingMode.HALF_UP);

        assertEquals(expected, actual);
    }

    @Test
    public void printAndSaveReceipt() {
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);


        CashierServiceHelper helper = new CashierServiceHelper();

        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomCommoditiesDataType> cart = List.of(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        Receipt receipt = null;

        try {
            receipt = cashierService.sellCommodities(cart, BigDecimal.valueOf(15));
        } catch (Exception e) {
            fail("Exception during selling: " + e.getMessage());
        }

        assertNotNull(receipt);
        ReceiptFileManager.writeToFile(receipt);
        ReceiptPrinter.printReceipt(receipt);
    }

    @Test
    public void readAndPrintReceipt() {
        Set<IStoreService> stores = new HashSet<>();

        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);
        stores.add(store);

        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomCommoditiesDataType> cart = List.of(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        try {
            Receipt receipt = cashierService.sellCommodities(cart, BigDecimal.valueOf(15));
            ReceiptFileManager.writeToFile(receipt);
        } catch (Exception e) {
            fail("Failed to write receipt: " + e.getMessage());
        }

        // Store data was reset
        Set<Receipt> newReceipts = new HashSet<>();
        store.setReceipts(newReceipts);

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

        } catch (Exception e) {
            fail("Error occurred while reading receipts: " + e.getMessage());
        }
    }
}