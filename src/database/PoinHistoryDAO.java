package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoinHistoryDAO {
    private Connection conn;

    public PoinHistoryDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public PoinHistoryDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPelanggan, String idTransaksi, int perubahanPoin) {
        String idHistory = "H" + System.currentTimeMillis();
        String query = "INSERT INTO Poin_History (id_history, perubahan_point, tanggal, id_transaksi, id_pelanggan) " +
                "VALUES (?, ?, GETDATE(), ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idHistory);
            ps.setInt(2, perubahanPoin);
            ps.setString(3, idTransaksi);
            ps.setString(4, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by pelanggan)
    public List<Map<String, Object>> getByPelangganId(String idPelanggan) {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT id_history, perubahan_point, tanggal, id_transaksi FROM Poin_History " +
                "WHERE id_pelanggan = ? ORDER BY tanggal DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> history = new HashMap<>();
                history.put("id_history", rs.getString("id_history"));
                history.put("perubahan_point", rs.getInt("perubahan_point"));
                history.put("tanggal", rs.getDate("tanggal"));
                history.put("id_transaksi", rs.getString("id_transaksi"));
                list.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ (all for admin)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT ph.*, p.nama as pelanggan_nama FROM Poin_History ph " +
                "JOIN Pelanggan p ON ph.id_pelanggan = p.id_pelanggan ORDER BY ph.tanggal DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> history = new HashMap<>();
                history.put("id_history", rs.getString("id_history"));
                history.put("perubahan_point", rs.getInt("perubahan_point"));
                history.put("tanggal", rs.getDate("tanggal"));
                history.put("id_transaksi", rs.getString("id_transaksi"));
                history.put("id_pelanggan", rs.getString("id_pelanggan"));
                history.put("pelanggan_nama", rs.getString("pelanggan_nama"));
                list.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // GET total poin pelanggan
    public int getTotalPoin(String idPelanggan) {
        String query = "SELECT SUM(perubahan_point) as total_poin FROM Poin_History WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_poin");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // DELETE (by pelanggan)
    public boolean deleteByPelangganId(String idPelanggan) {
        String query = "DELETE FROM Poin_History WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}