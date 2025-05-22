package Shop.cashiers;

import Shop.commodities.Commodity;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.CommodityNotFoundException;
import Shop.exceptions.InsufficientFundsException;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashierServiceImp implements ICashierService {
    public Cashier cashier;
    public CashierServiceHelper helper;

    // Constructor
    public CashierServiceImp(Cashier cashier, CashierServiceHelper helper) {
        this.cashier = cashier;
        this.helper = helper;
    }

    // Getters / Setters
    @Override
    public Cashier getCashier() {
        return cashier;
    }

    @Override
    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    @Override
    public CashierServiceHelper getHelper() {
        return helper;
    }

    @Override
    public void setHelper(CashierServiceHelper helper) {
        this.helper = helper;
    }

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
    public Receipt sellCommodities(List<CustomCommoditiesDataType> cartCommodities, BigDecimal money) throws CommodityNotFoundException, InsufficientFundsException {

        IStoreService store = cashier.getStore();

        helper.validateCashier(store, this);
        helper.validateCart(cartCommodities);

        BigDecimal totalCost = BigDecimal.ZERO;
        List<CustomCommoditiesDataType> purchasedCommodities = new ArrayList<>();

        for (CustomCommoditiesDataType cartItem : cartCommodities) {
            Commodity available = helper.findCommodityById(store.getAvailableCommodities(), cartItem.getId());

            helper.validateStockAvailability(available, cartItem.getQuantity());

            BigDecimal itemTotal = helper.calculateItemTotal(this, available, cartItem.getQuantity());
            totalCost = totalCost.add(itemTotal);

            Commodity updatedStock = helper.updateAvailableStock(available, cartItem.getQuantity());
            CustomCommoditiesDataType purchasedItem = helper.createPurchasedItem(this, updatedStock, cartItem.getQuantity());
            purchasedCommodities.add(purchasedItem);

            helper.updateSoldCommodities(store, purchasedItem);
        }

        helper.validateFunds(money, totalCost);

        BigDecimal change = money.subtract(totalCost);
        store.setRevenue(store.getRevenue().add(totalCost));

        Receipt receipt = helper.generateReceipt(store, this, purchasedCommodities, totalCost, change);
        store.getReceipts().add(receipt);
        return receipt;
    }
// =====================================================================================================================
}