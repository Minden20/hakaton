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
    private final OrderQueue orderQueue;

    private DefaultTableModel courierTableModel;
    private DefaultTableModel orderTableModel;
    private DefaultTableModel queueTableModel;
    private JTextArea logArea;
    private CityPanel cityPanel;
    private JLabel queueCountLabel;

    public AppUI() {
        super("Courier Delivery System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        this.orderQueue = new OrderQueue(service);

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
                new String[]{"ID", "Локація", "Транспорт", "Макс. вага", "Статус", "Виконано"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable courierTable = createStyledTable(courierTableModel);
        JScrollPane courierScroll = new JScrollPane(courierTable);
        courierScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Кур'єри", 0, 0, null, new Color(200, 200, 200)));
        courierScroll.setPreferredSize(new Dimension(500, 150));

        // Order table
        orderTableModel = new DefaultTableModel(
                new String[]{"ID", "Звідки", "Куди", "Вага (кг)", "Кур'єр"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable orderTable = createStyledTable(orderTableModel);
        JScrollPane orderScroll = new JScrollPane(orderTable);
        orderScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Замовлення", 0, 0, null, new Color(200, 200, 200)));
        orderScroll.setPreferredSize(new Dimension(500, 130));

        // Queue table
        queueTableModel = new DefaultTableModel(
                new String[]{"ID", "Куди", "Вага (кг)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable queueTable = createStyledTable(queueTableModel);
        JScrollPane queueScroll = new JScrollPane(queueTable);
        queueCountLabel = styledLabel("Черга: 0");
        queueCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        queueCountLabel.setForeground(new Color(255, 183, 77));

        JPanel queueHeader = new JPanel(new BorderLayout());
        queueHeader.setOpaque(false);
        queueHeader.add(queueCountLabel, BorderLayout.WEST);

        JPanel queuePanel = new JPanel(new BorderLayout(0, 2));
        queuePanel.setOpaque(false);
        queuePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Черга замовлень", 0, 0, null, new Color(200, 200, 200)));
        queuePanel.add(queueScroll, BorderLayout.CENTER);
        queuePanel.setPreferredSize(new Dimension(500, 100));

        JPanel tablesPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        tablesPanel.setOpaque(false);
        tablesPanel.add(courierScroll);
        tablesPanel.add(orderScroll);
        tablesPanel.add(queuePanel);

        leftPanel.add(tablesPanel, BorderLayout.CENTER);

        // --- Form panel ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(40, 40, 40));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                "Керування", 0, 0, null, new Color(200, 200, 200)));

        // Row 1: new order
        JPanel orderFormRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        orderFormRow.setOpaque(false);

        JLabel lblPX = styledLabel("Звідки X:");
        JTextField tfPX = styledField(3);
        JLabel lblPY = styledLabel("Y:");
        JTextField tfPY = styledField(3);
        JLabel lblDX = styledLabel("Куди X:");
        JTextField tfDX = styledField(3);
        JLabel lblDY = styledLabel("Y:");
        JTextField tfDY = styledField(3);
        JLabel lblW = styledLabel("Вага:");
        JTextField tfW = styledField(4);

        JButton btnAdd = createButton("Створити", new Color(46, 125, 50));

        orderFormRow.add(lblPX); orderFormRow.add(tfPX);
        orderFormRow.add(lblPY); orderFormRow.add(tfPY);
        orderFormRow.add(lblDX); orderFormRow.add(tfDX);
        orderFormRow.add(lblDY); orderFormRow.add(tfDY);
        orderFormRow.add(lblW); orderFormRow.add(tfW);
        orderFormRow.add(btnAdd);

        // Row 2: actions
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        actionRow.setOpaque(false);

        JLabel lblCourierId = styledLabel("ID кур'єра:");
        JTextField tfCourierId = styledField(4);
        JButton btnComplete = createButton("Завершити замовлення", new Color(33, 150, 243));
        JButton btnReset = createButton("Скинути все", new Color(198, 40, 40));

        actionRow.add(lblCourierId); actionRow.add(tfCourierId);
        actionRow.add(btnComplete);
        actionRow.add(btnReset);

        formPanel.add(orderFormRow);
        formPanel.add(actionRow);

        leftPanel.add(formPanel, BorderLayout.SOUTH);

        // --- Right: city grid + log ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 5));
        rightPanel.setOpaque(false);

        cityPanel = new CityPanel();
        cityPanel.setPreferredSize(new Dimension(550, 550));
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
        logScroll.setPreferredSize(new Dimension(550, 140));
        rightPanel.add(logScroll, BorderLayout.SOUTH);

        // --- Combine ---
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(500);
        split.setOpaque(false);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        setContentPane(root);
        refreshAll();

        // --- Button actions ---
        btnAdd.addActionListener(e -> {
            try {
                int px = Integer.parseInt(tfPX.getText().trim());
                int py = Integer.parseInt(tfPY.getText().trim());
                int dx = Integer.parseInt(tfDX.getText().trim());
                int dy = Integer.parseInt(tfDY.getText().trim());
                double w = Double.parseDouble(tfW.getText().trim());
                if (px < 0 || px >= 100 || py < 0 || py >= 100 ||
                    dx < 0 || dx >= 100 || dy < 0 || dy >= 100) {
                    log("Помилка: координати мають бути 0-99");
                    return;
                }
                if (w <= 0) {
                    log("Помилка: вага має бути > 0");
                    return;
                }
                createOrder(px, py, dx, dy, w);
                tfPX.setText(""); tfPY.setText("");
                tfDX.setText(""); tfDY.setText("");
                tfW.setText("");
            } catch (NumberFormatException ex) {
                log("Помилка: введіть коректні числа");
            }
        });

        btnComplete.addActionListener(e -> {
            String id = tfCourierId.getText().trim();
            if (id.isEmpty()) {
                log("Помилка: введіть ID кур'єра");
                return;
            }
            completeOrderForCourier(id);
            tfCourierId.setText("");
        });

        btnReset.addActionListener(e -> resetAll());
    }

    private void createOrder(int px, int py, int dx, int dy, double weight) {
        Point place = new Point(px, py);
        Point dest = new Point(dx, dy);
        Order order = new Order(place, dest, weight);
        orders.add(order);

        Courier best = service.findBestCourierForOrder(order, couriers);
        if (best != null) {
            best.setStatus(CourierStatus.BUSY);
            assignments.put(order, best);
            int dist = CityGrid.distance(best.getLocation(), dest);
            log("✓ " + shortId(order.getId()) + " (" + weight + "кг) → " +
                    best.getId() + " [" + transportLabel(best.getTransportType()) +
                    ", виконано: " + best.getCompletedOrdersToday() + "], відстань: " + dist);
        } else {
            orderQueue.enqueue(order);
            log("⏳ " + shortId(order.getId()) + " (" + weight + "кг) → в чергу (немає вільного кур'єра)");
        }

        refreshAll();
    }

    private void completeOrderForCourier(String courierId) {
        Courier courier = null;
        for (Courier c : couriers) {
            if (c.getId().equalsIgnoreCase(courierId)) {
                courier = c;
                break;
            }
        }
        if (courier == null) {
            log("Помилка: кур'єра '" + courierId + "' не знайдено");
            return;
        }
        if (courier.isAvailable()) {
            log("Помилка: кур'єр " + courier.getId() + " вже вільний");
            return;
        }

        courier.completeOrder();
        log("✓ Кур'єр " + courier.getId() + " завершив замовлення (всього сьогодні: " +
                courier.getCompletedOrdersToday() + ")");

        // Спробувати призначити замовлення з черги
        List<OrderQueue.AssignmentResult> fromQueue = orderQueue.tryAssignPending(couriers);
        for (OrderQueue.AssignmentResult ar : fromQueue) {
            assignments.put(ar.getOrder(), ar.getCourier());
            int dist = CityGrid.distance(ar.getCourier().getLocation(), ar.getOrder().getDestination());
            log("  ↳ Із черги: " + shortId(ar.getOrder().getId()) + " → " +
                    ar.getCourier().getId() + " [" + transportLabel(ar.getCourier().getTransportType()) + "], відстань: " + dist);
        }

        refreshAll();
    }

    private void resetAll() {
        orders.clear();
        assignments.clear();
        assignmentLog.clear();
        orderQueue.clear();
        logArea.setText("");
        for (Courier c : couriers) {
            c.setStatus(CourierStatus.AVAILABLE);
            c.resetDailyStats();
        }
        refreshAll();
        log("Система скинута — всі кур'єри вільні, черга порожня");
    }

    private void refreshAll() {
        refreshCourierTable();
        refreshOrderTable();
        refreshQueueTable();
        cityPanel.repaint();
    }

    private void refreshCourierTable() {
        courierTableModel.setRowCount(0);
        for (Courier c : couriers) {
            courierTableModel.addRow(new Object[]{
                    c.getId(),
                    c.getLocation().toString(),
                    transportLabel(c.getTransportType()),
                    c.getTransportType().getMaxWeight() + " кг",
                    c.isAvailable() ? "✓ Вільний" : "⊘ Зайнятий",
                    c.getCompletedOrdersToday()
            });
        }
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        for (Order o : orders) {
            Courier assigned = assignments.get(o);
            orderTableModel.addRow(new Object[]{
                    shortId(o.getId()),
                    o.getPlacePoint().toString(),
                    o.getDestination().toString(),
                    o.getWeight(),
                    assigned != null
                            ? assigned.getId() + " [" + transportLabel(assigned.getTransportType()) + "]"
                            : "— в черзі"
            });
        }
    }

    private void refreshQueueTable() {
        queueTableModel.setRowCount(0);
        List<Order> pending = orderQueue.getPendingOrders();
        queueCountLabel.setText("Черга: " + pending.size());
        for (Order o : pending) {
            queueTableModel.addRow(new Object[]{
                    shortId(o.getId()),
                    o.getDestination().toString(),
                    o.getWeight()
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
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

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

            // Draw orders (diamonds)
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

                // Show completed count
                if (c.getCompletedOrdersToday() > 0) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    g2.setColor(new Color(180, 255, 180));
                    g2.drawString("×" + c.getCompletedOrdersToday(), cx + r + 2, cy - r + 4);
                }
            }

            // Legend
            int lx = offX + 5;
            int ly = offY + h - 85;
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
            int[] dy = {ly + 48, ly + 53, ly + 58, ly + 53};
            g2.fillPolygon(dx, dy, 4);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Замовлення (призначене)", lx + 15, ly + 58);

            g2.setColor(new Color(244, 67, 54));
            int[] dx2 = {lx + 5, lx + 10, lx + 5, lx};
            int[] dy2 = {ly + 63, ly + 68, ly + 73, ly + 68};
            g2.fillPolygon(dx2, dy2, 4);
            g2.setColor(new Color(200, 200, 200));
            g2.drawString("Замовлення (в черзі)", lx + 15, ly + 73);

            g2.setColor(new Color(180, 255, 180));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString("×N = виконано сьогодні", lx + 15, ly + 85);
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
