package Shop.Exceptions;

public class CommodityNotFoundException extends Exception {
    public CommodityNotFoundException(int commodityId) {
        super("Commodity with ID " + commodityId + " was not found in the store.");
    }
}
