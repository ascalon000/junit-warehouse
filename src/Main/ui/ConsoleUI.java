package Main.ui;

import Main.core.Warehouse;
import Main.model.AbstractProduct;
import Main.model.ElectronicsProduct;
import Main.model.FoodProduct;
import Main.monitor.WarehouseMonitor;
import Main.strategy.JsonStorageStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public class ConsoleUI {
    private final Warehouse warehouse;
    private final WarehouseMonitor monitor;
    private final Scanner scanner;
    private final String DATA_FILE = "warehouse_data.json";

    public ConsoleUI() {
        this.warehouse = Warehouse.getInstance();
        this.warehouse.setStorageStrategy(new JsonStorageStrategy());
        this.monitor = new WarehouseMonitor(warehouse);
        this.scanner = new Scanner(in);
    }

    public void start() {
        try {
            warehouse.loadData(DATA_FILE);
        } catch (Exception e) {
            out.println("Стартовый файл данных не найден, создана новая база.");
        }

        monitor.startMonitoring();
        out.println("Warehouse master:");

        while (true) {
            out.println("\n1. Добавить еду");
            out.println("2. Добавить электронику");
            out.println("3. Показать все товары");
            out.println("4. Найти товар по имени");
            out.println("5. Показать товары дороже указанной цены");
            out.println("6. Фильтр по типу");
            out.println("7. Удалить товар");
            out.println("0. Выход");
            out.print("Выберите опцию: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> addFoodProduct();
                    case 2 -> addElectronicsProduct();
                    case 3 -> showAllProducts();
                    case 4 -> searchByName();
                    case 5 -> filterByPrice();
                    case 6 -> filterByType();
                    case 7 -> removeProduct();
                    case 0 -> exit();
                    default -> out.println("Неверный пункт меню.");
                }
            } catch (NumberFormatException e) {
                out.println("Ошибка: Введите число, а не строку!");
            } catch (Exception e) {
                out.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private void addFoodProduct() {
        try {
            out.print("ID: "); String id = scanner.nextLine();
            out.print("Название: "); String name = scanner.nextLine();
            out.print("Цена: "); double price = Double.parseDouble(scanner.nextLine());
            out.print("Количество: "); int qty = Integer.parseInt(scanner.nextLine());
            out.print("Срок годности (ГГГГ-ММ-ДД): ");
            LocalDate expDate = LocalDate.parse(scanner.nextLine());

            warehouse.addProduct(new FoodProduct(id, name, price, qty, expDate));
            out.println("Товар добавлен.");
        } catch (NumberFormatException e) {
            out.println("Ошибка: Неверный формат числа!");
        } catch (Exception e) {
            out.println("Ошибка ввода данных: " + e.getMessage());
        }
    }

    private void addElectronicsProduct() {
        try {
            out.print("ID: "); String id = scanner.nextLine();
            out.print("Название: "); String name = scanner.nextLine();
            out.print("Цена: "); double price = Double.parseDouble(scanner.nextLine());
            out.print("Количество: "); int qty = Integer.parseInt(scanner.nextLine());
            out.print("Гарантия (мес.): "); int warranty = Integer.parseInt(scanner.nextLine());

            warehouse.addProduct(new ElectronicsProduct(id, name, price, qty, warranty));
            out.println("Товар добавлен.");
        } catch (NumberFormatException e) {
            out.println("Ошибка: Неверный формат числа!");
        }
    }

    private void showAllProducts() {
        if (warehouse.getAllProducts().isEmpty()) {
            out.println("Склад пуст.");
            return;
        }
        out.println("Список товаров:");
        warehouse.getAllProducts().values().forEach(out::println);
    }

    private void searchByName() {
        out.print("Введите часть названия: ");
        String query = scanner.nextLine();
        List<AbstractProduct> result = warehouse.searchByName(query);
        if (result.isEmpty()) out.println("Ничего не найдено.");
        else result.forEach(out::println);
    }

    private void filterByPrice() {
        out.print("Введите минимальную цену: ");
        try {
            double price = Double.parseDouble(scanner.nextLine());
            List<AbstractProduct> result = warehouse.getProductsMoreExpensiveThan(price);
            if (result.isEmpty()) out.println("Нет товаров дороже " + price);
            else result.forEach(out::println);
        } catch (NumberFormatException e) {
            out.println("Ошибка: Введите число!");
        }
    }

    private void filterByType() {
        out.print("Введите тип (Food или Electronics): ");
        String type = scanner.nextLine();
        List<AbstractProduct> result = warehouse.getProductsByType(type);
        if (result.isEmpty()) out.println("Товары такого типа не найдены.");
        else result.forEach(out::println);
    }

    private void removeProduct() {
        out.print("Введите ID товара для удаления: ");
        String id = scanner.nextLine();
        if (warehouse.getProduct(id) != null) {
            warehouse.removeProduct(id);
            out.println("Товар удален.");
        } else {
            out.println("Товар с таким ID не найден.");
        }
    }

    private void exit() {
        try {
            warehouse.saveData(DATA_FILE);
            out.println("Данные сохранены в " + DATA_FILE);
        } catch (Exception e) {
            out.println("Ошибка при сохранении данных!");
        }
        monitor.stopMonitoring();
        out.println("Выход из программы...");
        System.exit(0);
    }
}