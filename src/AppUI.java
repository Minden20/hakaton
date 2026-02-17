import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AppUI extends JFrame {

    private final List<Courier> couriers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<String> assignmentLog = new ArrayList<>();
    private final java.util.Map<Order, Courier> assignments = new java.util.LinkedHashMap<>();

    private final OrderAssignmentService service = new OrderAssignmentService();

    private DefaultTableModel courierTableModel;
    private DefaultTableModel orderTableModel;
    private JTextArea logArea;
    private CityPanel cityPanel;

    public AppUI() {
        super("Courier Delivery System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        initCouriers();
        initUI();
    }

    private void initCouriers() {
        couriers.add(new Courier("C1", new Point(10, 10), TransportType.FOOT));
        couriers.add(new Courier("C2", new Point(50, 50), TransportType.BICYCLE));
        couriers.add(new Courier("C3", new Point(90, 90), TransportType.CAR_SCOOTER));
        couriers.add(new Courier("C4", new Point(30, 70), TransportType.FOOT));
        couriers.add(new Courier("C5", new Point(70, 30), TransportType.BICYCLE));
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(5, 5));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.setBackground(new Color(30, 30, 30));

        // --- Left: tables + form ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setOpaque(false);

        // Courier table
        courierTableModel = new DefaultTableModel(
                new String[]{"ID", "Локація", "Транспорт", "Макс. вага", "Статус"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable courierTable = createStyledTable(courierTableModel);
        JScrollPane courierScroll = new JScrollPane(courierTable);
        courierScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Кур'єри", 0, 0, null, new Color(200, 200, 200)));
        courierScroll.setPreferredSize(new Dimension(450, 170));

        // Order table
        orderTableModel = new DefaultTableModel(
                new String[]{"ID", "Точка", "Вага (кг)", "Кур'єр"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable orderTable = createStyledTable(orderTableModel);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Замовлення", 0, 0, null, new Color(200, 200, 200)));
        orderScroll.setPreferredSize(new Dimension(450, 170));

        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        tablesPanel.setOpaque(false);
        tablesPanel.add(courierScroll);
        tablesPanel.add(orderScroll);

        leftPanel.add(tablesPanel, BorderLayout.CENTER);

        // --- Form panel ---
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Нове замовлення", 0, 0, null, new Color(200, 200, 200)));

        JLabel lblX = styledLabel("X:");
        JTextField tfX = styledField(4);
        JLabel lblY = styledLabel("Y:");
        JTextField tfY = styledField(4);
        JLabel lblW = styledLabel("Вага (кг):");
        JTextField tfW = styledField(5);

        JButton btnAdd = new JButton("Створити");
        btnAdd.setBackground(new Color(46, 125, 50));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnReset = new JButton("Скинути все");
        btnReset.setBackground(new Color(198, 40, 40));
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        formPanel.add(lblX); formPanel.add(tfX);
        formPanel.add(lblY); formPanel.add(tfY);
        formPanel.add(lblW); formPanel.add(tfW);
        formPanel.add(btnAdd);
        formPanel.add(btnReset);

        leftPanel.add(formPanel, BorderLayout.SOUTH);

        // --- Right: city grid + log ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 5));
        rightPanel.setOpaque(false);

        cityPanel = new CityPanel();
        cityPanel.setPreferredSize(new Dimension(500, 500));
        cityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Карта міста (100×100)", 0, 0, null, new Color(200, 200, 200)));
        rightPanel.add(cityPanel, BorderLayout.CENTER);

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        logArea.setBackground(new Color(25, 25, 25));
        logArea.setForeground(new Color(160, 220, 160));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Лог", 0, 0, null, new Color(200, 200, 200)));
        logScroll.setPreferredSize(new Dimension(500, 140));
        rightPanel.add(logScroll, BorderLayout.SOUTH);

        // --- Combine ---
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(450);
        split.setOpaque(false);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        setContentPane(root);
        refreshCourierTable();

        // --- Button actions ---
        btnAdd.addActionListener(e -> {
            try {
                int x = Integer.parseInt(tfX.getText().trim());
                int y = Integer.parseInt(tfY.getText().trim());
                double w = Double.parseDouble(tfW.getText().trim());
                if (x < 0 || x >= 100 || y < 0 || y >= 100) {
                    log("Помилка: координати мають бути 0-99");
                    return;
                }
                if (w <= 0) {
                    log("Помилка: вага має бути > 0");
                    return;
                }
                createOrder(x, y, w);
                tfX.setText(""); tfY.setText(""); tfW.setText("");
            } catch (NumberFormatException ex) {
                log("Помилка: введіть коректні числа");
            }
        });

        btnReset.addActionListener(e -> resetAll());
    }

    private void createOrder(int x, int y, double weight) {
        Point dest = new Point(x, y);
        Order order = new Order(dest, weight);
        orders.add(order);

        Courier best = service.findBestCourierForOrder(order, couriers);
        if (best != null) {
            best.setStatus(CourierStatus.BUSY);
            assignments.put(order, best);
            int dist = CityGrid.distance(best.getLocation(), dest);
            log("Замовлення " + shortId(order.getId()) + " (" + weight + "кг) → кур'єр " +
                    best.getId() + " [" + transportLabel(best.getTransportType()) + "], відстань: " + dist);
        } else {
            log("Замовлення " + shortId(order.getId()) + " (" + weight + "кг) — жоден кур'єр не підходить!");
        }

        refreshCourierTable();
        refreshOrderTable();
        cityPanel.repaint();
    }

    private void resetAll() {
        orders.clear();
        assignments.clear();
        assignmentLog.clear();
        logArea.setText("");
        for (Courier c : couriers) {
            c.setStatus(CourierStatus.AVAILABLE);
        }
        refreshCourierTable();
        refreshOrderTable();
        cityPanel.repaint();
        log("Система скинута — всі кур'єри вільні");
    }

    private void refreshCourierTable() {
        courierTableModel.setRowCount(0);
        for (Courier c : couriers) {
            courierTableModel.addRow(new Object[]{
                    c.getId(),
                    c.getLocation().toString(),
                    transportLabel(c.getTransportType()),
                    c.getTransportType().getMaxWeight() + " кг",
                    c.isAvailable() ? "Вільний" : "Зайнятий"
            });
        }
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        for (Order o : orders) {
            Courier assigned = assignments.get(o);
            orderTableModel.addRow(new Object[]{
                    shortId(o.getId()),
                    o.getDestination().toString(),
                    o.getWeight(),
                    assigned != null ? assigned.getId() + " [" + transportLabel(assigned.getTransportType()) + "]" : "—"
            });
        }
    }

    private void log(String msg) {
        assignmentLog.add(msg);
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private String shortId(String id) {
        return id.length() > 8 ? id.substring(0, 8) : id;
    }

    private String transportLabel(TransportType t) {
        switch (t) {
            case FOOT: return "Пішки";
            case BICYCLE: return "Велосипед";
            case CAR_SCOOTER: return "Машина/Скутер";
            default: return t.name();
        }
    }

    // --- Styled helpers ---
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(new Color(35, 35, 35));
        table.setForeground(new Color(220, 220, 220));
        table.setGridColor(new Color(60, 60, 60));
        table.setSelectionBackground(new Color(55, 90, 130));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setBackground(new Color(50, 50, 50));
        table.getTableHeader().setForeground(new Color(200, 200, 200));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        return table;
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }

    private JTextField styledField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(new Color(50, 50, 50));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        return tf;
    }

    // --- City visualization panel ---
    private class CityPanel extends JPanel {
        CityPanel() {
            setBackground(new Color(20, 20, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets ins = getInsets();
            int w = getWidth() - ins.left - ins.right - 20;
            int h = getHeight() - ins.top - ins.bottom - 20;
            int offX = ins.left + 10;
            int offY = ins.top + 10;

            double scaleX = w / 100.0;
            double scaleY = h / 100.0;

            // Grid lines
            g2.setColor(new Color(40, 40, 55));
            for (int i = 0; i <= 100; i += 10) {
                int px = offX + (int)(i * scaleX);
                int py = offY + (int)(i * scaleY);
                g2.drawLine(px, offY, px, offY + h);
                g2.drawLine(offX, py, offX + w, py);
            }

            // Grid labels
            g2.setColor(new Color(100, 100, 120));
            g2.setFont(new Font("Consolas", Font.PLAIN, 10));
            for (int i = 0; i <= 100; i += 10) {
                int px = offX + (int)(i * scaleX);
                int py = offY + (int)(i * scaleY);
                g2.drawString(String.valueOf(i), px - 5, offY + h + 14);
                g2.drawString(String.valueOf(i), offX - 22, py + 4);
            }

            // Draw assignment lines
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{6, 4}, 0));
            for (java.util.Map.Entry<Order, Courier> entry : assignments.entrySet()) {
                Order o = entry.getKey();
                Courier c = entry.getValue();
                int cx = offX + (int)(c.getLocation().getX() * scaleX);
                int cy = offY + (int)(c.getLocation().getY() * scaleY);
                int ox = offX + (int)(o.getDestination().getX() * scaleX);
                int oy = offY + (int)(o.getDestination().getY() * scaleY);
                g2.setColor(new Color(255, 215, 0, 100));
                g2.drawLine(cx, cy, ox, oy);
            }
            g2.setStroke(new BasicStroke(1));

            // Draw orders (red diamonds)
            for (Order o : orders) {
                int ox = offX + (int)(o.getDestination().getX() * scaleX);
                int oy = offY + (int)(o.getDestination().getY() * scaleY);
                Courier assigned = assignments.get(o);

                if (assigned != null) {
                    g2.setColor(new Color(76, 175, 80));
                } else {
                    g2.setColor(new Color(244, 67, 54));
                }
                int[] xp = {ox, ox + 7, ox, ox - 7};
                int[] yp = {oy - 7, oy, oy + 7, oy};
                g2.fillPolygon(xp, yp, 4);
                g2.setColor(Color.WHITE);
                g2.drawPolygon(xp, yp, 4);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(255, 200, 200));
                g2.drawString(o.getWeight() + "кг", ox + 10, oy + 4);
            }

            // Draw couriers (colored circles)
            for (Courier c : couriers) {
                int cx = offX + (int)(c.getLocation().getX() * scaleX);
                int cy = offY + (int)(c.getLocation().getY() * scaleY);
                int r = 10;

                Color col;
                switch (c.getTransportType()) {
                    case FOOT: col = new Color(100, 181, 246); break;
                    case BICYCLE: col = new Color(255, 183, 77); break;
                    case CAR_SCOOTER: col = new Color(186, 104, 200); break;
                    default: col = Color.GRAY;
                }

                if (!c.isAvailable()) {
                    col = col.darker().darker();
                }

                g2.setColor(col);
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(Color.WHITE);
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String label = c.getId();
                g2.drawString(label, cx - fm.stringWidth(label) / 2, cy + 4);
            }

            // Legend
            int lx = offX + 5;
            int ly = offY + h - 70;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            g2.setColor(new Color(100, 181, 246));
            g2.fillOval(lx, ly, 10, 10);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Пішки (≤5кг)", lx + 15, ly + 10);

            g2.setColor(new Color(255, 183, 77));
            g2.fillOval(lx, ly + 16, 10, 10);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Велосипед (≤15кг)", lx + 15, ly + 26);

            g2.setColor(new Color(186, 104, 200));
            g2.fillOval(lx, ly + 32, 10, 10);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Машина/Скутер (≤50кг)", lx + 15, ly + 42);

            g2.setColor(new Color(76, 175, 80));
            int[] dx = {lx + 5, lx + 10, lx + 5, lx};
            int[] dy = {ly + 45, ly + 50, ly + 55, ly + 50};
            g2.fillPolygon(dx, dy, 4);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Замовлення (призначене)", lx + 15, ly + 55);

            g2.setColor(new Color(244, 67, 54));
            int[] dx2 = {lx + 5, lx + 10, lx + 5, lx};
            int[] dy2 = {ly + 58, ly + 63, ly + 68, ly + 63};
            g2.fillPolygon(dx2, dy2, 4);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Замовлення (без кур'єра)", lx + 15, ly + 68);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new AppUI().setVisible(true);
        });
    }
}
