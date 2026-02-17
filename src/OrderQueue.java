import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;

/**
 * Черга замовлень. Якщо вільних кур'єрів немає — замовлення потрапляє в чергу.
 * При звільненні кур'єра можна спробувати призначити замовлення з черги.
 */
public class OrderQueue {

    private final Queue<Order> pendingOrders = new LinkedList<>();
    private final OrderAssignmentService service;

    public OrderQueue(OrderAssignmentService service) {
        this.service = service;
    }

    /**
     * Додає замовлення в чергу.
     */
    public void enqueue(Order order) {
        pendingOrders.add(order);
    }

    /**
     * Повертає кількість замовлень у черзі.
     */
    public int size() {
        return pendingOrders.size();
    }

    /**
     * Повертає true якщо черга порожня.
     */
    public boolean isEmpty() {
        return pendingOrders.isEmpty();
    }

    /**
     * Повертає копію черги для перегляду (без видалення).
     */
    public List<Order> getPendingOrders() {
        return new ArrayList<>(pendingOrders);
    }

    /**
     * Намагається призначити замовлення з черги доступним кур'єрам.
     * Повертає список пар (замовлення → кур'єр) які вдалося призначити.
     */
    public List<AssignmentResult> tryAssignPending(List<Courier> couriers) {
        List<AssignmentResult> results = new ArrayList<>();
        Queue<Order> stillPending = new LinkedList<>();

        while (!pendingOrders.isEmpty()) {
            Order order = pendingOrders.poll();
            Courier best = service.findBestCourierForOrder(order, couriers);
            if (best != null) {
                best.setStatus(CourierStatus.BUSY);
                results.add(new AssignmentResult(order, best));
            } else {
                stillPending.add(order);
            }
        }

        // Повертаємо непризначені назад у чергу
        pendingOrders.addAll(stillPending);
        return results;
    }

    /**
     * Очищає чергу.
     */
    public void clear() {
        pendingOrders.clear();
    }

    /**
     * Результат призначення замовлення з черги.
     */
    public static class AssignmentResult {
        private final Order order;
        private final Courier courier;

        public AssignmentResult(Order order, Courier courier) {
            this.order = order;
            this.courier = courier;
        }

        public Order getOrder() { return order; }
        public Courier getCourier() { return courier; }
    }
}
