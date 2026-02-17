import java.util.UUID;

public class Order {

    private final String id;
    private final Point destination;

    public Order(Point destination) {
        this.id = UUID.randomUUID().toString();
        this.destination = destination;
    }

    public Order(String id, Point destination) {
        this.id = id;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public Point getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", destination=" + destination +
                '}';
    }
}

