package Shop.exceptions.fileExceptions;

public class ReceiptsDirectoryNotFoundException extends Exception {
  public ReceiptsDirectoryNotFoundException() {
    super("Receipts directory not found.");
  }
}
