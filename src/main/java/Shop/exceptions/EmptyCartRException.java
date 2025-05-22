package Shop.exceptions;

public class EmptyCartRException extends RuntimeException {
  public EmptyCartRException() {
    super("The cart is empty.");
  }
}