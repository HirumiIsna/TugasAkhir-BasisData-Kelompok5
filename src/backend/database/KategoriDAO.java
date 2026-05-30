package src.backend.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KategoriDAO {
    private Connection conn;
    private ProdukMempunyaiKategoriDAO relasiDAO;

    public KategoriDAO() {
        this.conn = DatabaseConnection.getConnection();
        this.relasiDAO = new ProdukMempunyaiKategoriDAO(conn);
    }

    public KategoriDAO(Connection conn) {
        this.conn = conn;
        this.relasiDAO = new ProdukMempunyaiKategoriDAO(conn);
    }

    // ==================== CREATE ====================

    public boolean insert(String idKategori, String namaKategori, String deskripsi) {
        String query = "INSERT INTO Kategori (id_kategori, nama_kategori, deskripsi) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            ps.setString(2, namaKategori);
            ps.setString(3, deskripsi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== READ ====================

    public Map<String, Object> getById(String idKategori) {
        String query = "SELECT * FROM Kategori WHERE id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> kategori = new HashMap<>();
                kategori.put("id_kategori", rs.getString("id_kategori"));
                kategori.put("nama_kategori", rs.getString("nama_kategori"));
                kategori.put("deskripsi", rs.getString("deskripsi"));
                return kategori;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, String>> getAll() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_kategori, nama_kategori FROM Kategori ORDER BY id_kategori";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> kategori = new HashMap<>();
                kategori.put("id_kategori", rs.getString("id_kategori"));
                kategori.put("nama_kategori", rs.getString("nama_kategori"));
                list.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, String>> getAllWithDesc() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_kategori, nama_kategori, deskripsi FROM Kategori ORDER BY id_kategori";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> kategori = new HashMap<>();
                kategori.put("id_kategori", rs.getString("id_kategori"));
                kategori.put("nama_kategori", rs.getString("nama_kategori"));
                kategori.put("deskripsi", rs.getString("deskripsi"));
                list.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== UPDATE ====================

    public boolean update(String idKategori, String namaKategori, String deskripsi) {
        String query = "UPDATE Kategori SET nama_kategori = ?, deskripsi = ? WHERE id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, namaKategori);
            ps.setString(2, deskripsi);
            ps.setString(3, idKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== DELETE ====================

    public boolean delete(String idKategori) {
        if (relasiDAO.countProdukByKategoriId(idKategori) > 0) {
            return false;
        }
        String query = "DELETE FROM Kategori WHERE id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== RELASI MANY-TO-MANY DENGAN PRODUK ====================

    public List<Map<String, String>> getProdukByKategoriId(String idKategori) {
        return relasiDAO.getProdukByKategoriId(idKategori);
    }

    public int countProdukByKategoriId(String idKategori) {
        return relasiDAO.countProdukByKategoriId(idKategori);
    }

    public boolean hasProduk(String idKategori) {
        return countProdukByKategoriId(idKategori) > 0;
    }
}