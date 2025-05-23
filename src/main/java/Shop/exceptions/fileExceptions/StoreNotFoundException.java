package Shop.exceptions.fileExceptions;

public class StoreNotFoundException extends Exception {
  public StoreNotFoundException(int storeId) {
    super("Store with ID " + storeId + " not found.");
  }
}
