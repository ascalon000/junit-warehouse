package Main.model;

public abstract class AbstractProduct {
    private String id;
    private String name;
    private double price;
    private int quantity;

    public AbstractProduct(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public abstract String getType();

    @Override
    public String toString() {
        return String.format("| %-4s | %-15s | %-10.2f | %-8d |", id, name, price, quantity);
    }
}