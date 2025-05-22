package Shop.stores;

import Shop.commodities.Commodity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class StoreServiceHelper {
    public Commodity findCommodityById(List<Commodity> availableCommodities, int id) {
        for (Commodity item : availableCommodities) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public BigDecimal calculateMarkupMultiplier(IStoreService store, Commodity commodity) {
        BigDecimal markupPercentage = store.getMarkupPercentages().getOrDefault(commodity.getCategory(), BigDecimal.ZERO);

        return BigDecimal.ONE.add(markupPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }
}
