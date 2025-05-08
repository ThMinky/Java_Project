package Shop.exceptions;

import java.math.BigDecimal;

public class InsufficientFundsException extends Exception {
  public InsufficientFundsException(BigDecimal required, BigDecimal provided) {
    super("Insufficient funds: Required " + required + ", but only " + provided + " was provided.");
  }
}
