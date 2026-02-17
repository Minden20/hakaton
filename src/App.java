import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Кур'єри з різним транспортом
        List<Courier> couriers = Arrays.asList(
                new Courier("C1", new Point(10, 10), TransportType.FOOT),
                new Courier("C2", new Point(50, 50), TransportType.BICYCLE),
                new Courier("C3", new Point(90, 90), TransportType.CAR_SCOOTER)
        );

        System.out.println("=== Кур'єри ===");
        for (Courier c : couriers) {
            System.out.println(c);
        }
        System.out.println();

        OrderAssignmentService service = new OrderAssignmentService();

        // Тест 1: легке замовлення (3 кг) від Shop A (12,9) до клієнта (15,12)
        // Найближчий кур'єр до клієнта (15,12) - C1 (10,10), відстань = 7
        System.out.println("=== Тест 1: легке замовлення 3кг від 'Shop A' (12,9) до клієнта (15,12) ===");
        String result1 = service.assignOrderFromPlace("Shop A", new Point(12, 9), new Point(15, 12), 3.0, couriers);
        System.out.println(result1);
        System.out.println();

        // Тест 2: середнє замовлення (12 кг) від Shop B (55,55) до клієнта (60,60)
        // Найближчий кур'єр до клієнта (60,60) - C2 (50,50), відстань = 20
        System.out.println("=== Тест 2: середнє замовлення 12кг від 'Shop B' (55,55) до клієнта (60,60) ===");
        String result2 = service.assignOrderFromPlace("Shop B", new Point(55, 55), new Point(60, 60), 12.0, couriers);
        System.out.println(result2);
        System.out.println();

        // Тест 3: важке замовлення (40 кг) від Shop C (80,80) до клієнта (85,85)
        // Найближчий кур'єр до клієнта (85,85) - C3 (90,90), відстань = 10
        System.out.println("=== Тест 3: важке замовлення 40кг від 'Shop C' (80,80) до клієнта (85,85) ===");
        String result3 = service.assignOrderFromPlace("Shop C", new Point(80, 80), new Point(85, 85), 40.0, couriers);
        System.out.println(result3);
        System.out.println();

        // Тест 4: занадто важке замовлення (60 кг) — жоден не підходить
        System.out.println("=== Тест 4: надважке замовлення 60кг від 'Shop D' (50,50) до клієнта (55,55) ===");
        String result4 = service.assignOrderFromPlace("Shop D", new Point(50, 50), new Point(55, 55), 60.0, couriers);
        System.out.println(result4);
        System.out.println();

        // Тест 5: замовлення 5кг — C1 вже BUSY, найближчий до клієнта (20,20) - C2 (50,50), відстань = 60
        System.out.println("=== Тест 5: замовлення 5кг від 'Shop E' (10,10) до клієнта (20,20) (C1 вже зайнятий) ===");
        String result5 = service.assignOrderFromPlace("Shop E", new Point(10, 10), new Point(20, 20), 5.0, couriers);
        System.out.println(result5);
    }
}
