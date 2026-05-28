package src.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarianProdukDAO {
    private Connection conn;
    private DetailTransaksiDAO detailTransaksiDAO;

    public VarianProdukDAO() {
        this.conn = DatabaseConnection.getConnection();
        this.detailTransaksiDAO = new DetailTransaksiDAO(conn);
    }

    public VarianProdukDAO(Connection conn) {
        this.conn = conn;
        this.detailTransaksiDAO = new DetailTransaksiDAO(conn);
    }

    // ==================== CREATE ====================

    public boolean insert(String idProduk, String idVarian, String ukuran, String warna,
                          int berat, int stok, int harga, String barcode) {
        String query = "INSERT INTO Varian_Produk (id_produk, id_varian, ukuran, warna, berat, stok, harga, barcode) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idVarian);
            ps.setString(3, ukuran);
            ps.setString(4, warna);
            ps.setInt(5, berat);
            ps.setInt(6, stok);
            ps.setInt(7, harga);
            ps.setString(8, barcode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== READ ====================

    public Map<String, Object> getById(String idProduk, String idVarian) {
        String query = "SELECT v.*, p.nama as produk_nama FROM Varian_Produk v " +
                "JOIN Produk p ON v.id_produk = p.id_produk " +
                "WHERE v.id_produk = ? AND v.id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idVarian);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> varian = new HashMap<>();
                varian.put("id_produk", rs.getString("id_produk"));
                varian.put("id_varian", rs.getString("id_varian"));
                varian.put("ukuran", rs.getString("ukuran"));
                varian.put("warna", rs.getString("warna"));
                varian.put("berat", rs.getInt("berat"));
                varian.put("stok", rs.getInt("stok"));
                varian.put("harga", rs.getInt("harga"));
                varian.put("barcode", rs.getString("barcode"));
                varian.put("produk_nama", rs.getString("produk_nama"));
                return varian;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getAllForAdmin() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT v.*, p.nama as produk_nama FROM Varian_Produk v " +
                "JOIN Produk p ON v.id_produk = p.id_produk ORDER BY v.id_produk, v.id_varian";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> varian = new HashMap<>();
                varian.put("id_produk", rs.getString("id_produk"));
                varian.put("id_varian", rs.getString("id_varian"));
                varian.put("ukuran", rs.getString("ukuran"));
                varian.put("warna", rs.getString("warna"));
                varian.put("berat", rs.getInt("berat"));
                varian.put("stok", rs.getInt("stok"));
                varian.put("harga", rs.getInt("harga"));
                varian.put("barcode", rs.getString("barcode"));
                varian.put("produk_nama", rs.getString("produk_nama"));
                list.add(varian);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getByProdukId(String idProduk) {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT id_varian, ukuran, warna, berat, stok, harga, barcode " +
                "FROM Varian_Produk WHERE id_produk = ? ORDER BY ukuran, warna";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> varian = new HashMap<>();
                varian.put("id_varian", rs.getString("id_varian"));
                varian.put("ukuran", rs.getString("ukuran"));
                varian.put("warna", rs.getString("warna"));
                varian.put("berat", rs.getInt("berat"));
                varian.put("stok", rs.getInt("stok"));
                varian.put("harga", rs.getInt("harga"));
                varian.put("barcode", rs.getString("barcode"));
                list.add(varian);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== RELASI MANY-TO-MANY DENGAN TRANSAKSI ====================

    public List<Map<String, Object>> getTransaksiByVarianId(String idProduk, String idVarian) {
        return detailTransaksiDAO.getByVarianId(idProduk, idVarian);
    }

    public boolean isVarianPernahTerjual(String idProduk, String idVarian) {
        return detailTransaksiDAO.isVarianPernahTerjual(idProduk, idVarian);
    }

    // ==================== UPDATE ====================

    public boolean update(String idProduk, String idVarian, String ukuran, String warna,
                          int berat, int stok, int harga, String barcode) {
        String query = "UPDATE Varian_Produk SET ukuran = ?, warna = ?, berat = ?, stok = ?, harga = ?, barcode = ? " +
                "WHERE id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ukuran);
            ps.setString(2, warna);
            ps.setInt(3, berat);
            ps.setInt(4, stok);
            ps.setInt(5, harga);
            ps.setString(6, barcode);
            ps.setString(7, idProduk);
            ps.setString(8, idVarian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStok(String idProduk, String idVarian, int stok) {
        String query = "UPDATE Varian_Produk SET stok = ? WHERE id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, stok);
            ps.setString(2, idProduk);
            ps.setString(3, idVarian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateHarga(String idProduk, String idVarian, int harga) {
        String query = "UPDATE Varian_Produk SET harga = ? WHERE id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, harga);
            ps.setString(2, idProduk);
            ps.setString(3, idVarian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reduceStock(String idProduk, String idVarian, int jumlah) {
        String query = "UPDATE Varian_Produk SET stok = stok - ? WHERE id_produk = ? AND id_varian = ? AND stok >= ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, jumlah);
            ps.setString(2, idProduk);
            ps.setString(3, idVarian);
            ps.setInt(4, jumlah);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== DELETE ====================

    public boolean delete(String idProduk, String idVarian) {
        if (isVarianPernahTerjual(idProduk, idVarian)) {
            return false;
        }
        String query = "DELETE FROM Varian_Produk WHERE id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idVarian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteByProdukId(String idProduk) {
        String query = "DELETE FROM Varian_Produk WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== CHECK ====================

    public boolean exists(String idProduk, String idVarian) {
        String query = "SELECT COUNT(*) FROM Varian_Produk WHERE id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idVarian);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}