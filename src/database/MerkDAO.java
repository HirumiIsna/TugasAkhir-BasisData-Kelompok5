package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerkDAO {
    private Connection conn;

    public MerkDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public MerkDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idMerk, String nama, String deskripsi) {
        String query = "INSERT INTO Merk (id_merk, nama, deskripsi) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idMerk);
            ps.setString(2, nama);
            ps.setString(3, deskripsi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idMerk) {
        String query = "SELECT * FROM Merk WHERE id_merk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idMerk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> merk = new HashMap<>();
                merk.put("id_merk", rs.getString("id_merk"));
                merk.put("nama", rs.getString("nama"));
                merk.put("deskripsi", rs.getString("deskripsi"));
                return merk;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, String>> getAll() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_merk, nama FROM Merk ORDER BY id_merk";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> merk = new HashMap<>();
                merk.put("id_merk", rs.getString("id_merk"));
                merk.put("nama", rs.getString("nama"));
                list.add(merk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ (all with description)
    public List<Map<String, String>> getAllWithDesc() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_merk, nama, deskripsi FROM Merk ORDER BY id_merk";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> merk = new HashMap<>();
                merk.put("id_merk", rs.getString("id_merk"));
                merk.put("nama", rs.getString("nama"));
                merk.put("deskripsi", rs.getString("deskripsi"));
                list.add(merk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean update(String idMerk, String nama, String deskripsi) {
        String query = "UPDATE Merk SET nama = ?, deskripsi = ? WHERE id_merk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, deskripsi);
            ps.setString(3, idMerk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE (with check if used)
    public boolean delete(String idMerk) {
        // Cek apakah merk masih digunakan oleh produk
        String checkQuery = "SELECT COUNT(*) FROM Produk WHERE id_merk = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setString(1, idMerk);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Merk masih digunakan oleh produk, tidak bisa dihapus!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String query = "DELETE FROM Merk WHERE id_merk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idMerk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}