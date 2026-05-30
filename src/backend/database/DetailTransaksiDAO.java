package src.backend.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailTransaksiDAO {
    private Connection conn;

    public DetailTransaksiDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public DetailTransaksiDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE - tambah item ke transaksi
    public boolean insert(String idTransaksi, String idProduk, String idVarian, int jumlah) {
        String query = "INSERT INTO Detail_Transaksi (id_transaksi, id_produk, id_varian, jumlah) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, idProduk);
            ps.setString(3, idVarian);
            ps.setInt(4, jumlah);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ - get all detail by transaksi ID
    public List<Map<String, Object>> getByTransaksiId(String idTransaksi) {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT dt.id_produk, dt.id_varian, dt.jumlah, " +
                "p.nama as produk_nama, v.ukuran, v.warna, v.harga " +
                "FROM Detail_Transaksi dt " +
                "JOIN Produk p ON dt.id_produk = p.id_produk " +
                "JOIN Varian_Produk v ON dt.id_produk = v.id_produk AND dt.id_varian = v.id_varian " +
                "WHERE dt.id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id_produk", rs.getString("id_produk"));
                detail.put("id_varian", rs.getString("id_varian"));
                detail.put("jumlah", rs.getInt("jumlah"));
                detail.put("produk_nama", rs.getString("produk_nama"));
                detail.put("ukuran", rs.getString("ukuran"));
                detail.put("warna", rs.getString("warna"));
                detail.put("harga", rs.getInt("harga"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ - get all detail by produk ID (history penjualan produk)
    public List<Map<String, Object>> getByProdukId(String idProduk) {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT dt.id_transaksi, dt.id_varian, dt.jumlah, " +
                "t.tanggal, t.status, t.total_harga " +
                "FROM Detail_Transaksi dt " +
                "JOIN Transaksi t ON dt.id_transaksi = t.id_transaksi " +
                "WHERE dt.id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id_transaksi", rs.getString("id_transaksi"));
                detail.put("id_varian", rs.getString("id_varian"));
                detail.put("jumlah", rs.getInt("jumlah"));
                detail.put("tanggal", rs.getDate("tanggal"));
                detail.put("status", rs.getString("status"));
                detail.put("total_harga", rs.getInt("total_harga"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ - get all detail by varian ID
    public List<Map<String, Object>> getByVarianId(String idProduk, String idVarian) {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT dt.id_transaksi, dt.jumlah, t.tanggal, t.status " +
                "FROM Detail_Transaksi dt " +
                "JOIN Transaksi t ON dt.id_transaksi = t.id_transaksi " +
                "WHERE dt.id_produk = ? AND dt.id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, idVarian);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id_transaksi", rs.getString("id_transaksi"));
                detail.put("jumlah", rs.getInt("jumlah"));
                detail.put("tanggal", rs.getDate("tanggal"));
                detail.put("status", rs.getString("status"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ - get all
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT dt.id_transaksi, dt.id_produk, dt.id_varian, dt.jumlah, " +
                "p.nama as produk_nama, v.ukuran, v.warna, t.tanggal, t.status " +
                "FROM Detail_Transaksi dt " +
                "JOIN Produk p ON dt.id_produk = p.id_produk " +
                "JOIN Varian_Produk v ON dt.id_produk = v.id_produk AND dt.id_varian = v.id_varian " +
                "JOIN Transaksi t ON dt.id_transaksi = t.id_transaksi " +
                "ORDER BY dt.id_transaksi";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id_transaksi", rs.getString("id_transaksi"));
                detail.put("id_produk", rs.getString("id_produk"));
                detail.put("id_varian", rs.getString("id_varian"));
                detail.put("jumlah", rs.getInt("jumlah"));
                detail.put("produk_nama", rs.getString("produk_nama"));
                detail.put("ukuran", rs.getString("ukuran"));
                detail.put("warna", rs.getString("warna"));
                detail.put("tanggal", rs.getDate("tanggal"));
                detail.put("status", rs.getString("status"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE - update jumlah
    public boolean updateJumlah(String idTransaksi, String idProduk, String idVarian, int jumlahBaru) {
        String query = "UPDATE Detail_Transaksi SET jumlah = ? WHERE id_transaksi = ? AND id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, jumlahBaru);
            ps.setString(2, idTransaksi);
            ps.setString(3, idProduk);
            ps.setString(4, idVarian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - delete one detail
    public boolean delete(String idTransaksi, String idProduk, String idVarian) {
        String query = "DELETE FROM Detail_Transaksi WHERE id_transaksi = ? AND id_produk = ? AND id_varian = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, idProduk);
            ps.setString(3, idVarian);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE - delete all detail by transaksi ID
    public boolean deleteByTransaksiId(String idTransaksi) {
        String query = "DELETE FROM Detail_Transaksi WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // STATISTICS - total penjualan per produk
    public List<Map<String, Object>> getTotalPenjualanPerProduk() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT p.id_produk, p.nama, SUM(dt.jumlah) as total_terjual, " +
                "SUM(dt.jumlah * v.harga) as total_penjualan " +
                "FROM Detail_Transaksi dt " +
                "JOIN Produk p ON dt.id_produk = p.id_produk " +
                "JOIN Varian_Produk v ON dt.id_produk = v.id_produk AND dt.id_varian = v.id_varian " +
                "GROUP BY p.id_produk, p.nama " +
                "ORDER BY total_terjual DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("id_produk", rs.getString("id_produk"));
                stat.put("nama", rs.getString("nama"));
                stat.put("total_terjual", rs.getInt("total_terjual"));
                stat.put("total_penjualan", rs.getInt("total_penjualan"));
                list.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // CHECK - apakah produk pernah terjual
    public boolean isProdukPernahTerjual(String idProduk) {
        String query = "SELECT COUNT(*) FROM Detail_Transaksi WHERE id_produk = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CHECK - apakah varian pernah terjual
    public boolean isVarianPernahTerjual(String idProduk, String idVarian) {
        String query = "SELECT COUNT(*) FROM Detail_Transaksi WHERE id_produk = ? AND id_varian = ?";
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