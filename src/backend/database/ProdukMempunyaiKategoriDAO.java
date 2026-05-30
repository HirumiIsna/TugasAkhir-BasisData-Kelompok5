package src.backend.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO untuk tabel Produk_Mempunyai_Kategori (Many-to-Many antara Produk dan Kategori)
 *
 * Relasi:
 * - Produk_Mempunyai_Kategori menghubungkan Produk dengan Kategori
 * - Composite Primary Key: (id_kategori, id_produk)
 * - Satu produk bisa memiliki banyak kategori
 * - Satu kategori bisa dimiliki banyak produk
 */
public class ProdukMempunyaiKategoriDAO {
    private Connection conn;

    public ProdukMempunyaiKategoriDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public ProdukMempunyaiKategoriDAO(Connection conn) {
        this.conn = conn;
    }

    // ==================== CREATE ====================

    // assign kategori ke produk
    public boolean insert(String idProduk, String idKategori) {
        String query = "INSERT INTO Produk_Mempunyai_Kategori (id_kategori, id_produk) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            ps.setString(2, idProduk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // batch insert multiple kategori untuk satu produk
    public boolean insertBatch(String idProduk, List<String> kategoriIds) {
        String query = "INSERT INTO Produk_Mempunyai_Kategori (id_kategori, id_produk) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (String idKategori : kategoriIds) {
                ps.setString(1, idKategori);
                ps.setString(2, idProduk);
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            for (int result : results) {
                if (result <= 0) return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== READ ====================

    // get all relasi
    public List<Map<String, String>> getAll() {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT id_kategori, id_produk FROM Produk_Mempunyai_Kategori ORDER BY id_produk";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> relasi = new HashMap<>();
                relasi.put("id_kategori", rs.getString("id_kategori"));
                relasi.put("id_produk", rs.getString("id_produk"));
                list.add(relasi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // get all kategori by produk ID (dengan detail nama kategori)
    public List<Map<String, String>> getKategoriByProdukId(String idProduk) {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT k.id_kategori, k.nama_kategori FROM Kategori k " +
                "JOIN Produk_Mempunyai_Kategori pmk ON k.id_kategori = pmk.id_kategori " +
                "WHERE pmk.id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ResultSet rs = ps.executeQuery();
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

    // get all produk by kategori ID (dengan detail nama produk)
    public List<Map<String, String>> getProdukByKategoriId(String idKategori) {
        List<Map<String, String>> list = new ArrayList<>();
        String query = "SELECT p.id_produk, p.nama FROM Produk p " +
                "JOIN Produk_Mempunyai_Kategori pmk ON p.id_produk = pmk.id_produk " +
                "WHERE pmk.id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> produk = new HashMap<>();
                produk.put("id_produk", rs.getString("id_produk"));
                produk.put("nama", rs.getString("nama"));
                list.add(produk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== UPDATE ====================

    // UPDATE: hapus semua relasi lama, lalu insert relasi baru untuk satu produk
    public boolean updateKategoriForProduk(String idProduk, List<String> newKategoriIds) {
        try {
            conn.setAutoCommit(false);

            // Hapus semua relasi lama
            deleteByProdukId(idProduk);

            // Insert relasi baru
            for (String idKategori : newKategoriIds) {
                if (!insert(idProduk, idKategori)) {
                    conn.rollback();
                    return false;
                }
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

    // UPDATE: tambah satu kategori ke produk (tanpa menghapus yang sudah ada)
    public boolean addKategoriToProduk(String idProduk, String idKategori) {
        if (exists(idProduk, idKategori)) {
            return true; // sudah ada, tidak perlu insert
        }
        return insert(idProduk, idKategori);
    }

    // UPDATE: hapus satu kategori dari produk
    public boolean removeKategoriFromProduk(String idProduk, String idKategori) {
        return delete(idProduk, idKategori);
    }

    // ==================== DELETE ====================

    // remove one relation
    public boolean delete(String idProduk, String idKategori) {
        String query = "DELETE FROM Produk_Mempunyai_Kategori WHERE id_produk = ? AND id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // remove all relations for a produk
    public boolean deleteByProdukId(String idProduk) {
        String query = "DELETE FROM Produk_Mempunyai_Kategori WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // remove all relations for a kategori
    public boolean deleteByKategoriId(String idKategori) {
        String query = "DELETE FROM Produk_Mempunyai_Kategori WHERE id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== CHECK & COUNT ====================

    // apakah relasi sudah ada
    public boolean exists(String idProduk, String idKategori) {
        String query = "SELECT COUNT(*) FROM Produk_Mempunyai_Kategori WHERE id_produk = ? AND id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idKategori);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // jumlah kategori untuk suatu produk
    public int countKategoriByProdukId(String idProduk) {
        String query = "SELECT COUNT(*) FROM Produk_Mempunyai_Kategori WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // jumlah produk untuk suatu kategori
    public int countProdukByKategoriId(String idKategori) {
        String query = "SELECT COUNT(*) FROM Produk_Mempunyai_Kategori WHERE id_kategori = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}