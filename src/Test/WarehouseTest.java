package Test;

import Main.core.Warehouse;
import Main.model.AbstractProduct;
import Main.model.ElectronicsProduct;
import Main.model.FoodProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Warehouse Tests")
public class WarehouseTest {
    private Warehouse warehouse;

    @BeforeEach
    public void setUp() {
        warehouse = Warehouse.getInstance();
        warehouse.getAllProducts().clear();
    }


    @Test
    @DisplayName("Add and get product")
    public void testAddAndGetProduct() {
        FoodProduct food = new FoodProduct("F1", "Apple", 50.0, 10, LocalDate.now().plusDays(5));
        warehouse.addProduct(food);

        assertEquals(food, warehouse.getProduct("F1"));
        assertEquals(1, warehouse.getAllProducts().size());
    }

    @Test
    @DisplayName("Remove product")
    public void testRemoveProduct() {
        ElectronicsProduct tech = new ElectronicsProduct("E1", "Phone", 50000, 5, 12);
        warehouse.addProduct(tech);
        warehouse.removeProduct("E1");

        assertNull(warehouse.getProduct("E1"));
    }

    @Test
    @DisplayName("Filter by type")
    public void testStreamFilterByType() {
        warehouse.addProduct(new FoodProduct("F1", "Bread", 30, 20, LocalDate.now().plusDays(1)));
        warehouse.addProduct(new ElectronicsProduct("E1", "Laptop", 80000, 2, 24));

        List<AbstractProduct> electronics = warehouse.getProductsByType("Electronics");

        assertEquals(1, electronics.size());
        assertEquals("Laptop", electronics.get(0).getName());
    }

    @Test
    @DisplayName("Filter by price")
    public void testStreamFilterByPrice() {
        warehouse.addProduct(new FoodProduct("F1", "Milk", 80, 15, LocalDate.now().plusDays(7)));
        warehouse.addProduct(new ElectronicsProduct("E1", "TV", 40000, 1, 12));
        warehouse.addProduct(new FoodProduct("F2", "Caviar", 5000, 2, LocalDate.now().plusDays(30)));

        List<AbstractProduct> expensive = warehouse.getProductsMoreExpensiveThan(1000);

        assertEquals(2, expensive.size());
        assertEquals("TV", expensive.get(0).getName());
    }

    @Test
    @DisplayName("Expired food detection")
    public void testExpiredFoodDetection() {
        FoodProduct expiredFood = new FoodProduct("F3", "Yogurt", 60, 5, LocalDate.now().minusDays(1));
        FoodProduct freshFood = new FoodProduct("F4", "Cheese", 400, 3, LocalDate.now().plusDays(10));

        warehouse.addProduct(expiredFood);
        warehouse.addProduct(freshFood);

        List<FoodProduct> expiredList = warehouse.getExpiredFoodProducts();

        assertEquals(1, expiredList.size());
        assertEquals("Yogurt", expiredList.get(0).getName());
    }


    @Test
    @DisplayName("Price filtering - valid equivalence classes")
    public void testPriceFilteringEquivalencePartitioning() {
        warehouse.addProduct(new FoodProduct("F1", "Cheap", 10, 5, LocalDate.now().plusDays(10)));
        warehouse.addProduct(new FoodProduct("F2", "Exact", 1000, 5, LocalDate.now().plusDays(10)));
        warehouse.addProduct(new ElectronicsProduct("E1", "Expensive", 5000, 2, 12));

        List<AbstractProduct> expensive = warehouse.getProductsMoreExpensiveThan(1000);

        assertEquals(1, expensive.size());
        assertEquals("Expensive", expensive.get(0).getName());
    }


