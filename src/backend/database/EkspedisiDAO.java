package src.backend.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EkspedisiDAO {
    private Connection conn;

    public EkspedisiDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public EkspedisiDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idEkspedisi, String nama, String kode, String status) {
        String query = "INSERT INTO Ekspedisi (id_ekspedisi, nama, kode, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idEkspedisi);
            ps.setString(2, nama);
            ps.setString(3, kode);
            ps.setString(4, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idEkspedisi) {
        String query = "SELECT * FROM Ekspedisi WHERE id_ekspedisi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idEkspedisi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> ekspedisi = new HashMap<>();
                ekspedisi.put("id_ekspedisi", rs.getString("id_ekspedisi"));
                ekspedisi.put("nama", rs.getString("nama"));
                ekspedisi.put("kode", rs.getString("kode"));
                ekspedisi.put("status", rs.getString("status"));
                return ekspedisi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all active for customer)
    public List<Map<String, String>> getAllActive() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_ekspedisi, nama, kode FROM Ekspedisi WHERE status = 'Aktif'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> ekspedisi = new HashMap<>();
                ekspedisi.put("id_ekspedisi", rs.getString("id_ekspedisi"));
                ekspedisi.put("nama", rs.getString("nama"));
                ekspedisi.put("kode", rs.getString("kode"));
                list.add(ekspedisi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ (all for admin)
    public List<Map<String, String>> getAll() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_ekspedisi, nama, kode, status FROM Ekspedisi";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> ekspedisi = new HashMap<>();
                ekspedisi.put("id_ekspedisi", rs.getString("id_ekspedisi"));
                ekspedisi.put("nama", rs.getString("nama"));
                ekspedisi.put("kode", rs.getString("kode"));
                ekspedisi.put("status", rs.getString("status"));
                list.add(ekspedisi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean update(String idEkspedisi, String nama, String kode, String status) {
        String query = "UPDATE Ekspedisi SET nama = ?, kode = ?, status = ? WHERE id_ekspedisi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, kode);
            ps.setString(3, status);
            ps.setString(4, idEkspedisi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE status only
    public boolean updateStatus(String idEkspedisi, String status) {
        String query = "UPDATE Ekspedisi SET status = ? WHERE id_ekspedisi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idEkspedisi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idEkspedisi) {
        String query = "DELETE FROM Ekspedisi WHERE id_ekspedisi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idEkspedisi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}