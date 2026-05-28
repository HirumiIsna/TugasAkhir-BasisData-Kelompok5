package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TierLoyalitasDAO {
    private Connection conn;

    public TierLoyalitasDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public TierLoyalitasDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idTier, String namaTier, int minPoin, String benefit) {
        String query = "INSERT INTO Tier_Loyalitas (id_tier, nama_tier, min_poin, benefit) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTier);
            ps.setString(2, namaTier);
            ps.setInt(3, minPoin);
            ps.setString(4, benefit);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idTier) {
        String query = "SELECT * FROM Tier_Loyalitas WHERE id_tier = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTier);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> tier = new HashMap<>();
                tier.put("id_tier", rs.getString("id_tier"));
                tier.put("nama_tier", rs.getString("nama_tier"));
                tier.put("min_poin", rs.getInt("min_poin"));
                tier.put("benefit", rs.getString("benefit"));
                return tier;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT * FROM Tier_Loyalitas ORDER BY min_poin";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> tier = new HashMap<>();
                tier.put("id_tier", rs.getString("id_tier"));
                tier.put("nama_tier", rs.getString("nama_tier"));
                tier.put("min_poin", rs.getInt("min_poin"));
                tier.put("benefit", rs.getString("benefit"));
                list.add(tier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ tier by poin
    public Map<String, Object> getTierByPoin(int poin) {
        String query = "SELECT TOP 1 * FROM Tier_Loyalitas WHERE min_poin <= ? ORDER BY min_poin DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, poin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> tier = new HashMap<>();
                tier.put("id_tier", rs.getString("id_tier"));
                tier.put("nama_tier", rs.getString("nama_tier"));
                tier.put("min_poin", rs.getInt("min_poin"));
                tier.put("benefit", rs.getString("benefit"));
                return tier;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean update(String idTier, String namaTier, int minPoin, String benefit) {
        String query = "UPDATE Tier_Loyalitas SET nama_tier = ?, min_poin = ?, benefit = ? WHERE id_tier = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaTier);
            ps.setInt(2, minPoin);
            ps.setString(3, benefit);
            ps.setString(4, idTier);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idTier) {
        // Cek apakah tier masih digunakan oleh pelanggan
        String checkQuery = "SELECT COUNT(*) FROM Pelanggan WHERE id_tier = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setString(1, idTier);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Tier masih digunakan oleh pelanggan, tidak bisa dihapus!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String query = "DELETE FROM Tier_Loyalitas WHERE id_tier = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTier);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}