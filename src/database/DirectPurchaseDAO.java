package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectPurchaseDAO {
    private Connection conn;

    public DirectPurchaseDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public DirectPurchaseDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPemasok, Date terminPembayaran, int volumeMin, Date jadwalPengiriman, double hargaKontrak) {
        String query = "INSERT INTO Direct_Purchase (id_pemasok, Termin_Pembayaran, Volume_Min, Jadwal_Pengiriman, Harga_Kontrak) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            ps.setDate(2, terminPembayaran);
            ps.setInt(3, volumeMin);
            ps.setDate(4, jadwalPengiriman);
            ps.setDouble(5, hargaKontrak);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idPemasok) {
        String query = "SELECT dp.*, p.nama as pemasok_nama FROM Direct_Purchase dp JOIN Pemasok p ON dp.id_pemasok = p.id_pemasok WHERE dp.id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> direct = new HashMap<>();
                direct.put("id_pemasok", rs.getString("id_pemasok"));
                direct.put("pemasok_nama", rs.getString("pemasok_nama"));
                direct.put("termin_pembayaran", rs.getDate("Termin_Pembayaran"));
                direct.put("volume_min", rs.getInt("Volume_Min"));
                direct.put("jadwal_pengiriman", rs.getDate("Jadwal_Pengiriman"));
                direct.put("harga_kontrak", rs.getDouble("Harga_Kontrak"));
                return direct;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT dp.*, p.nama as pemasok_nama FROM Direct_Purchase dp JOIN Pemasok p ON dp.id_pemasok = p.id_pemasok";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> direct = new HashMap<>();
                direct.put("id_pemasok", rs.getString("id_pemasok"));
                direct.put("pemasok_nama", rs.getString("pemasok_nama"));
                direct.put("termin_pembayaran", rs.getDate("Termin_Pembayaran"));
                direct.put("volume_min", rs.getInt("Volume_Min"));
                direct.put("jadwal_pengiriman", rs.getDate("Jadwal_Pengiriman"));
                direct.put("harga_kontrak", rs.getDouble("Harga_Kontrak"));
                list.add(direct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean update(String idPemasok, Date terminPembayaran, int volumeMin, Date jadwalPengiriman, double hargaKontrak) {
        String query = "UPDATE Direct_Purchase SET Termin_Pembayaran = ?, Volume_Min = ?, Jadwal_Pengiriman = ?, Harga_Kontrak = ? WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, terminPembayaran);
            ps.setInt(2, volumeMin);
            ps.setDate(3, jadwalPengiriman);
            ps.setDouble(4, hargaKontrak);
            ps.setString(5, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idPemasok) {
        String query = "DELETE FROM Direct_Purchase WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}