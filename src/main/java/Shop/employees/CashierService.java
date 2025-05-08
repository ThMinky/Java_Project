package Shop.employees;

import Shop.commodities.Commodity;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.exceptions.*;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;

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
    public Receipt sellCommodities(List<CustomCommoditiesDataType> cartCommodities, BigDecimal money)
            throws EmptyCartException, CommodityNotFoundException, InsufficientQuantityException,
            InsufficientFundsException, CashierNotHiredException {

        IStoreService store = cashier.getStore();

        validateCashier(store, cashier);
        validateCart(cartCommodities);

        BigDecimal totalCost = BigDecimal.ZERO;
        List<CustomCommoditiesDataType> purchasedCommodities = new ArrayList<>();

        for (CustomCommoditiesDataType cartItem : cartCommodities) {
            Commodity available = findCommodityById(store, cartItem.getId());
            validateStockAvailability(available, cartItem.getQuantity());

            BigDecimal itemTotal = calculateItemTotal(available, cartItem.getQuantity());
            totalCost = totalCost.add(itemTotal);

            updateAvailableStock(available, cartItem.getQuantity());
            CustomCommoditiesDataType purchasedItem = createPurchasedItem(available, cartItem.getQuantity());
            purchasedCommodities.add(purchasedItem);

            updateSoldCommodities(store, purchasedItem);
        }

        validateFunds(money, totalCost);

        BigDecimal change = money.subtract(totalCost);
        store.setRevenue(store.getRevenue().add(totalCost));

        Receipt receipt = generateReceipt(store, cashier, purchasedCommodities, totalCost, change);
        store.getReceipts().add(receipt);
        return receipt;
    }

    private void validateCashier(IStoreService store, Cashier cashier) throws CashierNotHiredException {
        boolean isHired = store.getCashiers().stream()
                .anyMatch(c -> c.getId() == cashier.getId());

        if (!isHired) {
            throw new CashierNotHiredException(cashier.getId());
        }
    }

    private void validateCart(List<CustomCommoditiesDataType> cartCommodities) throws EmptyCartException {
        if (cartCommodities.isEmpty()) {
            throw new EmptyCartException();
        }
    }

    private Commodity findCommodityById(IStoreService store, int id) throws CommodityNotFoundException {
        for (Commodity available : store.getAvailableCommodities()) {
            if (available.getId() == id) {
                return available;
            }
        }
        throw new CommodityNotFoundException(id);
    }

    private void validateStockAvailability(Commodity commodity, BigDecimal requestedQuantity)
            throws InsufficientQuantityException {
        if (commodity.getQuantity().compareTo(requestedQuantity) < 0) {
            throw new InsufficientQuantityException(commodity.getName(), commodity.getQuantity(), requestedQuantity);
        }
    }

    private BigDecimal calculateItemTotal(Commodity commodity, BigDecimal quantity) {
        return commodity.getSellingPrice().multiply(quantity);
    }

    private void updateAvailableStock(Commodity commodity, BigDecimal quantityPurchased) {
        commodity.setQuantity(commodity.getQuantity().subtract(quantityPurchased));
    }

    private CustomCommoditiesDataType createPurchasedItem(Commodity commodity, BigDecimal quantity) {
        return new CustomCommoditiesDataType(
                commodity.getId(),
                commodity.getName(),
                quantity,
                commodity.getSellingPrice()
        );
    }

    private void updateSoldCommodities(IStoreService store, CustomCommoditiesDataType purchased) {
        for (CustomCommoditiesDataType sold : store.getSoldCommodities()) {
            if (sold.getId() == purchased.getId()) {
                sold.setQuantity(sold.getQuantity().add(purchased.getQuantity()));
                return;
            }
        }
        store.getSoldCommodities().add(purchased);
    }

    private void validateFunds(BigDecimal money, BigDecimal totalCost) throws InsufficientFundsException {
        if (money.compareTo(totalCost) < 0) {
            throw new InsufficientFundsException(totalCost, money);
        }
    }

    private Receipt generateReceipt(IStoreService store, Cashier cashier, List<CustomCommoditiesDataType> items,
                                    BigDecimal totalCost, BigDecimal change) {
        int receiptId = store.getReceiptCount() + 1;
        store.setReceiptCount(receiptId);
        LocalDateTime issued = LocalDateTime.now();

        return new Receipt(receiptId, cashier, issued, items, totalCost, change);
    }
}