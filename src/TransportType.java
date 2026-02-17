public enum TransportType {

    FOOT(5.0),
    BICYCLE(15.0),
    CAR_SCOOTER(50.0);

    private final double maxWeight;

    TransportType(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }
}
