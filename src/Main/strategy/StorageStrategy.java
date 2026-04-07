package Main.strategy;

import Main.model.AbstractProduct;
import java.util.Map;

public interface StorageStrategy {
    void saveToFile(Map<String, AbstractProduct> products, String filePath) throws Exception;
    Map<String, AbstractProduct> loadFromFile(String filePath) throws Exception;
}