package Main.core;

import Main.model.FoodProduct;
import Main.model.AbstractProduct;
import Main.model.FoodProduct;
import Main.strategy.StorageStrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Warehouse {
    private static Warehouse instance;
    private final Map<String, AbstractProduct> products;
    private StorageStrategy storageStrategy;

    private Warehouse() {
        this.products = new ConcurrentHashMap<>();
    }

    public static Warehouse getInstance() {
        if (instance == null) {
            synchronized (Warehouse.class) {
                if (instance == null) {
                    instance = new Warehouse();
                }
            }
        }
        return instance;
    }

    public void setStorageStrategy(StorageStrategy strategy) {
        this.storageStrategy = strategy;
    }

    public void addProduct(AbstractProduct product) {
        products.put(product.getId(), product);
    }

    public void removeProduct(String id) {
        products.remove(id);
    }

    public AbstractProduct getProduct(String id) {
        return products.get(id);
    }

    public Map<String, AbstractProduct> getAllProducts() {
        return products;
    }


    public List<AbstractProduct> getProductsByType(String type) {
        return products.values().stream()
                .filter(p -> p.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<AbstractProduct> searchByName(String substring) {
        return products.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<AbstractProduct> getProductsMoreExpensiveThan(double price) {
        return products.values().stream()
                .filter(p -> p.getPrice() > price)
                .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                .collect(Collectors.toList());
    }

    public List<FoodProduct> getExpiredFoodProducts() {
        return products.values().stream()
                .filter(p -> p instanceof FoodProduct)
                .map(p -> (FoodProduct) p)
                .filter(FoodProduct::isExpired)
                .collect(Collectors.toList());
    }

    public void saveData(String filePath) throws Exception {
        if (storageStrategy != null) {
            storageStrategy.saveToFile(products, filePath);
        }
    }

    public void loadData(String filePath) throws Exception {
        if (storageStrategy != null) {
            Map<String, AbstractProduct> loaded = storageStrategy.loadFromFile(filePath);
            products.clear();
            products.putAll(loaded);
        }
    }
}