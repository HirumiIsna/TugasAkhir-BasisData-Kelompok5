package src.FrontEnd;

import src.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class TransactionView {
    private final App app;

    public TransactionView(App app) {
        this.app = app;
    }

    public JPanel buildTransactionsPanel(String filter, String order) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Filter & Urutkan"));
        JLabel filterLabel = new JLabel("Filter Waktu:");
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Last Week", "Last Month", "Last Year"});
        filterCombo.setSelectedItem(getFilterText(filter));
        JLabel sortLabel = new JLabel("Urutkan:");
        JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Terbaru (DESC)", "Terlama (ASC)"});
        sortCombo.setSelectedItem(order.equals("DESC") ? "Terbaru (DESC)" : "Terlama (ASC)");
        JButton applyButton = new JButton("Terapkan");

        controlPanel.add(filterLabel);
        controlPanel.add(filterCombo);
        controlPanel.add(sortLabel);
        controlPanel.add(sortCombo);
        controlPanel.add(applyButton);
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        String selectedFilter = (String) filterCombo.getSelectedItem();
        String filterCondition = buildFilterCondition(selectedFilter);
        String orderBy = sortCombo.getSelectedItem().equals("Terbaru (DESC)") ? "DESC" : "ASC";
        String query = buildQuery(filterCondition, orderBy);

        loadTransactionsToContainer(containerPanel, query);

        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        applyButton.addActionListener(e -> {
            String newFilter = (String) filterCombo.getSelectedItem();
            String filterCode = getFilterCode(newFilter);
            String newOrder = sortCombo.getSelectedItem().equals("Terbaru (DESC)") ? "DESC" : "ASC";
            JPanel newPanel = buildTransactionsPanel(filterCode, newOrder);
            app.getTabbedPane().setComponentAt(2, newPanel);
            app.getTabbedPane().setSelectedIndex(2);
        });

        return mainPanel;
    }

    private String getFilterText(String filter) {
        switch (filter) {
            case "ALL": return "All";
            case "WEEK": return "Last Week";
            case "MONTH": return "Last Month";
            case "YEAR": return "Last Year";
            default: return "All";
        }
    }

    private String getFilterCode(String filterText) {
        switch (filterText) {
            case "Last Week": return "WEEK";
            case "Last Month": return "MONTH";
            case "Last Year": return "YEAR";
            default: return "ALL";
        }
    }

    private String buildFilterCondition(String selectedFilter) {
        if ("Last Week".equals(selectedFilter)) {
            return "AND t.tanggal >= DATEADD(day, -7, GETDATE())";
        } else if ("Last Month".equals(selectedFilter)) {
            return "AND t.tanggal >= DATEADD(month, -1, GETDATE())";
        } else if ("Last Year".equals(selectedFilter)) {
            return "AND t.tanggal >= DATEADD(year, -1, GETDATE())";
        }
        return "";
    }

    private String buildQuery(String filterCondition, String orderBy) {
        return "SELECT t.id_transaksi, t.tanggal, t.total_harga, t.status AS status_transaksi, " +
                "       p.status AS status_pengiriman, " +
                "       COALESCE(cd.alamat, cc.alamat_gerai) AS alamat_pengiriman, " +
                "       CASE WHEN cd.id_pengiriman IS NOT NULL THEN 'Delivery' ELSE 'Collect' END AS jenis_pengiriman " +
                "FROM Transaksi t " +
                "JOIN Pengiriman p ON t.id_pengiriman = p.id_pengiriman " +
                "LEFT JOIN Click_and_Deliver cd ON p.id_pengiriman = cd.id_pengiriman " +
                "LEFT JOIN Click_and_Collect cc ON p.id_pengiriman = cc.id_pengiriman " +
                "WHERE t.id_pelanggan = ? " + filterCondition +
                " ORDER BY t.tanggal " + orderBy;
    }

    private void loadTransactionsToContainer(JPanel container, String query) {
        container.removeAll();
        try (PreparedStatement ps = app.getConnection().prepareStatement(query)) {
            ps.setString(1, app.getLoggedinUserID());
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    JPanel card = createTransactionsCard(
                            rs.getString("id_transaksi"),
                            rs.getDate("tanggal"),
                            rs.getDouble("total_harga"),
                            rs.getString("status_transaksi"),
                            rs.getString("status_pengiriman"),
                            rs.getString("jenis_pengiriman"),
                            rs.getString("alamat_pengiriman")
                    );
                    container.add(card);
                    container.add(Box.createRigidArea(new Dimension(0, 10)));
                }
                if (!hasData) {
                    JLabel emptyLabel = new JLabel("Belum ada riwayat transaksi.", SwingConstants.CENTER);
                    emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                    container.add(emptyLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Gagal memuat data transaksi: " + e.getMessage(), SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            container.add(errorLabel);
        }
        container.revalidate();
        container.repaint();
    }

    private JPanel createTransactionsCard(String idTransaksi, Date tanggal, double totalHarga,
                                          String statusTransaksi, String statusPengiriman,
                                          String jenis, String alamat) {
        JPanel cardPanel = new JPanel(new BorderLayout(15, 10));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.add(new JLabel("ID Transaksi: " + idTransaksi));
        infoPanel.add(new JLabel("Tanggal: " + (tanggal != null ? tanggal.toString() : "-")));
        infoPanel.add(new JLabel("Status Transaksi: " + statusTransaksi));
        infoPanel.add(new JLabel("Pengiriman: " + jenis + " | " + statusPengiriman + " | " + alamat));

        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        DecimalFormat df = new DecimalFormat("#,###");
        JLabel priceLabel = new JLabel("Total: Rp " + df.format(totalHarga), SwingConstants.RIGHT);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton viewDetailButton = new JButton("Lihat Detail Barang");
        viewDetailButton.addActionListener(e -> showTransactionDetail(idTransaksi));

        actionPanel.add(priceLabel);
        actionPanel.add(viewDetailButton);
        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(actionPanel, BorderLayout.EAST);
        return cardPanel;
    }

    private void showTransactionDetail(String idTransaksi) {
        JDialog detailDialog = new JDialog(app, "Detail Barang - Transaksi " + idTransaksi, true);
        detailDialog.setSize(700, 400);
        detailDialog.setLayout(new BorderLayout());

        DefaultTableModel detailTableModel = new DefaultTableModel(
                new Object[]{"Nama Produk", "Ukuran", "Warna", "Jumlah", "Harga Satuan", "Subtotal"}, 0
        );
        JTable detailTable = new JTable(detailTableModel);
        detailDialog.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        String detailQuery = "SELECT p.nama, vp.ukuran, vp.warna, dt.jumlah, vp.harga, (dt.jumlah * vp.harga) AS subtotal " +
                "FROM Detail_Transaksi dt " +
                "JOIN Varian_Produk vp ON dt.id_produk = vp.id_produk AND dt.id_varian = vp.id_varian " +
                "JOIN Produk p ON vp.id_produk = p.id_produk " +
                "WHERE dt.id_transaksi = ?";

        try (PreparedStatement ps = app.getConnection().prepareStatement(detailQuery)) {
            ps.setString(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detailTableModel.addRow(new Object[]{
                            rs.getString("nama"),
                            rs.getString("ukuran"),
                            rs.getString("warna"),
                            rs.getInt("jumlah"),
                            rs.getDouble("harga"),
                            rs.getDouble("subtotal")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(app, "Gagal memuat detail barang: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        detailDialog.setLocationRelativeTo(app);
        detailDialog.setVisible(true);
    }
}