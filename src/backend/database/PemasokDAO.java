package src.backend.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PemasokDAO {
    private Connection conn;

    public PemasokDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public PemasokDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPemasok, String nama, String email, String noTelp, String alamat) {
        String query = "INSERT INTO Pemasok (id_pemasok, nama, email, no_telp, alamat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            ps.setString(2, nama);
            ps.setString(3, email);
            ps.setString(4, noTelp);
            ps.setString(5, alamat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idPemasok) {
        String query = "SELECT * FROM Pemasok WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> pemasok = new HashMap<>();
                pemasok.put("id_pemasok", rs.getString("id_pemasok"));
                pemasok.put("nama", rs.getString("nama"));
                pemasok.put("email", rs.getString("email"));
                pemasok.put("no_telp", rs.getString("no_telp"));
                pemasok.put("alamat", rs.getString("alamat"));
                return pemasok;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT * FROM Pemasok ORDER BY id_pemasok";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> pemasok = new HashMap<>();
                pemasok.put("id_pemasok", rs.getString("id_pemasok"));
                pemasok.put("nama", rs.getString("nama"));
                pemasok.put("email", rs.getString("email"));
                pemasok.put("no_telp", rs.getString("no_telp"));
                pemasok.put("alamat", rs.getString("alamat"));
                list.add(pemasok);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean update(String idPemasok, String nama, String email, String noTelp, String alamat) {
        String query = "UPDATE Pemasok SET nama = ?, email = ?, no_telp = ?, alamat = ? WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, email);
            ps.setString(3, noTelp);
            ps.setString(4, alamat);
            ps.setString(5, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE (with check if used)
    public boolean delete(String idPemasok) {
        // Cek apakah pemasok masih memiliki produk
        String checkQuery = "SELECT COUNT(*) FROM Produk WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setString(1, idPemasok);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Pemasok masih memiliki produk, tidak bisa dihapus!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String query = "DELETE FROM Pemasok WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}