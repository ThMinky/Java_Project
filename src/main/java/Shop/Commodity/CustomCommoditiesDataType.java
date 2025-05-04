package Shop.Commodity;

// Generic
import java.math.BigDecimal;

public class CustomCommoditiesDataType {
    private int id;
    private String name;
    private int quantity;
    private BigDecimal price;

    public CustomCommoditiesDataType(int id, String name, int quantity, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
    // -----------------------------------------------------------------------------------------------------------------
}
