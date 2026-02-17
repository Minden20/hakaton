public class Courier {

    private final String id;
    private Point location;
    private CourierStatus status;
    private final TransportType transportType;

    public Courier(String id, Point location, TransportType transportType) {
        this.id = id;
        this.location = location;
        this.status = CourierStatus.AVAILABLE;
        this.transportType = transportType;
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

    public TransportType getTransportType() {
        return transportType;
    }

    public boolean isAvailable() {
        return status == CourierStatus.AVAILABLE;
    }

    public boolean canCarry(double weight) {
        return weight <= transportType.getMaxWeight();
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id='" + id + '\'' +
                ", location=" + location +
                ", status=" + status +
                ", transport=" + transportType +
                " (max " + transportType.getMaxWeight() + "kg)" +
                '}';
    }
}
