package Shop.cashiers;

import Shop.commodities.Commodity;
import Shop.commodities.CustomDataType;
import Shop.exceptions.CashierNotHiredRException;
import Shop.exceptions.EmptyCartRException;
import Shop.exceptions.InsufficientFundsException;
import Shop.exceptions.InsufficientQuantityRException;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class CashierServiceHelper {


    // sellCommodities Helpers
    public boolean validateCashier(IStoreService store, ICashierService cashier) {
        boolean isHired = store.getCashiers().stream().anyMatch(c -> c.getId() == cashier.getId());
        if (!isHired) {
            throw new CashierNotHiredRException(cashier.getId());
        }
        return true;
    }

    public boolean validateCart(List<CustomDataType> cartCommodities) {
        if (cartCommodities.isEmpty()) {
            throw new EmptyCartRException();
        }
        return true;
    }

    public Commodity findCommodityById(List<Commodity> availableCommodities, int id) {
        for (Commodity item : availableCommodities) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public boolean validateStockAvailability(Commodity commodity, BigDecimal requestedQuantity) {
        if (commodity.getQuantity().compareTo(requestedQuantity) < 0) {
            throw new InsufficientQuantityRException(commodity.getName(), commodity.getQuantity(), requestedQuantity);
        }
        return true;
    }

    public Commodity updateAvailableStock(Commodity commodity, BigDecimal quantityPurchased) {
        commodity.setQuantity(commodity.getQuantity().subtract(quantityPurchased));
        return commodity;
    }

    public CustomDataType createPurchasedItem(ICashierService cashier, Commodity commodity, BigDecimal quantity) {
        BigDecimal multiplier = calculateMarkupMultiplier(cashier, commodity);
        BigDecimal priceWithMarkup = commodity.getDeliveryPrice().multiply(multiplier);
        return new CustomDataType(commodity.getId(), commodity.getName(), quantity, priceWithMarkup);
    }

    public CustomDataType updateSoldCommodities(IStoreService store, CustomDataType purchased) {
        for (CustomDataType sold : store.getSoldCommodities()) {
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

    public BigDecimal calculateItemTotal(ICashierService cashier, Commodity commodity, BigDecimal quantity) {
        BigDecimal multiplier = calculateMarkupMultiplier(cashier, commodity);
        BigDecimal deliverPriceWithMarkup = commodity.getDeliveryPrice().multiply(multiplier);
        return deliverPriceWithMarkup.multiply(quantity);
    }

    public Receipt generateReceipt(IStoreService store, ICashierService cashier, List<CustomDataType> items, BigDecimal totalCost, BigDecimal change) {
        int receiptId = store.getReceiptCount() + 1;
        store.setReceiptCount(receiptId);
        LocalDateTime issued = LocalDateTime.now();
        return new Receipt(receiptId, store, cashier, issued, items, totalCost, change);
    }

    // Generic Helper
    public BigDecimal calculateMarkupMultiplier(ICashierService cashier, Commodity commodity) {
        BigDecimal markupPercentage = cashier.getStore().getMarkupPercentages().getOrDefault(commodity.getCategory(), BigDecimal.ZERO);
        return BigDecimal.ONE.add(markupPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }
}