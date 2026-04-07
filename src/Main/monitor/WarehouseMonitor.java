package Main.monitor;

import Main.core.Warehouse;
import Main.model.FoodProduct;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WarehouseMonitor {
    private final ScheduledExecutorService scheduler;
    private final Main.core.Warehouse warehouse;

    public WarehouseMonitor(Main.core.Warehouse warehouse) {
        this.warehouse = warehouse;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Expiration-Monitor");
            t.setDaemon(true);
            return t;
        });
    }

    public void startMonitoring() {
        scheduler.scheduleAtFixedRate(this::checkExpirations, 0, 30, TimeUnit.SECONDS);
    }

    private void checkExpirations() {
        List<FoodProduct> expired = warehouse.getExpiredFoodProducts();
        if (!expired.isEmpty()) {
            System.out.println("\n[МОНИТОРИНГ] Обнаружены просроченные продукты:");
            expired.forEach(p -> System.out.println(" - " + p.getName() + " (ID: " + p.getId() + ")"));
            System.out.print("Введите команду: ");
        }
    }

    public void stopMonitoring() {
        scheduler.shutdown();
    }
}