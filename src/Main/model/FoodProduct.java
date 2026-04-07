package Main.model;

import java.time.LocalDate;

public class FoodProduct extends AbstractProduct {
    private LocalDate expirationDate;

    public FoodProduct(String id, String name, double price, int quantity, LocalDate expirationDate) {
        super(id, name, price, quantity);
        this.expirationDate = expirationDate;
    }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    @Override
    public String getType() { return "Food"; }

    @Override
    public String toString() {
        return super.toString() + String.format(" Срок годности: %-10s |", expirationDate);
    }
}