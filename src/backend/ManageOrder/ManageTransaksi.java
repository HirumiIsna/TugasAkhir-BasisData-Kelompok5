package src.backend.ManageOrder; // Sesuaikan package Anda

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class ManageTransaksi {
    private Connection conn;
    private Runnable refreshCallback;

    public ManageTransaksi(Connection conn, Runnable refreshCallback) {
        this.conn = conn;
        this.refreshCallback = refreshCallback;
    }

    public void loadDataTransaksi(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Transaksi");
        model.addColumn("ID Pelanggan");
        model.addColumn("Tanggal");
        model.addColumn("Total Harga");
        model.addColumn("Status"); // Sesuai dengan kolom di SQL

        try {
            String sql = "SELECT id_transaksi, id_pelanggan, tanggal, total_harga, status " +
                    "FROM Transaksi ORDER BY tanggal DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("id_transaksi"));
                row.add(rs.getString("id_pelanggan"));
                row.add(rs.getDate("tanggal"));
                row.add(rs.getInt("total_harga"));
                row.add(rs.getString("status"));
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data transaksi: " + e.getMessage());
        }
    }

    public void updateStatusTransaksi(JTable table, String idTransaksi, String statusBaru) {
        try {
            String sql = "UPDATE Transaksi SET status = ? WHERE id_transaksi = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusBaru);
            pstmt.setString(2, idTransaksi);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Status transaksi berhasil diperbarui!");
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Transaksi tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memperbarui status. Pastikan status adalah 'Pending', 'Berhasil', atau 'Gagal'.");
        }
    }
}