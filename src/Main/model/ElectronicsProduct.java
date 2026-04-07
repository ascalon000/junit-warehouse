package Main.model;

public class ElectronicsProduct extends AbstractProduct {
    private int warrantyMonths;

    public ElectronicsProduct(String id, String name, double price, int quantity, int warrantyMonths) {
        super(id, name, price, quantity);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(int warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    @Override
    public String getType() { return "Electronics"; }

    @Override
    public String toString() {
        return super.toString() + String.format(" Гарантия: %-5d мес. |", warrantyMonths);
    }
}