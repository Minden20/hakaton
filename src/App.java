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
        OrderQueue queue = new OrderQueue(service);

        // Тест 1: легке замовлення (3 кг)
        System.out.println("=== Тест 1: легке замовлення 3кг від 'Shop A' ===");
        String result1 = service.assignOrderFromPlace("Shop A", new Point(12, 9), new Point(15, 12), 3.0, couriers);
        System.out.println(result1);
        System.out.println();

        // Тест 2: середнє замовлення (12 кг)
        System.out.println("=== Тест 2: середнє замовлення 12кг від 'Shop B' ===");
        String result2 = service.assignOrderFromPlace("Shop B", new Point(55, 55), new Point(60, 60), 12.0, couriers);
        System.out.println(result2);
        System.out.println();

        // Тест 3: важке замовлення (40 кг)
        System.out.println("=== Тест 3: важке замовлення 40кг від 'Shop C' ===");
        String result3 = service.assignOrderFromPlace("Shop C", new Point(80, 80), new Point(85, 85), 40.0, couriers);
        System.out.println(result3);
        System.out.println();

        // Тест 4: всі зайняті — замовлення йде в чергу
        System.out.println("=== Тест 4: всі кур'єри зайняті → черга ===");
        Order queuedOrder = new Order(new Point(20, 20), new Point(25, 25), 4.0);
        Courier best = service.findBestCourierForOrder(queuedOrder, couriers);
        if (best == null) {
            queue.enqueue(queuedOrder);
            System.out.println("Замовлення додано в чергу. Розмір черги: " + queue.size());
        }
        System.out.println();

        // Тест 5: кур'єр C1 завершує замовлення → перевіряємо чергу
        System.out.println("=== Тест 5: C1 завершує замовлення → обробка черги ===");
        couriers.get(0).completeOrder(); // C1 стає AVAILABLE, completedOrdersToday = 1
        System.out.println("C1 після завершення: " + couriers.get(0));

        List<OrderQueue.AssignmentResult> fromQueue = queue.tryAssignPending(couriers);
        for (OrderQueue.AssignmentResult ar : fromQueue) {
            int dist = CityGrid.distance(ar.getCourier().getLocation(), ar.getOrder().getDestination());
            System.out.println("Із черги: замовлення → кур'єр " + ar.getCourier().getId() +
                    " [" + ar.getCourier().getTransportType() + "], відстань: " + dist);
        }
        System.out.println("Залишок у черзі: " + queue.size());
        System.out.println();

        // Тест 6: пріоритет — C1 має 1 виконане, C2 має 0 → при однаковій відстані C2 виграє
        System.out.println("=== Тест 6: пріоритет (C1 completedToday=1+, C2 completedToday=0) ===");
        couriers.get(0).completeOrder(); // C1: completedToday = 2
        couriers.get(1).completeOrder(); // C2: completedToday = 1, стає AVAILABLE
        System.out.println("C1: " + couriers.get(0));
        System.out.println("C2: " + couriers.get(1));

        // Замовлення на рівній відстані від C1 та C2 — має обрати C2 (менше виконаних)
        // C1 (10,10), C2 (50,50), destination (30,30): dist_C1 = 40, dist_C2 = 40
        Order priorityOrder = new Order(new Point(30, 30), new Point(30, 30), 5.0);
        Courier chosen = service.findBestCourierForOrder(priorityOrder, couriers);
        if (chosen != null) {
            System.out.println("Обрано кур'єра: " + chosen.getId() +
                    " (completedToday=" + chosen.getCompletedOrdersToday() + ")");
        }
    }
}
