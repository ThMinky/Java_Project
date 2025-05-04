package Shop.Commodity;

// Generic
import java.math.BigDecimal;
import java.time.LocalDate;

public interface ICommodity {

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    int getId();
    String getName();
    CommodityCategory getCategory();

    BigDecimal getDeliveryPrice();
    BigDecimal getSellingPrice();
    BigDecimal getPrice();
    void setPrice(BigDecimal price);

    int getQuantity();
    void setQuantity(int quantity);

    LocalDate getExpiryDate();
    Boolean getIsExpired();
    void setIsExpired(Boolean expired);
    // -----------------------------------------------------------------------------------------------------------------
}