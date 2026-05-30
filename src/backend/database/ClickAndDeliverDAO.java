package src.backend.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ClickAndDeliverDAO {
    private Connection conn;

    public ClickAndDeliverDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public ClickAndDeliverDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPengiriman, String alamat, String idEkspedisi) {
        String query = "INSERT INTO Click_and_Deliver (id_pengiriman, alamat, no_resi, estimasi, id_ekspedisi) VALUES (?, ?, ?, 3, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ps.setString(2, alamat);
            ps.setInt(3, (int)(Math.random() * 10000));
            ps.setString(4, idEkspedisi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by pengiriman ID)
    public Map<String, Object> getById(String idPengiriman) {
        String query = "SELECT cd.*, e.nama as ekspedisi_nama FROM Click_and_Deliver cd " +
                "LEFT JOIN Ekspedisi e ON cd.id_ekspedisi = e.id_ekspedisi WHERE cd.id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> deliver = new HashMap<>();
                deliver.put("id_pengiriman", rs.getString("id_pengiriman"));
                deliver.put("alamat", rs.getString("alamat"));
                deliver.put("no_resi", rs.getInt("no_resi"));
                deliver.put("estimasi", rs.getInt("estimasi"));
                deliver.put("id_ekspedisi", rs.getString("id_ekspedisi"));
                deliver.put("ekspedisi_nama", rs.getString("ekspedisi_nama"));
                return deliver;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE no resi
    public boolean updateNoResi(String idPengiriman, int noResi) {
        String query = "UPDATE Click_and_Deliver SET no_resi = ? WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, noResi);
            ps.setString(2, idPengiriman);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE estimasi
    public boolean updateEstimasi(String idPengiriman, int estimasi) {
        String query = "UPDATE Click_and_Deliver SET estimasi = ? WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, estimasi);
            ps.setString(2, idPengiriman);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}