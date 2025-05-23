package Shop.commodities;

import java.math.BigDecimal;

public class CustomDataType {
    private final int id;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;

    // Constructor
    public CustomDataType(int id, String name, BigDecimal quantity, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters / Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
    // -----------------
}