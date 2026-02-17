import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Набір курʼєрів для тестів
        List<Courier> couriers = Arrays.asList(
                new Courier("C1", new Point(10, 10)),
                new Courier("C2", new Point(50, 50)),
                new Courier("C3", new Point(90, 90))
        );

        OrderAssignmentService service = new OrderAssignmentService();

        System.out.println("=== Тест 1: замовлення від закладу 'Shop A' ===");
        String result1 = service.assignOrderFromPlace("Shop A", new Point(12, 9), couriers);
        System.out.println(result1);

        System.out.println("=== Тест 2: ще одне замовлення від 'Shop B' ===");
        String result2 = service.assignOrderFromPlace("Shop B", new Point(80, 80), couriers);
        System.out.println(result2);

        System.out.println("=== Тест 3: всі курʼєри зайняті (може повернути 'No couriers available') ===");
        // Можна кілька разів викликати, поки всі не стануть BUSY
        String result3 = service.assignOrderFromPlace("Shop C", new Point(5, 5), couriers);
        System.out.println(result3);
    }
}
