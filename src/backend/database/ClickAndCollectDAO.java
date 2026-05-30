package src.backend.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ClickAndCollectDAO {
    private Connection conn;

    public ClickAndCollectDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public ClickAndCollectDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPengiriman, String alamatGerai) {
        String query = "INSERT INTO Click_and_Collect (id_pengiriman, alamat_gerai, batas_ambil, kode_pengambilan, status_pengembalian) " +
                "VALUES (?, ?, DATEADD(day, 7, GETDATE()), ?, 'Belum Diambil')";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ps.setString(2, alamatGerai);
            ps.setString(3, "CC" + System.currentTimeMillis());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by pengiriman ID)
    public Map<String, Object> getById(String idPengiriman) {
        String query = "SELECT * FROM Click_and_Collect WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> collect = new HashMap<>();
                collect.put("id_pengiriman", rs.getString("id_pengiriman"));
                collect.put("alamat_gerai", rs.getString("alamat_gerai"));
                collect.put("batas_ambil", rs.getDate("batas_ambil"));
                collect.put("kode_pengambilan", rs.getString("kode_pengambilan"));
                collect.put("status_pengembalian", rs.getString("status_pengembalian"));
                return collect;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE status pengambilan
    public boolean updateStatusPengambilan(String idPengiriman, String status) {
        String query = "UPDATE Click_and_Collect SET status_pengembalian = ? WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idPengiriman);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE batas ambil
    public boolean updateBatasAmbil(String idPengiriman, Date batasAmbil) {
        String query = "UPDATE Click_and_Collect SET batas_ambil = ? WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, batasAmbil);
            ps.setString(2, idPengiriman);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}