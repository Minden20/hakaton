public class Courier {

    private final String id;
    private Point location;
    private CourierStatus status;

    public Courier(String id, Point location) {
        this.id = id;
        this.location = location;
        this.status = CourierStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public CourierStatus getStatus() {
        return status;
    }

    public void setStatus(CourierStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == CourierStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id='" + id + '\'' +
                ", location=" + location +
                ", status=" + status +
                '}';
    }
}

