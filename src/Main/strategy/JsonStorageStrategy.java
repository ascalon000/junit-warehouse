package Main.strategy;

import Main.model.AbstractProduct;
import Main.model.ElectronicsProduct;
import Main.model.FoodProduct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class JsonStorageStrategy implements StorageStrategy {

    @Override
    public void saveToFile(Map<String, AbstractProduct> products, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write("[\n");
            int count = 0;
            for (AbstractProduct p : products.values()) {
                writer.write("  {\n");
                writer.write("    \"id\": \"" + p.getId() + "\",\n");
                writer.write("    \"name\": \"" + p.getName() + "\",\n");
                writer.write("    \"price\": " + p.getPrice() + ",\n");
                writer.write("    \"quantity\": " + p.getQuantity() + ",\n");
                writer.write("    \"type\": \"" + p.getType() + "\",\n");

                if (p instanceof FoodProduct fp) {
                    writer.write("    \"expirationDate\": \"" + fp.getExpirationDate() + "\"\n");
                } else if (p instanceof ElectronicsProduct ep) {
                    writer.write("    \"warrantyMonths\": " + ep.getWarrantyMonths() + "\n");
                }

                writer.write("  }" + (count < products.size() - 1 ? "," : "") + "\n");
                count++;
            }
            writer.write("]");
        }
    }

    @Override
    public Map<String, AbstractProduct> loadFromFile(String filePath) throws IOException {
        Map<String, AbstractProduct> products = new HashMap<>();
        if (!Files.exists(Paths.get(filePath))) return products;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            String id = null, name = null, type = null;
            double price = 0;
            int quantity = 0, warranty = 0;
            LocalDate expDate = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim().replaceAll("[{},\\[\\]]", "").replaceAll("\"", "");
                if (line.isEmpty()) continue;

                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "id" -> id = value;
                    case "name" -> name = value;
                    case "price" -> price = Double.parseDouble(value);
                    case "quantity" -> quantity = Integer.parseInt(value);
                    case "type" -> type = value;
                    case "expirationDate" -> expDate = LocalDate.parse(value);
                    case "warrantyMonths" -> warranty = Integer.parseInt(value);
                }

                if ("type".equals(key) || "expirationDate".equals(key) || "warrantyMonths".equals(key)) {
                    AbstractProduct product = switch (type) {
                        case "Food" -> new FoodProduct(id, name, price, quantity, expDate);
                        case "Electronics" -> new ElectronicsProduct(id, name, price, quantity, warranty);
                        default -> null;
                    };
                    if (product != null) products.put(id, product);
                }
            }
        }
        return products;
    }
}