package src.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransaksiDAO {
    private Connection conn;
    private DetailTransaksiDAO detailDAO;

    public TransaksiDAO() {
        this.conn = DatabaseConnection.getConnection();
        this.detailDAO = new DetailTransaksiDAO(conn);
    }

    public TransaksiDAO(Connection conn) {
        this.conn = conn;
        this.detailDAO = new DetailTransaksiDAO(conn);
    }

    // ==================== CREATE ====================

    // Insert transaksi dengan items (langsung ke detail transaksi many-to-many)
    public String insertWithItems(String idPelanggan, int totalHarga, int totalBerat, int potonganHarga,
                                  String idPengiriman, String idVoucher, List<Map<String, Object>> items) {
        String idTransaksi = "T" + System.currentTimeMillis();

        try {
            conn.setAutoCommit(false);

            String queryTransaksi = "INSERT INTO Transaksi (id_transaksi, tanggal, total_harga, total_berat, " +
                    "potongan_harga, status, id_pengiriman, id_pelanggan, id_voucher) " +
                    "VALUES (?, GETDATE(), ?, ?, ?, 'Pending', ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(queryTransaksi)) {
                ps.setString(1, idTransaksi);
                ps.setInt(2, totalHarga);
                ps.setInt(3, totalBerat);
                ps.setInt(4, potonganHarga);
                ps.setString(5, idPengiriman);
                ps.setString(6, idPelanggan);
                ps.setString(7, idVoucher);
                ps.executeUpdate();
            }

            for (Map<String, Object> item : items) {
                String idProduk = (String) item.get("id_produk");
                String idVarian = (String) item.get("id_varian");
                int jumlah = (int) item.get("jumlah");
                detailDAO.insert(idTransaksi, idProduk, idVarian, jumlah);
            }

            conn.commit();
            return idTransaksi;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Insert transaksi sederhana (tanpa items)
    public String insert(String idPelanggan, int totalHarga, int totalBerat, int potonganHarga,
                         String idPengiriman, String idVoucher) {
        String idTransaksi = "T" + System.currentTimeMillis();
        String query = "INSERT INTO Transaksi (id_transaksi, tanggal, total_harga, total_berat, " +
                "potongan_harga, status, id_pengiriman, id_pelanggan, id_voucher) " +
                "VALUES (?, GETDATE(), ?, ?, ?, 'Pending', ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setInt(2, totalHarga);
            ps.setInt(3, totalBerat);
            ps.setInt(4, potonganHarga);
            ps.setString(5, idPengiriman);
            ps.setString(6, idPelanggan);
            ps.setString(7, idVoucher);
            ps.executeUpdate();
            return idTransaksi;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================== READ ====================

    // Get transaksi by ID (dengan detail items dari many-to-many)
    public Map<String, Object> getById(String idTransaksi) {
        String query = "SELECT t.*, p.nama as pelanggan_nama FROM Transaksi t " +
                "JOIN Pelanggan p ON t.id_pelanggan = p.id_pelanggan WHERE t.id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("id_transaksi", rs.getString("id_transaksi"));
                transaksi.put("tanggal", rs.getDate("tanggal"));
                transaksi.put("total_harga", rs.getInt("total_harga"));
                transaksi.put("total_berat", rs.getInt("total_berat"));
                transaksi.put("potongan_harga", rs.getInt("potongan_harga"));
                transaksi.put("status", rs.getString("status"));
                transaksi.put("id_pengiriman", rs.getString("id_pengiriman"));
                transaksi.put("id_pelanggan", rs.getString("id_pelanggan"));
                transaksi.put("pelanggan_nama", rs.getString("pelanggan_nama"));
                transaksi.put("id_voucher", rs.getString("id_voucher"));
                transaksi.put("items", detailDAO.getByTransaksiId(idTransaksi));
                return transaksi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get transaksi by pelanggan ID
    public List<Map<String, Object>> getByPelangganId(String idPelanggan) {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT id_transaksi, tanggal, total_harga, status, potongan_harga FROM Transaksi " +
                "WHERE id_pelanggan = ? ORDER BY tanggal DESC";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("id_transaksi", rs.getString("id_transaksi"));
                transaksi.put("tanggal", rs.getDate("tanggal"));
                transaksi.put("total_harga", rs.getInt("total_harga"));
                transaksi.put("status", rs.getString("status"));
                transaksi.put("potongan_harga", rs.getInt("potongan_harga"));
                list.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Get all transaksi for admin
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT t.id_transaksi, t.tanggal, t.total_harga, t.status, p.nama as pelanggan, t.id_pengiriman " +
                "FROM Transaksi t JOIN Pelanggan p ON t.id_pelanggan = p.id_pelanggan ORDER BY t.tanggal DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("id_transaksi", rs.getString("id_transaksi"));
                transaksi.put("tanggal", rs.getDate("tanggal"));
                transaksi.put("total_harga", rs.getInt("total_harga"));
                transaksi.put("status", rs.getString("status"));
                transaksi.put("pelanggan", rs.getString("pelanggan"));
                transaksi.put("id_pengiriman", rs.getString("id_pengiriman"));
                list.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== UPDATE ====================

    // Update status transaksi
    public boolean updateStatus(String idTransaksi, String status) {
        String query = "UPDATE Transaksi SET status = ? WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idTransaksi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== DELETE ====================

    // Delete transaksi (dengan cascade ke detail transaksi)
    public boolean delete(String idTransaksi) {
        try {
            conn.setAutoCommit(false);
            detailDAO.deleteByTransaksiId(idTransaksi);
            String query = "DELETE FROM Transaksi WHERE id_transaksi = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, idTransaksi);
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

    // ==================== STATISTICS ====================

    // Get total penjualan per bulan
    public List<Map<String, Object>> getSalesStatistikPerBulan() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT YEAR(tanggal) as tahun, MONTH(tanggal) as bulan, " +
                "COUNT(*) as jumlah_transaksi, SUM(total_harga) as total_penjualan " +
                "FROM Transaksi WHERE status = 'Berhasil' " +
                "GROUP BY YEAR(tanggal), MONTH(tanggal) ORDER BY tahun DESC, bulan DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("tahun", rs.getInt("tahun"));
                stat.put("bulan", rs.getInt("bulan"));
                stat.put("jumlah_transaksi", rs.getInt("jumlah_transaksi"));
                stat.put("total_penjualan", rs.getInt("total_penjualan"));
                list.add(stat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}