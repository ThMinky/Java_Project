package org.example;

import Shop.cashiers.Cashier;
import Shop.cashiers.CashierServiceHelper;
import Shop.cashiers.CashierServiceImp;
import Shop.cashiers.ICashierService;
import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomDataType;
import Shop.exceptions.CommodityExpiredDateRException;
import Shop.exceptions.InsufficientFundsException;
import Shop.exceptions.InsufficientQuantityRException;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    // Cashier
    @Test
    public void hiringCashier_One() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Test
        assertTrue(store.getCashiers().contains(cashierService));

        Iterator<ICashierService> iterator = store.getCashiers().iterator();

        if (iterator.hasNext()) {
            ICashierService first = iterator.next();
            System.out.println("Cashier ID: " + first.getId() + ", Name: " + first.getName());
        }
    }

    @Test
    public void hiringCashier_Two() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashiers
        Cashier cashier_one = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService_one = new CashierServiceImp(cashier_one, helper);
        store.hireCashier(cashierService_one);

        // Cashier
        Cashier cashier_two = new Cashier("BobTheBuilder", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        ICashierService cashierService_two = new CashierServiceImp(cashier_two, helper);
        store.hireCashier(cashierService_two);

        // Test
        assertTrue(store.getCashiers().contains(cashierService_one) && store.getCashiers().contains(cashierService_two));

        Iterator<ICashierService> iterator = store.getCashiers().iterator();

        if (iterator.hasNext()) {
            ICashierService first = iterator.next();
            System.out.println("Cashier ID: " + first.getId() + ", Name: " + first.getName());

            if (iterator.hasNext()) {
                ICashierService second = iterator.next();
                System.out.println("Cashier ID: " + second.getId() + ", Name: " + second.getName());
            }
        }
    }


    // Commodity
    @Test
    public void addingCommodityToStore() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        // Test
        assertEquals(1, store.getAvailableCommodities().size());
        assertTrue(store.getAvailableCommodities().stream().anyMatch(c -> c.getId() == commodity.getId()));

        System.out.println("Id: " + store.getAvailableCommodities().get(0).getId());
        System.out.println("Name: " + store.getAvailableCommodities().get(0).getName());
        System.out.println("Quantity: " + store.getAvailableCommodities().get(0).getQuantity());
    }

    @Test
    public void addingAlreadyExistingCommodityToStore() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));

        // First time
        store.addCommodity(commodity);
        System.out.println("Quantity: " + store.getAvailableCommodities().get(0).getQuantity());

        // Second time
        store.addCommodity(commodity);
        System.out.println("Quantity: " + store.getAvailableCommodities().get(0).getQuantity());

        // Test
        assertEquals(BigDecimal.valueOf(20), store.getAvailableCommodities().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(20), store.getDeliveredCommodities().get(0).getQuantity());
    }

    @Test
    public void sellCommodity() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomDataType> cart = List.of(new CustomDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashierService.sellCommodities(cart, BigDecimal.valueOf(100)));

        // Test
        assertEquals(BigDecimal.valueOf(9), store.getAvailableCommodities().get(0).getQuantity());

        System.out.println("Id: " + store.getAvailableCommodities().get(0).getId());
        System.out.println("Name: " + store.getAvailableCommodities().get(0).getName());
        System.out.println("Quantity: " + store.getAvailableCommodities().get(0).getQuantity());
    }

    @Test
    public void sellingCommodities_NotEnoughQuantity() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomDataType> cart = List.of(new CustomDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(11), storeHelper.calculateMarkupMultiplier(store, commodity)));

        // Test
        InsufficientQuantityRException exception = assertThrows(InsufficientQuantityRException.class, () -> {
            cashierService.sellCommodities(cart, BigDecimal.valueOf(100));
        });

        System.out.println("Caught expected exception: " + exception.getMessage());
    }

    @Test
    public void sellingCommodities_NotEnoughFunds() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(100), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        List<CustomDataType> cart = List.of(new CustomDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        // Test
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            cashierService.sellCommodities(cart, BigDecimal.valueOf(10));
        });

        System.out.println("Caught expected exception: " + exception.getMessage());
    }

    @Test
    public void deliveredCommoditiesRemainUnchangedAfterSelling() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Commodity", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(5), null);
        store.addCommodity(commodity);

        List<CustomDataType> cart = List.of(new CustomDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashierService.sellCommodities(cart, BigDecimal.valueOf(100)));

        BigDecimal expected = BigDecimal.valueOf(5).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = store.getDeliveredCommodities().get(0).getQuantity();

        // Test
        assertTrue(actual.compareTo(expected) == 0);
        System.out.println("Delivered quantity is: " + actual);
    }


    // ExpiryStatus/Discount
    @Test
    public void checkAndSetExpiryStatus() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity expiredCommodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(1));
        store.addCommodity(expiredCommodity);

        expiredCommodity.setExpiryDate(LocalDate.now().minusDays(1));

        // Test
        CommodityExpiredDateRException exception = assertThrows(CommodityExpiredDateRException.class, () -> {
            store.checkForExpired(expiredCommodity);
        });

        System.out.println("Caught expected exception: " + exception.getMessage());
    }

    @Test
    public void tryToAddAlreadyExpiredCommodity(){
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity expiredCommodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().minusDays(1));

        // Test
        CommodityExpiredDateRException exception = assertThrows(CommodityExpiredDateRException.class, () -> {
            store.addCommodity(expiredCommodity);
        });

        System.out.println("Caught expected exception: " + exception.getMessage());
    }

    @Test
    public void checkAndApplyExpiryDiscount() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity discountableCommodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(discountableCommodity);

        discountableCommodity.setExpiryDate(LocalDate.now().minusDays(3));

        BigDecimal expectedPrice = BigDecimal.valueOf(9.90).setScale(2, RoundingMode.HALF_UP);

        // Test
        assertEquals(expectedPrice, store.applyExpiryDiscount(discountableCommodity));
    }

    @Test
    public void tryToAddCommodityThatIsAboutToExpiry() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity discountableCommodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10), LocalDate.now().plusDays(1));
        store.addCommodity(discountableCommodity);

        BigDecimal expectedPrice = BigDecimal.valueOf(9.90).setScale(2, RoundingMode.HALF_UP);

        // Test
        assertEquals(expectedPrice, store.applyExpiryDiscount(discountableCommodity));
    }


    // TotalDeliveryCost
    @Test
    public void calculateTotalDeliveryCost_Positive() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Commodity", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), null);
        store.addCommodity(commodity);

        BigDecimal actual = store.calculateTotalDeliveryCost().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertTrue(actual.compareTo(BigDecimal.ZERO) > 0);
        System.out.println("The total delivery cost is : " + actual);
    }

    @Test
    public void calculateTotalDeliveryCost_Negative() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Commodity", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(-1), BigDecimal.valueOf(10), null);
        store.addCommodity(commodity);

        BigDecimal actual = store.calculateTotalDeliveryCost().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertTrue(actual.compareTo(BigDecimal.ZERO) < 0);
        System.out.println("The total delivery cost is : " + actual);
    }

    @Test
    public void calculateTotalDeliveryCost_Zero() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        BigDecimal actual = store.calculateTotalDeliveryCost().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertEquals(0, actual.compareTo(BigDecimal.ZERO));
        System.out.println("The total delivery cost is : " + actual);
    }


    // Revenue
    @Test
    public void storeRevenueUpdate_Positive() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Commodity", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(1), null);
        store.addCommodity(commodity);

        List<CustomDataType> cart = List.of(new CustomDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashierService.sellCommodities(cart, BigDecimal.valueOf(100)));

        BigDecimal actual = store.calculateRevenue().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertTrue(actual.compareTo(BigDecimal.ZERO) > 0);
        System.out.println("Store revenue is: " + actual);
    }

    @Test
    public void storeRevenueUpdate_Negative() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Commodity", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(-1), BigDecimal.valueOf(1), null);
        store.addCommodity(commodity);

        List<CustomDataType> cart = List.of(new CustomDataType(commodity.getId(), commodity.getName(),
                BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity)));

        assertDoesNotThrow(() -> cashierService.sellCommodities(cart, BigDecimal.valueOf(100)));

        BigDecimal actual = store.calculateRevenue().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertTrue(actual.compareTo(BigDecimal.ZERO) < 0);
        System.out.println("Store revenue is: " + actual);
    }

    @Test
    public void storeRevenueUpdate_Zero() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        BigDecimal actual = store.calculateRevenue().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertEquals(0, actual.compareTo(BigDecimal.ZERO));
        System.out.println("Store revenue is: " + actual);
    }


    // Monthly Salaries
    @Test
    public void calculateMonthlySalaries_Positive() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        BigDecimal actual = store.calculateMonthlySalaries().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertTrue(actual.compareTo(BigDecimal.ZERO) > 0);
        System.out.println("The cost for monthly salaries is: " + actual);
    }

    @Test
    public void calculateMonthlySalaries_Negative() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(-10), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        BigDecimal actual = store.calculateMonthlySalaries().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertTrue(actual.compareTo(BigDecimal.ZERO) < 0);
        System.out.println("The cost for monthly salaries is: " + actual);
    }

    @Test
    public void calculateMonthlySalaries_Zero() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        BigDecimal actual = store.calculateMonthlySalaries().setScale(2, RoundingMode.HALF_UP);

        // Test
        assertEquals(0, actual.compareTo(BigDecimal.ZERO));
        System.out.println("The cost for monthly salaries is: " + actual);
    }


    // Receipts
    @Test
    public void printAndSaveReceipt_One() {
        // Store
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", 1, BigDecimal.valueOf(1800), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        // Commodities
        Commodity commodity_one = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity_one);

        Commodity commodity_two = new Commodity(store.getNextCommodityId(), "Soup", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), LocalDate.now().plusDays(365));
        store.addCommodity(commodity_two);

        List<CustomDataType> cart = List.of(
                new CustomDataType(commodity_one.getId(), commodity_one.getName(),
                        BigDecimal.valueOf(3), storeHelper.calculateMarkupMultiplier(store, commodity_one)),
                new CustomDataType(commodity_two.getId(), commodity_two.getName(),
                        BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity_two)));

        Receipt receipt = null;

        try {
            receipt = cashierService.sellCommodities(cart, BigDecimal.valueOf(15));
        } catch (Exception e) {
            fail("Exception during selling: " + e.getMessage());
        }

        // Test
        assertNotNull(receipt);

        ReceiptFileManager.writeToFile(receipt);
        ReceiptPrinter.printReceipt(receipt);
    }

    @Test
    public void printAndSaveReceipt_Two() {
        // Store
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Commodities
        Commodity commodity_one = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity_one);

        Commodity commodity_two = new Commodity(store.getNextCommodityId(), "Soup", CommodityCategory.NONEATABLE,
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), LocalDate.now().plusDays(365));
        store.addCommodity(commodity_two);

        // First Receipt
        // Cashier_1
        Cashier cashier_one = new Cashier("Bob", 1, BigDecimal.valueOf(1000), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService_one = new CashierServiceImp(cashier_one, helper);
        store.hireCashier(cashierService_one);

        List<CustomDataType> cart = List.of(
                new CustomDataType(commodity_one.getId(), commodity_one.getName(),
                        BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity_one)),
                new CustomDataType(commodity_two.getId(), commodity_two.getName(),
                        BigDecimal.valueOf(1), storeHelper.calculateMarkupMultiplier(store, commodity_two)));

        Receipt receipt = null;

        try {
            receipt = cashierService_one.sellCommodities(cart, BigDecimal.valueOf(15));
        } catch (Exception e) {
            fail("Exception during selling: " + e.getMessage());
        }

        if (receipt != null) {
            ReceiptFileManager.writeToFile(receipt);
            ReceiptPrinter.printReceipt(receipt);
        }

        // Second Receipt
        // Cashier_2
        Cashier cashier_two = new Cashier("BobTheBuilder", 2, BigDecimal.valueOf(1000), store);
        ICashierService cashierService_two = new CashierServiceImp(cashier_two, helper);
        store.hireCashier(cashierService_two);

        cart = List.of(
                new CustomDataType(commodity_one.getId(), commodity_one.getName(),
                        BigDecimal.valueOf(2), storeHelper.calculateMarkupMultiplier(store, commodity_one)),
                new CustomDataType(commodity_two.getId(), commodity_two.getName(),
                        BigDecimal.valueOf(2), storeHelper.calculateMarkupMultiplier(store, commodity_two)));

        receipt = null;

        try {
            receipt = cashierService_two.sellCommodities(cart, BigDecimal.valueOf(15));
        } catch (Exception e) {
            fail("Exception during selling: " + e.getMessage());
        }

        // Test
        assertNotNull(receipt);

        ReceiptFileManager.writeToFile(receipt);
        ReceiptPrinter.printReceipt(receipt);
    }

    @Test
    public void readAndPrintReceipt() {
        Set<IStoreService> stores = new HashSet<>();

        // Store
        Store storeData = new Store(1, "MegaStore", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);
        stores.add(store);

        // Cashiers
        Cashier cashier = new Cashier("Bob", 1, BigDecimal.valueOf(1000), store);
        CashierServiceHelper helper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, helper);
        store.hireCashier(cashierService);

        Cashier cashier_two = new Cashier("BobTheBuilder", 2, BigDecimal.valueOf(1000), store);
        ICashierService cashierService_two = new CashierServiceImp(cashier_two, helper);
        store.hireCashier(cashierService_two);

        // Empty the receipts set
        Set<Receipt> emptyReceiptsSet = new HashSet<>();
        store.setReceipts(emptyReceiptsSet);

        int numOfReceiptsFound = 0;

        try {
            Set<Receipt> receipts = ReceiptFileManager.readReceiptsFromFiles(stores);

            for (Receipt receipt : receipts) {
                if (receipt.getStoreId() == store.getId()) {
                    store.getReceipts().add(receipt);
                    ReceiptPrinter.printReceipt(receipt);
                    numOfReceiptsFound++;
                }
            }

            assertEquals(numOfReceiptsFound, store.getReceipts().size());

        } catch (Exception e) {
            fail("Error occurred while reading receipts: " + e.getMessage());
        }
    }


    // Sell sellCommodity Helpers test
    @Test
    public void sellCommodityHelpersTest() {
        // Store
        Store storeData = new Store(1, "Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                BigDecimal.valueOf(10), 3);
        StoreServiceHelper storeHelper = new StoreServiceHelper();
        IStoreService store = new StoreServiceImp(storeData, storeHelper);

        // Cashier
        Cashier cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(10), store);
        CashierServiceHelper cashierHelper = new CashierServiceHelper();
        ICashierService cashierService = new CashierServiceImp(cashier, cashierHelper);
        store.hireCashier(cashierService);

        // Commodity
        Commodity commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE,
                BigDecimal.valueOf(1), BigDecimal.valueOf(10), LocalDate.now().plusDays(5));
        store.addCommodity(commodity);

        // findCommodityById Test
        Commodity found = storeHelper.findCommodityById(store.getAvailableCommodities(), commodity.getId());
        assertEquals(commodity.getId(), found.getId());
        assertEquals("Apple", found.getName());
        System.out.println("findCommodityById passed");

        // calculateMarkupMultiplier Test
        BigDecimal markupMultiplier = storeHelper.calculateMarkupMultiplier(store, commodity);
        BigDecimal expectedMultiplier = new BigDecimal("1.10").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedMultiplier, markupMultiplier);
        System.out.println("calculateMarkupMultiplier passed");

        //Cart
        BigDecimal quantity = BigDecimal.valueOf(1);
        BigDecimal priceWithMarkup = commodity.getDeliveryPrice().multiply(markupMultiplier);
        List<CustomDataType> cart = List.of(
                new CustomDataType(commodity.getId(), commodity.getName(), quantity, priceWithMarkup));
        BigDecimal customerFunds = BigDecimal.valueOf(100);

        // validateCashier Test
        assertTrue(cashierHelper.validateCashier(store, cashierService));
        System.out.println("validateCashier passed");

        // validateCart Test
        assertTrue(cashierHelper.validateCart(cart));
        System.out.println("validateCart passed");

        // validateStockAvailability Test
        assertTrue(cashierHelper.validateStockAvailability(commodity, quantity));
        System.out.println("validateStockAvailability passed");

        // calculateItemTotal Test
        BigDecimal totalCost = cashierHelper.calculateItemTotal(cashierService, commodity, quantity);
        BigDecimal expectedTotal = new BigDecimal("1.10").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedTotal, totalCost);
        System.out.println("calculateItemTotal passed");

        // validateFunds Test
        assertDoesNotThrow(() -> {
            boolean result = cashierHelper.validateFunds(customerFunds, totalCost);
            assertTrue(result);
        });
        System.out.println("validateFunds passed");

        // updateAvailableStock Test
        Commodity updatedCommodity = cashierHelper.updateAvailableStock(commodity, quantity);
        assertEquals(BigDecimal.valueOf(9), updatedCommodity.getQuantity());
        System.out.println("updateAvailableStock passed");

        // createPurchasedItem Test
        CustomDataType purchasedItem = cashierHelper.createPurchasedItem(cashierService, commodity, quantity);
        CustomDataType expectedItem = new CustomDataType(
                commodity.getId(), commodity.getName(), BigDecimal.valueOf(1), new BigDecimal("1.10").setScale(2, RoundingMode.HALF_UP));

        assertEquals(expectedItem.getId(), purchasedItem.getId());
        assertEquals(expectedItem.getName(), purchasedItem.getName());
        assertEquals(expectedItem.getQuantity(), purchasedItem.getQuantity());
        assertEquals(expectedItem.getPrice(), purchasedItem.getPrice());
        System.out.println("createPurchasedItem passed");

        // updateSoldCommodities Test
        CustomDataType updatedSold = cashierHelper.updateSoldCommodities(store, purchasedItem);
        assertEquals(quantity, updatedSold.getQuantity());
        System.out.println("updateSoldCommodities passed");

        // generateReceipt Test
        BigDecimal change = customerFunds.subtract(totalCost);
        Receipt receipt = cashierHelper.generateReceipt(store, cashierService, List.of(purchasedItem), totalCost, change);

        assertNotNull(receipt);
        assertEquals(1, receipt.getPurchasedCommodities().size());
        assertEquals(totalCost, receipt.getTotalCost());
        assertEquals(change, receipt.getChange());
        System.out.println("generateReceipt passed");
    }
}