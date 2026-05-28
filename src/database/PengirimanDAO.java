package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PengirimanDAO {
    private Connection conn;

    public PengirimanDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public PengirimanDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public String insert(String status) {
        String idPengiriman = "N" + System.currentTimeMillis();
        String query = "INSERT INTO Pengiriman (id_pengiriman, status) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ps.setString(2, status);
            ps.executeUpdate();
            return idPengiriman;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idPengiriman) {
        String query = "SELECT * FROM Pengiriman WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> pengiriman = new HashMap<>();
                pengiriman.put("id_pengiriman", rs.getString("id_pengiriman"));
                pengiriman.put("status", rs.getString("status"));
                return pengiriman;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE status
    public boolean updateStatus(String idPengiriman, String status) {
        String query = "UPDATE Pengiriman SET status = ? WHERE id_pengiriman = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idPengiriman);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // TRACK pengiriman by transaksi ID
    public Map<String, Object> trackByTransaksiId(String idTransaksi) {
        Map<String, Object> trackingInfo = new HashMap<>();
        String query = "SELECT t.id_pengiriman, p.status as status_pengiriman, " +
                "cd.alamat, cd.no_resi, cd.estimasi, e.nama as ekspedisi, " +
                "cc.alamat_gerai, cc.kode_pengambilan, cc.status_pengembalian " +
                "FROM Transaksi t " +
                "JOIN Pengiriman p ON t.id_pengiriman = p.id_pengiriman " +
                "LEFT JOIN Click_and_Deliver cd ON p.id_pengiriman = cd.id_pengiriman " +
                "LEFT JOIN Ekspedisi e ON cd.id_ekspedisi = e.id_ekspedisi " +
                "LEFT JOIN Click_and_Collect cc ON p.id_pengiriman = cc.id_pengiriman " +
                "WHERE t.id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                trackingInfo.put("id_pengiriman", rs.getString("id_pengiriman"));
                trackingInfo.put("status_pengiriman", rs.getString("status_pengiriman"));
                trackingInfo.put("alamat", rs.getString("alamat"));
                trackingInfo.put("no_resi", rs.getInt("no_resi"));
                trackingInfo.put("estimasi", rs.getInt("estimasi"));
                trackingInfo.put("ekspedisi", rs.getString("ekspedisi"));
                trackingInfo.put("alamat_gerai", rs.getString("alamat_gerai"));
                trackingInfo.put("kode_pengambilan", rs.getString("kode_pengambilan"));
                trackingInfo.put("status_pengembalian", rs.getString("status_pengembalian"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackingInfo;
    }
}