import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAssignmentService {

    /**
     * Призначає кожне замовлення найближчому доступному курʼєру,
     * який може перевезти замовлення за вагою.
     * Якщо доступних курʼєрів немає, замовлення залишається без призначення.
     */
    public Map<Order, Courier> assignOrders(List<Order> orders, List<Courier> couriers) {
        Map<Order, Courier> result = new HashMap<>();
        if (orders == null || couriers == null) {
            return result;
        }

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
     * Кур'єр вибирається найближчий до точки доставки замовника (destination).
     *
     * @param placeName   назва закладу (для інформаційного виводу)
     * @param placePoint  координати закладу на сітці 100x100
     * @param destination координати точки доставки замовника
     * @param weight      вага замовлення в кг
     * @param couriers    список усіх курʼєрів
     * @return текстовий результат з інформацією про призначеного курʼєра
     *         або повідомлення про помилку
     */
    public String assignOrderFromPlace(String placeName, Point placePoint, Point destination, double weight, List<Courier> couriers) {
        if (placePoint == null || !CityGrid.isValid(placePoint)) {
            return "Invalid place location";
        }
        if (destination == null || !CityGrid.isValid(destination)) {
            return "Invalid destination location";
        }
        if (couriers == null || couriers.isEmpty()) {
            return "No couriers available";
        }

        Order order = new Order(placeName, placePoint, destination, weight);

        Courier bestCourier = findBestCourierForOrder(order, couriers);
        if (bestCourier == null) {
            return "No couriers available for order '" + placeName + "' (weight: " + weight + "kg)";
        }

        bestCourier.setStatus(CourierStatus.BUSY);
        // Відстань від кур'єра до точки доставки замовника
        int distanceToDestination = CityGrid.distance(bestCourier.getLocation(), destination);
        // Відстань від закладу до точки доставки (для інформації)
        int distancePlaceToDestination = CityGrid.distance(placePoint, destination);

        return "Order from '" + placeName + "' (" + weight + "kg) assigned to courier '" + bestCourier.getId() +
                "' [" + bestCourier.getTransportType() + ", max " + bestCourier.getTransportType().getMaxWeight() + "kg]" +
                " | Distance: courier→destination=" + distanceToDestination +
                ", place→destination=" + distancePlaceToDestination;
    }

    /**
     * Повертає найближчого доступного курʼєра до точки доставки замовлення,
     * який здатний перевезти замовлення за вагою.
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
            if (!courier.canCarry(order.getWeight())) {
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
