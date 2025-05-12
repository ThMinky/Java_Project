package org.example;

import Shop.commodities.Commodity;
import Shop.commodities.CommodityCategory;
import Shop.commodities.CustomCommoditiesDataType;
import Shop.employees.ICashierService;
import Shop.receipts.Receipt;
import Shop.stores.IStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestServiceMocked {

    @Mock
    private IStoreService store;

    @Mock
    private ICashierService cashier;

    private Commodity commodity;
    private List<CustomCommoditiesDataType> cart;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity(1, "Apple", CommodityCategory.EATABLE, BigDecimal.valueOf(1), BigDecimal.valueOf(2),
                BigDecimal.valueOf(10), LocalDate.now().plusDays(5));

        cart = new ArrayList<>();
        cart.add(new CustomCommoditiesDataType(commodity.getId(), commodity.getName(), BigDecimal.valueOf(1), commodity.getSellingPrice()));
    }

    @Test
    public void hiringCashierMocked() {
        doNothing().when(store).hireCashier(any(ICashierService.class));

        store.hireCashier(cashier);

        verify(store, times(1)).hireCashier(cashier);
    }

    @Test
    public void sellingCommoditiesMocked() throws Exception {
        Receipt receipt = new Receipt(1, mock(IStoreService.class), mock(ICashierService.class), LocalDateTime.now(),
                cart, BigDecimal.valueOf(2.00), BigDecimal.valueOf(0.00));

        when(cashier.sellCommodities(anyList(), any())).thenReturn(receipt);

        Receipt result = cashier.sellCommodities(cart, BigDecimal.valueOf(2.00));

        assertEquals(BigDecimal.valueOf(2.00), result.getTotalCost());
        verify(cashier, times(1)).sellCommodities(cart, BigDecimal.valueOf(2.00));
    }

    @Test
    public void applyExpiryDiscountMocked() {
        doAnswer(invocation -> {
            Commodity c = invocation.getArgument(0);
            c.setSellingPrice(c.getSellingPrice().multiply(BigDecimal.valueOf(0.9)));
            return null;
        }).when(store).applyExpiryDiscount(commodity);

        store.applyExpiryDiscount(commodity);

        assertEquals(BigDecimal.valueOf(1.80).setScale(2, RoundingMode.HALF_UP), commodity.getSellingPrice().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void storeRevenueUpdateMocked() {
        when(store.getRevenue()).thenReturn(BigDecimal.valueOf(2.20));

        assertEquals(0, store.getRevenue().compareTo(BigDecimal.valueOf(2.20)));
    }
}
