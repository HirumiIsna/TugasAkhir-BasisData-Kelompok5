package src.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PelangganDAO {
    private Connection conn;

    public PelangganDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public PelangganDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPelanggan, String nama, String email, String noTelp, String alamat) {
        String query = "INSERT INTO Pelanggan (id_pelanggan, nama, email, no_telp, tgl_daftar, alamat_utama, id_tier) " +
                "VALUES (?, ?, ?, ?, GETDATE(), ?, 'TR01')";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
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
    public Map<String, Object> getById(String idPelanggan) {
        String query = "SELECT p.*, t.nama_tier, t.benefit FROM Pelanggan p " +
                "JOIN Tier_Loyalitas t ON p.id_tier = t.id_tier WHERE p.id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> pelanggan = new HashMap<>();
                pelanggan.put("id_pelanggan", rs.getString("id_pelanggan"));
                pelanggan.put("nama", rs.getString("nama"));
                pelanggan.put("email", rs.getString("email"));
                pelanggan.put("no_telp", rs.getString("no_telp"));
                pelanggan.put("tgl_daftar", rs.getDate("tgl_daftar"));
                pelanggan.put("alamat_utama", rs.getString("alamat_utama"));
                pelanggan.put("id_tier", rs.getString("id_tier"));
                pelanggan.put("nama_tier", rs.getString("nama_tier"));
                pelanggan.put("benefit", rs.getString("benefit"));
                return pelanggan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT p.*, t.nama_tier, t.benefit FROM Pelanggan p JOIN Tier_Loyalitas t ON p.id_tier = t.id_tier";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> pelanggan = new HashMap<>();
                pelanggan.put("id_pelanggan", rs.getString("id_pelanggan"));
                pelanggan.put("nama", rs.getString("nama"));
                pelanggan.put("email", rs.getString("email"));
                pelanggan.put("no_telp", rs.getString("no_telp"));
                pelanggan.put("tgl_daftar", rs.getDate("tgl_daftar"));
                pelanggan.put("alamat_utama", rs.getString("alamat_utama"));
                pelanggan.put("id_tier", rs.getString("id_tier"));
                pelanggan.put("nama_tier", rs.getString("nama_tier"));
                pelanggan.put("benefit", rs.getString("benefit"));
                list.add(pelanggan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean update(String idPelanggan, String nama, String email, String noTelp, String alamat) {
        String query = "UPDATE Pelanggan SET nama = ?, email = ?, no_telp = ?, alamat_utama = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, email);
            ps.setString(3, noTelp);
            ps.setString(4, alamat);
            ps.setString(5, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE tier
    public boolean updateTier(String idPelanggan, String idTier) {
        String query = "UPDATE Pelanggan SET id_tier = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTier);
            ps.setString(2, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateId(String oldId, String newId) {
        String query = "UPDATE Pelanggan SET id_pelanggan = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newId);
            ps.setString(2, oldId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNama(String idPelanggan, String nama) {
        String query = "UPDATE Pelanggan SET nama = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmail(String idPelanggan, String email) {
        String query = "UPDATE Pelanggan SET email = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNoTelp(String idPelanggan, String noTelp) {
        String query = "UPDATE Pelanggan SET no_telp = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, noTelp);
            ps.setString(2, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAlamat(String idPelanggan, String alamat) {
        String query = "UPDATE Pelanggan SET alamat_utama = ? WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, alamat);
            ps.setString(2, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idPelanggan) {
        String query = "DELETE FROM Pelanggan WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}