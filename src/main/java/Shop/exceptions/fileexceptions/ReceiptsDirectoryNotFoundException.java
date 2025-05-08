package Shop.exceptions.fileexceptions;

public class ReceiptsDirectoryNotFoundException extends Exception {
  public ReceiptsDirectoryNotFoundException() {
    super("Receipts directory not found.");
  }
}
