package Shop.Commodity;

// Generic
import java.math.BigDecimal;
import java.time.LocalDate;

public class Commodity implements ICommodity {
    private int id;
    private String name;
    private CommodityCategory category;

    private BigDecimal deliveryPrice;
    private BigDecimal sellingPrice;
    private BigDecimal price;

    private int quantity;

    private LocalDate expiryDate;
    private Boolean isExpired;

    public Commodity(int id, String name, CommodityCategory category, BigDecimal deliveryPrice, BigDecimal sellingPrice,
                     int quantity, LocalDate expiryDate, Boolean isExpired) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.deliveryPrice = deliveryPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.isExpired = isExpired;

        this.price = BigDecimal.ZERO;
    }

    // --- Copy constructor ---
    public Commodity(ICommodity other) {
        this.id = other.getId();
        this.name = other.getName();
        this.category = other.getCategory();
        this.deliveryPrice = other.getDeliveryPrice();
        this.sellingPrice = other.getSellingPrice();
        this.price = other.getPrice();
        this.quantity = other.getQuantity();
        this.expiryDate = other.getExpiryDate();
        this.isExpired = other.getIsExpired();

        this.price = other.getPrice();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // --- Getters / Setters ---
    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public CommodityCategory getCategory() {
        return category;
    }

    public void setCategory(CommodityCategory category) {
        this.category = category;
    }


    @Override
    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }


    @Override
    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }


    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }


    @Override
    public Boolean getIsExpired() {
        return isExpired;
    }

    @Override
    public void setIsExpired(Boolean expired) {
        isExpired = expired;
    }
    // -----------------------------------------------------------------------------------------------------------------
}