import java.util.UUID;

public class Order {

    private final String id;
    private final Point placePoint;  // координати закладу (ресторан, кафе тощо)
    private final Point destination; // координати точки доставки замовника
    private final double weight;

    public Order(Point placePoint, Point destination, double weight) {
        this.id = UUID.randomUUID().toString();
        this.placePoint = placePoint;
        this.destination = destination;
        this.weight = weight;
    }

    public Order(String id, Point placePoint, Point destination, double weight) {
        this.id = id;
        this.placePoint = placePoint;
        this.destination = destination;
        this.weight = weight;
    }

    // Конструктор для сумісності зі старим кодом (placePoint = destination)
    public Order(Point destination, double weight) {
        this.id = UUID.randomUUID().toString();
        this.placePoint = destination;
        this.destination = destination;
        this.weight = weight;
    }

    public Order(String id, Point destination, double weight) {
        this.id = id;
        this.placePoint = destination;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public Point getPlacePoint() {
        return placePoint;
    }

    public Point getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", place=" + placePoint +
                ", destination=" + destination +
                ", weight=" + weight + "kg" +
                '}';
    }
}
