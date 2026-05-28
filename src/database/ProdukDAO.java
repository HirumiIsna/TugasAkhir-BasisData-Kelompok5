package src.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProdukDAO {
    private Connection conn;
    private ProdukMempunyaiKategoriDAO relasiDAO;

    public ProdukDAO() {
        this.conn = DatabaseConnection.getConnection();
        this.relasiDAO = new ProdukMempunyaiKategoriDAO(conn);
    }

    public ProdukDAO(Connection conn) {
        this.conn = conn;
        this.relasiDAO = new ProdukMempunyaiKategoriDAO(conn);
    }

    // ==================== CREATE ====================

    public boolean insert(String idProduk, String nama, String deskripsi, String idMerk, String idPemasok) {
        String query = "INSERT INTO Produk (id_produk, status, nama, deskripsi, id_merk, id_pemasok) " +
                "VALUES (?, 'Tersedia', ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, nama);
            ps.setString(3, deskripsi);
            ps.setString(4, idMerk);
            ps.setString(5, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertWithKategori(String idProduk, String nama, String deskripsi, String idMerk,
                                      String idPemasok, List<String> kategoriIds) {
        try {
            conn.setAutoCommit(false);

            if (!insert(idProduk, nama, deskripsi, idMerk, idPemasok)) {
                conn.rollback();
                return false;
            }

            if (!relasiDAO.insertBatch(idProduk, kategoriIds)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ==================== READ ====================

    public Map<String, Object> getById(String idProduk) {
        String query = "SELECT p.*, m.nama as merk_nama FROM Produk p " +
                "JOIN Merk m ON p.id_merk = m.id_merk WHERE p.id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> produk = new HashMap<>();
                produk.put("id_produk", rs.getString("id_produk"));
                produk.put("nama", rs.getString("nama"));
                produk.put("deskripsi", rs.getString("deskripsi"));
                produk.put("status", rs.getString("status"));
                produk.put("id_merk", rs.getString("id_merk"));
                produk.put("merk_nama", rs.getString("merk_nama"));
                produk.put("id_pemasok", rs.getString("id_pemasok"));
                produk.put("kategori", relasiDAO.getKategoriByProdukId(idProduk));
                return produk;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllForAdmin() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT p.id_produk, p.nama, p.deskripsi, p.status, m.nama as merk, p.id_merk, p.id_pemasok " +
                "FROM Produk p JOIN Merk m ON p.id_merk = m.id_merk ORDER BY p.id_produk";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> produk = new HashMap<>();
                produk.put("id_produk", rs.getString("id_produk"));
                produk.put("nama", rs.getString("nama"));
                produk.put("deskripsi", rs.getString("deskripsi"));
                produk.put("status", rs.getString("status"));
                produk.put("merk", rs.getString("merk"));
                produk.put("id_merk", rs.getString("id_merk"));
                produk.put("id_pemasok", rs.getString("id_pemasok"));
                list.add(produk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getAllAvailable() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT p.id_produk, p.nama, p.deskripsi, m.nama as merk, " +
                "v.id_varian, v.ukuran, v.warna, v.harga, v.stok, v.berat " +
                "FROM Produk p " +
                "JOIN Merk m ON p.id_merk = m.id_merk " +
                "JOIN Varian_Produk v ON p.id_produk = v.id_produk " +
                "WHERE p.status = 'Tersedia' AND v.stok > 0";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> produk = new HashMap<>();
                produk.put("id_produk", rs.getString("id_produk"));
                produk.put("nama", rs.getString("nama"));
                produk.put("deskripsi", rs.getString("deskripsi"));
                produk.put("merk", rs.getString("merk"));
                produk.put("id_varian", rs.getString("id_varian"));
                produk.put("ukuran", rs.getString("ukuran"));
                produk.put("warna", rs.getString("warna"));
                produk.put("harga", rs.getInt("harga"));
                produk.put("stok", rs.getInt("stok"));
                produk.put("berat", rs.getInt("berat"));
                list.add(produk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== UPDATE ====================

    public boolean update(String idProduk, String nama, String deskripsi, String status) {
        String query = "UPDATE Produk SET nama = ?, deskripsi = ?, status = ? WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, deskripsi);
            ps.setString(3, status);
            ps.setString(4, idProduk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateWithKategori(String idProduk, String nama, String deskripsi, String status,
                                      List<String> kategoriIds) {
        try {
            conn.setAutoCommit(false);

            if (!update(idProduk, nama, deskripsi, status)) {
                conn.rollback();
                return false;
            }

            if (!relasiDAO.updateKategoriForProduk(idProduk, kategoriIds)) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ==================== DELETE ====================

    public boolean delete(String idProduk) {
        try {
            conn.setAutoCommit(false);
            relasiDAO.deleteByProdukId(idProduk);
            String q2 = "DELETE FROM Varian_Produk WHERE id_produk = ?";
            try (PreparedStatement ps = conn.prepareStatement(q2)) {
                ps.setString(1, idProduk);
                ps.executeUpdate();
            }
            String q3 = "DELETE FROM Produk WHERE id_produk = ?";
            try (PreparedStatement ps = conn.prepareStatement(q3)) {
                ps.setString(1, idProduk);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ==================== MANY-TO-MANY METHODS ====================

    public boolean assignKategori(String idProduk, String idKategori) {
        return relasiDAO.addKategoriToProduk(idProduk, idKategori);
    }

    public boolean removeKategori(String idProduk, String idKategori) {
        return relasiDAO.removeKategoriFromProduk(idProduk, idKategori);
    }

    public boolean removeAllKategoriFromProduk(String idProduk) {
        return relasiDAO.deleteByProdukId(idProduk);
    }

    public List<Map<String, String>> getKategoriByProdukId(String idProduk) {
        return relasiDAO.getKategoriByProdukId(idProduk);
    }

    public int countKategoriByProdukId(String idProduk) {
        return relasiDAO.countKategoriByProdukId(idProduk);
    }
}