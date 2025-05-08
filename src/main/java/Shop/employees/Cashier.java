package Shop.employees;

import Shop.stores.IStoreService;

import java.math.BigDecimal;

public class Cashier {
    private int id;
    private String name;
    private BigDecimal salary;
    private IStoreService store;

    public Cashier(String name, int id, BigDecimal salary, IStoreService store) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.store = store;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public IStoreService getStore() { return store; }
    public void setStore(IStoreService store) { this.store = store; }
    // -----------------
}