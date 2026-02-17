public class Courier {

    private final String id;
    private Point location;
    private CourierStatus status;
    private final TransportType transportType;
    private int completedOrdersToday;

    public Courier(String id, Point location, TransportType transportType) {
        this.id = id;
        this.location = location;
        this.status = CourierStatus.AVAILABLE;
        this.transportType = transportType;
        this.completedOrdersToday = 0;
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

    public int getCompletedOrdersToday() {
        return completedOrdersToday;
    }

    /**
     * Завершує поточне замовлення: збільшує лічильник та звільняє кур'єра.
     */
    public void completeOrder() {
        this.completedOrdersToday++;
        this.status = CourierStatus.AVAILABLE;
    }

    public void resetDailyStats() {
        this.completedOrdersToday = 0;
    }

    @Override
    public String toString() {
        return "Courier{" +
                "id='" + id + '\'' +
                ", location=" + location +
                ", status=" + status +
                ", transport=" + transportType +
                " (max " + transportType.getMaxWeight() + "kg)" +
                ", completedToday=" + completedOrdersToday +
                '}';
    }
}
