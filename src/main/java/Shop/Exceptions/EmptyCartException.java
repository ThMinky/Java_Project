package Shop.Exceptions;

public class EmptyCartException extends Exception {
  public EmptyCartException() {
    super("The cart is empty.");
  }
}
