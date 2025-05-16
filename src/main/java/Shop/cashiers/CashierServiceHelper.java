package Shop.cashiers;

import Shop.commodities.Commodity;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.*;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CashierServiceHelper {

    public boolean validateCashier(IStoreService store, Cashier cashier) throws CashierNotHiredException {
        boolean isHired = store.getCashiers().stream().anyMatch(c -> c.getId() == cashier.getId());

        if (!isHired) {
            throw new CashierNotHiredException(cashier.getId());
        }
        return true;
    }

    public boolean validateCart(List<CustomCommoditiesDataType> cartCommodities) throws EmptyCartException {
        if (cartCommodities.isEmpty()) {
            throw new EmptyCartException();
        }
        return true;
    }

    public Commodity findCommodityById(IStoreService store, int id) throws CommodityNotFoundException {
        for (Commodity available : store.getAvailableCommodities()) {
            if (available.getId() == id) {
                return available;
            }
        }
        throw new CommodityNotFoundException(id);
    }

    public boolean validateStockAvailability(Commodity commodity, BigDecimal requestedQuantity) throws InsufficientQuantityException {
        if (commodity.getQuantity().compareTo(requestedQuantity) < 0) {
            throw new InsufficientQuantityException(commodity.getName(), commodity.getQuantity(), requestedQuantity);
        }
        return true;
    }

    public BigDecimal calculateItemTotal(Commodity commodity, BigDecimal quantity) {
        return commodity.getSellingPrice().multiply(quantity);
    }

    public Commodity updateAvailableStock(Commodity commodity, BigDecimal quantityPurchased) {
        commodity.setQuantity(commodity.getQuantity().subtract(quantityPurchased));
        return commodity;
    }

    public CustomCommoditiesDataType createPurchasedItem(Commodity commodity, BigDecimal quantity) {
        return new CustomCommoditiesDataType(commodity.getId(), commodity.getName(), quantity, commodity.getSellingPrice());
    }

    public CustomCommoditiesDataType updateSoldCommodities(IStoreService store, CustomCommoditiesDataType purchased) {
        for (CustomCommoditiesDataType sold : store.getSoldCommodities()) {
            if (sold.getId() == purchased.getId()) {
                sold.setQuantity(sold.getQuantity().add(purchased.getQuantity()));
                return sold;
            }
        }
        store.getSoldCommodities().add(purchased);
        return purchased;
    }

    public boolean validateFunds(BigDecimal money, BigDecimal totalCost) throws InsufficientFundsException {
        if (money.compareTo(totalCost) < 0) {
            throw new InsufficientFundsException(totalCost, money);
        }
        return true;
    }

    public Receipt generateReceipt(IStoreService store, ICashierService cashier, List<CustomCommoditiesDataType> items, BigDecimal totalCost, BigDecimal change) {
        int receiptId = store.getReceiptCount() + 1;
        store.setReceiptCount(receiptId);
        LocalDateTime issued = LocalDateTime.now();

        return new Receipt(receiptId, store, cashier, issued, items, totalCost, change);
    }
}