    @Test
    @DisplayName("Expiration date - boundary values")
    public void testExpirationDateBoundaryValues() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);

        FoodProduct expiresToday = new FoodProduct("F1", "Today", 100, 5, today);
        FoodProduct expiresTomorrow = new FoodProduct("F2", "Tomorrow", 100, 5, tomorrow);
        FoodProduct expiresYesterday = new FoodProduct("F3", "Yesterday", 100, 5, yesterday);

        warehouse.addProduct(expiresToday);
        warehouse.addProduct(expiresTomorrow);
        warehouse.addProduct(expiresYesterday);

        List<FoodProduct> expired = warehouse.getExpiredFoodProducts();

        assertEquals(1, expired.size());
        assertEquals("Yesterday", expired.get(0).getName());
    }

    @Test
    @DisplayName("Quantity - boundary values")
    public void testQuantityBoundaryValues() {
        FoodProduct zeroQuantity = new FoodProduct("F1", "Zero", 100, 0, LocalDate.now().plusDays(10));
        FoodProduct oneQuantity = new FoodProduct("F2", "One", 100, 1, LocalDate.now().plusDays(10));
        FoodProduct negativeQuantity = new FoodProduct("F3", "Negative", 100, -5, LocalDate.now().plusDays(10));

        warehouse.addProduct(zeroQuantity);
        warehouse.addProduct(oneQuantity);
        warehouse.addProduct(negativeQuantity);

        assertEquals(3, warehouse.getAllProducts().size());
        assertEquals(0, warehouse.getProduct("F1").getQuantity());
        assertEquals(1, warehouse.getProduct("F2").getQuantity());
        assertEquals(-5, warehouse.getProduct("F3").getQuantity());
    }


    @Test
    @DisplayName("Error guessing - duplicate product ID")
    public void testDuplicateProductIdErrorGuessing() {
        FoodProduct first = new FoodProduct("F1", "Apple", 50, 10, LocalDate.now().plusDays(5));
        FoodProduct duplicate = new FoodProduct("F1", "Orange", 30, 15, LocalDate.now().plusDays(3));

        warehouse.addProduct(first);
        warehouse.addProduct(duplicate);

        assertEquals(1, warehouse.getAllProducts().size());
        assertEquals("Orange", warehouse.getProduct("F1").getName());
    }

    @Test
    @DisplayName("Error guessing - null values")
    public void testNullValuesErrorGuessing() {

        assertThrows(Exception.class, () -> {
            warehouse.addProduct(null);
        });
    }

    @Test
    @DisplayName("Error guessing - remove non-existent product")
    public void testRemoveNonExistentProduct() {
        assertDoesNotThrow(() -> {
            warehouse.removeProduct("NON_EXISTENT_ID");
        });

        assertEquals(0, warehouse.getAllProducts().size());
    }


    @Test
    @DisplayName("Pairwise - product combinations")
    public void testProductCombinationsPairwise() {

        warehouse.addProduct(new FoodProduct("F1", "Bread", 30, 2, LocalDate.now().plusDays(5)));
        warehouse.addProduct(new FoodProduct("F2", "Caviar", 5000, 50, LocalDate.now().plusDays(10)));
        warehouse.addProduct(new ElectronicsProduct("E1", "Cable", 20, 100, 6));
        warehouse.addProduct(new ElectronicsProduct("E2", "Laptop", 80000, 1, 24));

        List<AbstractProduct> expensive = warehouse.getProductsMoreExpensiveThan(1000);
        assertEquals(2, expensive.size());

        List<AbstractProduct> electronics = warehouse.getProductsByType("Electronics");
        assertEquals(2, electronics.size());
    }


    @Test
    @DisplayName("Functional Test: Complete warehouse operation scenario")
    public void testCompleteWarehouseScenario() {

        FoodProduct milk = new FoodProduct("M1", "Milk", 80, 20, LocalDate.now().plusDays(7));
        FoodProduct bread = new FoodProduct("B1", "Bread", 30, 15, LocalDate.now().plusDays(2));
        ElectronicsProduct phone = new ElectronicsProduct("P1", "Phone", 25000, 5, 12);

        warehouse.addProduct(milk);
        warehouse.addProduct(bread);
        warehouse.addProduct(phone);

        assertEquals(3, warehouse.getAllProducts().size());

        List<AbstractProduct> foodProducts = warehouse.getProductsByType("Food");
        assertEquals(2, foodProducts.size());

        List<AbstractProduct> electronicsProducts = warehouse.getProductsByType("Electronics");
        assertEquals(1, electronicsProducts.size());

        List<AbstractProduct> expensiveProducts = warehouse.getProductsMoreExpensiveThan(10000);
        assertEquals(1, expensiveProducts.size());
        assertEquals("Phone", expensiveProducts.get(0).getName());

        List<FoodProduct> expiredProducts = warehouse.getExpiredFoodProducts();
        assertEquals(0, expiredProducts.size());

        warehouse.removeProduct("B1");
        assertEquals(2, warehouse.getAllProducts().size());
        assertNull(warehouse.getProduct("B1"));

        ElectronicsProduct anotherPhone = new ElectronicsProduct("P1", "iPhone", 35000, 3, 24);
        warehouse.addProduct(anotherPhone);

        assertEquals(2, warehouse.getAllProducts().size());
        assertEquals("iPhone", warehouse.getProduct("P1").getName());
        assertEquals(35000, warehouse.getProduct("P1").getPrice());
    }


    @Test
    @DisplayName("Functional Test: Edge cases and stress scenarios")
    public void testEdgeAndStressScenarios() {
        for (int i = 0; i < 100; i++) {
            FoodProduct product = new FoodProduct("ID_" + i, "Product_" + i, i * 10, i, LocalDate.now().plusDays(i));
            warehouse.addProduct(product);
        }
        assertEquals(100, warehouse.getAllProducts().size());

        List<AbstractProduct> moreThan500 = warehouse.getProductsMoreExpensiveThan(500);
        assertEquals(49, moreThan500.size());

        List<AbstractProduct> moreThan490 = warehouse.getProductsMoreExpensiveThan(490);
        assertEquals(50, moreThan490.size());

        for (int i = 0; i < 100; i++) {
            warehouse.removeProduct("ID_" + i);
        }
        assertEquals(0, warehouse.getAllProducts().size());

        assertNull(warehouse.getProduct("ANY_ID"));
        assertDoesNotThrow(() -> warehouse.removeProduct("ANY_ID"));
        assertEquals(0, warehouse.getProductsByType("Food").size());
        assertEquals(0, warehouse.getProductsMoreExpensiveThan(0).size());
        assertEquals(0, warehouse.getExpiredFoodProducts().size());
    }
}