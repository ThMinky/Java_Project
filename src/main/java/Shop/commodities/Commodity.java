package Shop.commodities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Commodity {
    private int id;
    private String name;
    private CommodityCategory category;

    private BigDecimal deliveryPrice;
    private BigDecimal sellingPrice;

    private BigDecimal quantity;

    private LocalDate expiryDate;

    public Commodity(int id, String name, CommodityCategory category, BigDecimal deliveryPrice, BigDecimal sellingPrice, BigDecimal quantity, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.deliveryPrice = deliveryPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public Commodity(Commodity other) {
        this.id = other.id;
        this.name = other.name;
        this.category = other.category;
        this.deliveryPrice = other.deliveryPrice != null ? new BigDecimal(other.deliveryPrice.toString()) : null;
        this.sellingPrice = other.sellingPrice != null ? new BigDecimal(other.sellingPrice.toString()) : null;
        this.quantity = other.quantity != null ? new BigDecimal(other.quantity.toString()) : null;
        this.expiryDate = other.expiryDate;
    }

    // Getters / Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommodityCategory getCategory() {
        return category;
    }

    public void setCategory(CommodityCategory category) {
        this.category = category;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    // -----------------
}