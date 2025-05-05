package org.example;

import Shop.Exceptions.*;

import Shop.Store.IStore;
import Shop.Store.Store;

import Shop.employees.ICashier;
import Shop.employees.Cashier;

import Shop.Commodity.ICommodity;
import Shop.Commodity.Commodity;
import Shop.Commodity.CommodityCategory;
import Shop.Commodity.CustomCommoditiesDataType;

import Shop.transactions.IReceipt;

// Generic
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestService {

    IStore store;
    ICashier cashier;
    ICommodity commodity;
    List<CustomCommoditiesDataType> cart;

    @BeforeEach
    public void setUp() {
        store = new Store("Store", BigDecimal.valueOf(10), BigDecimal.valueOf(10), BigDecimal.valueOf(10), 3);

        cashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1800), store);
        assertDoesNotThrow(() -> store.hireCashier(cashier));

        commodity = new Commodity(store.getNextCommodityId(), "Apple", CommodityCategory.EATABLE, BigDecimal.valueOf(1),
                BigDecimal.valueOf(2), 10, LocalDate.now().plusDays(5), false);
        store.addCommodity(commodity);

        cart = new ArrayList<>();
        cart.add(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(), 1, commodity.getPrice()));
    }

    @Test
    public void hiringCashier() {
        ICashier newCashier = new Cashier("Bob", store.getNextCashierId(), BigDecimal.valueOf(1500), store);

        try {
            store.hireCashier(newCashier);
        } catch (CashierAlreadyExistsException e) {
            fail("Cashier Already Exists Exception! " + e);
        }

        assertTrue(store.getCashiers().contains(newCashier));
    }

    @Test
    public void addingCommodityToStore() {
        ICommodity newCommodity = new Commodity(store.getNextCommodityId(), "Milk", CommodityCategory.EATABLE, BigDecimal.valueOf(1),
                BigDecimal.valueOf(2), 10, LocalDate.now().plusDays(5), false);
        store.addCommodity(newCommodity);

        assertEquals(2, store.getAvailableCommodities().size());
        assertTrue(store.getAvailableCommodities().contains(commodity));

        assertEquals(2, store.getDeliveredCommodities().size());
        assertTrue(store.getAvailableCommodities().contains(commodity));
    }

    @Test
    public void addingAlreadyExistingCommodityToStore() {
        store.addCommodity(commodity);

        assertEquals(20, store.getAvailableCommodities().getFirst().getQuantity());
        assertEquals(20, store.getDeliveredCommodities().getFirst().getQuantity());
    }


    @Test
    public void checkAndSetExpiryStatus() {
        ICommodity expiringToday = new Commodity(3, "Apple", CommodityCategory.EATABLE, BigDecimal.valueOf(1), BigDecimal.valueOf(2),
                1, LocalDate.now(), false);

        store.checkAndSetExpiryStatus(expiringToday);

        assertTrue(expiringToday.getIsExpired());
    }

    @Test
    public void applyExpiryDiscount() {
        ICommodity discountableCommodity = new Commodity(2, "Apple", CommodityCategory.EATABLE, BigDecimal.valueOf(10), BigDecimal.valueOf(10),
                1, LocalDate.now().plusDays(1), false);
        store.addCommodity(discountableCommodity);

        store.applyExpiryDiscount(discountableCommodity);

        BigDecimal expectedPrice = BigDecimal.valueOf(9.90).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedPrice, discountableCommodity.getPrice());
    }

    @Test
    public void sellingCommodities() {
        ICommodity newCommodity = new Commodity(store.getNextCommodityId(), "Soap", CommodityCategory.NONEATABLE, BigDecimal.valueOf(2), BigDecimal.valueOf(4),
                5, LocalDate.now().plusDays(100), false);
        store.addCommodity(newCommodity);

        cart.add(new CustomCommoditiesDataType(newCommodity.getId(), newCommodity.getName(), 2, newCommodity.getPrice()));

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

        assertEquals(9, store.getAvailableCommodities().get(0).getQuantity());
        assertEquals(3, store.getAvailableCommodities().get(1).getQuantity());
    }

    @Test
    public void printingAndSavingReceipt() {
        IReceipt receipt = null;

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
            cashier.writeReceiptToJsonFile(receipt);
            receipt.printReceipt();
        }
    }

    @Test
    public void deliveredCommoditiesRemainUnchangedAfterSelling() {
        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(10, store.getDeliveredCommodities().getFirst().getQuantity());
    }


    @Test
    public void storeRevenueUpdate() {
        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20)));

        assertEquals(BigDecimal.valueOf(2.20).setScale(2, RoundingMode.HALF_UP),
                store.getRevenue().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void calculateMonthlySalaries() {
        ICashier cashier2 = new Cashier("BobTheBuilder", store.getNextCashierId(), BigDecimal.valueOf(2000), store);
        assertDoesNotThrow(() -> store.hireCashier(cashier2));

        assertEquals(BigDecimal.valueOf(3800), store.calculateMonthlySalaries());
    }

    @Test
    public void calculatePureRevenue() {
        ICommodity newCommodity = new Commodity(store.getNextCommodityId(), "GoldBar", CommodityCategory.NONEATABLE, BigDecimal.valueOf(0),
                BigDecimal.valueOf(2000), 1, null, false);

        store.addCommodity(newCommodity);

        cart.add(new CustomCommoditiesDataType(newCommodity.getId(), newCommodity.getName(), 1, newCommodity.getPrice()));

        assertDoesNotThrow(() -> cashier.sellCommodities(cart, BigDecimal.valueOf(20000)));

        BigDecimal value = BigDecimal.valueOf(392.20).setScale(2, RoundingMode.HALF_UP);
        assertEquals(value, store.calculatePureRevenue());
    }
}