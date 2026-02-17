public final class CityGrid {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;

    private CityGrid() {
        // utility class
    }

    public static boolean isValid(Point point) {
        if (point == null) {
            return false;
        }
        int x = point.getX();
        int y = point.getY();
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    /**
     * Обчислює мангеттенську відстань між двома точками на сітці.
     */
    public static int distance(Point a, Point b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Points must not be null");
        }
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
}

