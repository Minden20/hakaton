import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAssignmentService {

    /**
     * Призначає кожне замовлення найближчому доступному курʼєру.
     * Якщо доступних курʼєрів немає, замовлення залишається без призначення.
     */
    public Map<Order, Courier> assignOrders(List<Order> orders, List<Courier> couriers) {
        Map<Order, Courier> result = new HashMap<>();
        if (orders == null || couriers == null) {
            return result;
        }

        // Копія списку курʼєрів, щоб потенційно змінювати їхній стан/локацію в майбутньому
        List<Courier> workingCouriers = new ArrayList<>(couriers);

        for (Order order : orders) {
            Courier best = findBestCourierForOrder(order, workingCouriers);
            if (best != null) {
                best.setStatus(CourierStatus.BUSY);
                result.put(order, best);
            }
        }

        return result;
    }

    /**
     * Обробляє одне замовлення від будь-якого закладу (ресторан, кафе, магазин тощо).
     *
     * @param placeName   назва закладу (для інформаційного виводу)
     * @param placePoint  координати закладу на сітці 100x100
     * @param couriers    список усіх курʼєрів
     * @return текстовий результат з інформацією про призначеного курʼєра
     *         або "No couriers available", якщо вільних немає
     */
    public String assignOrderFromPlace(String placeName, Point placePoint, List<Courier> couriers) {
        if (placePoint == null || !CityGrid.isValid(placePoint)) {
            return "Invalid place location";
        }
        if (couriers == null || couriers.isEmpty()) {
            return "No couriers available";
        }

        // Створюємо "технічне" замовлення, де destination = координати закладу
        Order technicalOrder = new Order(placeName, placePoint);

        Courier bestCourier = findBestCourierForOrder(technicalOrder, couriers);
        if (bestCourier == null) {
            return "No couriers available";
        }

        // Закріплюємо курʼєра за цим замовленням
        bestCourier.setStatus(CourierStatus.BUSY);
        int distance = CityGrid.distance(bestCourier.getLocation(), placePoint);

        return "Order from '" + placeName + "' assigned to courier '" + bestCourier.getId() +
                "' at distance " + distance;
    }

    /**
     * Повертає найближчого доступного курʼєра до точки доставки замовлення.
     */
    public Courier findBestCourierForOrder(Order order, List<Courier> couriers) {
        if (order == null || couriers == null || couriers.isEmpty()) {
            return null;
        }

        Point destination = order.getDestination();
        if (!CityGrid.isValid(destination)) {
            throw new IllegalArgumentException("Destination point is outside of 100x100 grid: " + destination);
        }

        Courier bestCourier = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Courier courier : couriers) {
            if (!courier.isAvailable()) {
                continue;
            }
            Point location = courier.getLocation();
            if (!CityGrid.isValid(location)) {
                continue;
            }
            int distance = CityGrid.distance(location, destination);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestCourier = courier;
            }
        }

        return bestCourier;
    }
}

