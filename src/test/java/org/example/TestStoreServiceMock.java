package org.example;

import Shop.stores.IStoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestStoreServiceMock {

    @Test
    void testCalculatePureRevenueMocked_Positive() {
        IStoreService iStoreServiceMocked = mock(IStoreService.class);

        when(iStoreServiceMocked.calculateRevenue()).thenReturn(new BigDecimal("10000.00"));
        when(iStoreServiceMocked.calculateMonthlySalaries()).thenReturn(new BigDecimal("2500.00"));
        when(iStoreServiceMocked.calculateTotalDeliveryCost()).thenReturn(new BigDecimal("1500.00"));

        when(iStoreServiceMocked.calculatePureRevenue()).thenAnswer(invocation -> {
            BigDecimal revenue = iStoreServiceMocked.calculateRevenue();
            BigDecimal salaries = iStoreServiceMocked.calculateMonthlySalaries();
            BigDecimal delivery = iStoreServiceMocked.calculateTotalDeliveryCost();
            return revenue.subtract(salaries).subtract(delivery).setScale(2, RoundingMode.HALF_UP);
        });

        BigDecimal expected = new BigDecimal("6000.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = iStoreServiceMocked.calculatePureRevenue();

        assertEquals(expected, actual);
    }

    @Test
    void testCalculatePureRevenueMocked_Negative() {
        IStoreService iStoreServiceMocked = mock(IStoreService.class);

        when(iStoreServiceMocked.calculateRevenue()).thenReturn(new BigDecimal("-1000.00"));
        when(iStoreServiceMocked.calculateMonthlySalaries()).thenReturn(new BigDecimal("2500.00"));
        when(iStoreServiceMocked.calculateTotalDeliveryCost()).thenReturn(new BigDecimal("1500.00"));

        when(iStoreServiceMocked.calculatePureRevenue()).thenAnswer(invocation -> {
            BigDecimal revenue = iStoreServiceMocked.calculateRevenue();
            BigDecimal salaries = iStoreServiceMocked.calculateMonthlySalaries();
            BigDecimal delivery = iStoreServiceMocked.calculateTotalDeliveryCost();
            return revenue.subtract(salaries).subtract(delivery).setScale(2, RoundingMode.HALF_UP);
        });

        BigDecimal expected = new BigDecimal("-5000.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = iStoreServiceMocked.calculatePureRevenue();

        assertEquals(expected, actual);
    }

    @Test
    void testCalculatePureRevenueMocked_Zero() {
        IStoreService iStoreServiceMocked = mock(IStoreService.class);

        when(iStoreServiceMocked.calculateRevenue()).thenReturn(BigDecimal.ZERO);
        when(iStoreServiceMocked.calculateMonthlySalaries()).thenReturn(new BigDecimal("2500.00"));
        when(iStoreServiceMocked.calculateTotalDeliveryCost()).thenReturn(new BigDecimal("1500.00"));

        when(iStoreServiceMocked.calculatePureRevenue()).thenAnswer(invocation -> {
            BigDecimal revenue = iStoreServiceMocked.calculateRevenue();
            BigDecimal salaries = iStoreServiceMocked.calculateMonthlySalaries();
            BigDecimal delivery = iStoreServiceMocked.calculateTotalDeliveryCost();
            return revenue.subtract(salaries).subtract(delivery).setScale(2, RoundingMode.HALF_UP);
        });

        BigDecimal expected = new BigDecimal("-4000.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal actual = iStoreServiceMocked.calculatePureRevenue();

        assertEquals(expected, actual);
    }
}