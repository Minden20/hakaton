import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        // Приклад: 100x100 місто, кілька курʼєрів і замовлень
        List<Courier> couriers = Arrays.asList(
                new Courier("C1", new Point(10, 10)),
                new Courier("C2", new Point(50, 50)),
                new Courier("C3", new Point(90, 90))
        );

        List<Order> orders = Arrays.asList(
                new Order("O1", new Point(12, 9)),
                new Order("O2", new Point(48, 52)),
                new Order("O3", new Point(80, 80)),
                new Order("O4", new Point(5, 5)) // одне замовлення залишиться без курʼєра, якщо всі вже будуть зайняті
        );

        OrderAssignmentService service = new OrderAssignmentService();
        Map<Order, Courier> assignments = service.assignOrders(orders, couriers);

        System.out.println("Результати розподілу замовлень:");
        for (Order order : orders) {
            Courier courier = assignments.get(order);
            if (courier != null) {
                System.out.printf("Замовлення %s -> курʼєр %s (відстань %d)%n",
                        order.getId(),
                        courier.getId(),
                        CityGrid.distance(courier.getLocation(), order.getDestination()));
            } else {
                System.out.printf("Замовлення %s -> немає доступного курʼєра%n", order.getId());
            }
        }
    }
}
