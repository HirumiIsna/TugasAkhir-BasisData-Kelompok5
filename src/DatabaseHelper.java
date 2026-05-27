package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private Connection conn;

    public DatabaseHelper(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn(){
        return this.conn;
    }

    // ==================== FRONT-END METHODS ====================

    // Get all produk with their variants
    public List<Map<String, Object>> getAllProduk() {
        List<Map<String, Object>> produkList = new ArrayList<>();
        String query = "SELECT p.id_produk, p.nama, p.deskripsi, p.status, " +
                "m.nama as merk, v.id_varian, v.ukuran, v.warna, v.harga, v.stok, v.berat " +
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
                produkList.add(produk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produkList;
    }

    public List<Map<String, Object>> getVarianByProdukId(String idProduk) {
        List<Map<String, Object>> varianList = new ArrayList<>();
        String query = "SELECT id_varian, ukuran, warna, harga, stok, berat " +
                "FROM Varian_Produk WHERE id_produk = ? ORDER BY ukuran, warna";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> varian = new HashMap<>();
                    varian.put("id_varian", rs.getString("id_varian"));
                    varian.put("ukuran", rs.getString("ukuran"));
                    varian.put("warna", rs.getString("warna"));
                    varian.put("harga", rs.getInt("harga"));
                    varian.put("stok", rs.getInt("stok"));
                    varian.put("berat", rs.getInt("berat"));
                    varianList.add(varian);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return varianList;
    }

    // Filter produk by kategori
    public List<Map<String, Object>> getProdukByKategori(String idKategori) {
        List<Map<String, Object>> produkList = new ArrayList<>();
        String query = "SELECT p.id_produk, p.nama, p.deskripsi, m.nama as merk, " +
                "v.id_varian, v.ukuran, v.warna, v.harga, v.stok " +
                "FROM Produk p " +
                "JOIN Merk m ON p.id_merk = m.id_merk " +
                "JOIN Varian_Produk v ON p.id_produk = v.id_produk " +
                "JOIN Produk_Mempunyai_Kategori pmk ON p.id_produk = pmk.id_produk " +
                "WHERE pmk.id_kategori = ? AND p.status = 'Tersedia' AND v.stok > 0";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idKategori);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> produk = new HashMap<>();
                produk.put("id_produk", rs.getString("id_produk"));
                produk.put("nama", rs.getString("nama"));
                produk.put("merk", rs.getString("merk"));
                produk.put("id_varian", rs.getString("id_varian"));
                produk.put("ukuran", rs.getString("ukuran"));
                produk.put("warna", rs.getString("warna"));
                produk.put("harga", rs.getInt("harga"));
                produk.put("stok", rs.getInt("stok"));
                produkList.add(produk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produkList;
    }

    // Get all kategori
    public List<Map<String, String>> getAllKategori() {
        List<Map<String, String>> kategoriList = new ArrayList<>();
        String query = "SELECT id_kategori, nama_kategori FROM Kategori";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> kategori = new HashMap<>();
                kategori.put("id_kategori", rs.getString("id_kategori"));
                kategori.put("nama_kategori", rs.getString("nama_kategori"));
                kategoriList.add(kategori);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kategoriList;
    }

    // Get all ekspedisi
    public List<Map<String, String>> getAllEkspedisi() {
        List<Map<String, String>> ekspedisiList = new ArrayList<>();
        String query = "SELECT id_ekspedisi, nama, kode FROM Ekspedisi WHERE status = 'Aktif'";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> ekspedisi = new HashMap<>();
                ekspedisi.put("id_ekspedisi", rs.getString("id_ekspedisi"));
                ekspedisi.put("nama", rs.getString("nama"));
                ekspedisi.put("kode", rs.getString("kode"));
                ekspedisiList.add(ekspedisi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ekspedisiList;
    }

    // Get valid voucher by code
    public Map<String, Object> getVoucherByCode(String kodeVoucher) {
        String query = "SELECT * FROM Voucher WHERE kode = ? " +
                "AND tgl_mulai <= GETDATE() AND tgl_berakhir >= GETDATE() " +
                "AND kuota > 0";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, kodeVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> voucher = new HashMap<>();
                voucher.put("id_voucher", rs.getString("id_voucher"));
                voucher.put("min_belanja", rs.getInt("min_belanja"));
                voucher.put("kode", rs.getString("kode"));

                // Check voucher type
                String diskonQuery = "SELECT persen_diskon, maks_diskon FROM Diskon WHERE id_voucher = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(diskonQuery)) {
                    ps2.setString(1, rs.getString("id_voucher"));
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        voucher.put("type", "DISKON");
                        voucher.put("persen_diskon", rs2.getInt("persen_diskon"));
                        voucher.put("maks_diskon", rs2.getInt("maks_diskon"));
                    }
                }

                String potonganQuery = "SELECT nominal FROM Potongan WHERE id_voucher = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(potonganQuery)) {
                    ps2.setString(1, rs.getString("id_voucher"));
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        voucher.put("type", "POTONGAN");
                        voucher.put("nominal", rs2.getInt("nominal"));
                    }
                }

                String ongkirQuery = "SELECT persen_diskon, maks_diskon FROM Ongkir WHERE id_voucher = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(ongkirQuery)) {
                    ps2.setString(1, rs.getString("id_voucher"));
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        voucher.put("type", "ONGKIR");
                        voucher.put("persen_diskon", rs2.getInt("persen_diskon"));
                        voucher.put("maks_diskon", rs2.getInt("maks_diskon"));
                    }
                }

                return voucher;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Create new transaksi
    public String createTransaksi(String idPelanggan, int totalHarga, int totalBerat,
                                  int potonganHarga, String idPengiriman, String idVoucher) {
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

    // Add detail transaksi
    public boolean addDetailTransaksi(String idTransaksi, String idProduk, String idVarian, int jumlah) {
        String query = "INSERT INTO Detail_Transaksi (id_transaksi, id_produk, id_varian, jumlah) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, idProduk);
            ps.setString(3, idVarian);
            ps.setInt(4, jumlah);
            ps.executeUpdate();

            // Update stok
            String updateStok = "UPDATE Varian_Produk SET stok = stok - ? WHERE id_produk = ? AND id_varian = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(updateStok)) {
                ps2.setInt(1, jumlah);
                ps2.setString(2, idProduk);
                ps2.setString(3, idVarian);
                ps2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create pengiriman
    public String createPengiriman(String status) {
        String idPengiriman = "N" + System.currentTimeMillis();
        String query = "INSERT INTO Pengiriman (id_pengiriman, status) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ps.setString(2, status);
            ps.executeUpdate();
            return idPengiriman;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Create click and deliver
    public boolean createClickAndDeliver(String idPengiriman, String alamat, String idEkspedisi) {
        String query = "INSERT INTO Click_and_Deliver (id_pengiriman, alamat, no_resi, estimasi, id_ekspedisi) " +
                "VALUES (?, ?, ?, 3, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ps.setString(2, alamat);
            ps.setInt(3, (int)(Math.random() * 10000));
            ps.setString(4, idEkspedisi);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create click and collect
    public boolean createClickAndCollect(String idPengiriman, String alamatGerai) {
        String query = "INSERT INTO Click_and_Collect (id_pengiriman, alamat_gerai, batas_ambil, " +
                "kode_pengambilan, status_pengembalian) " +
                "VALUES (?, ?, DATEADD(day, 7, GETDATE()), ?, 'Belum Diambil')";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPengiriman);
            ps.setString(2, alamatGerai);
            ps.setString(3, "CC" + System.currentTimeMillis());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create payment
    public boolean createPaymentTransfer(String idTransaksi, String namaBank, String noRek) {
        String query = "INSERT INTO Transfer_Bank (id_transaksi, nama_bank, no_rek) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, namaBank);
            ps.setString(3, noRek);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createPaymentKredit(String idTransaksi, String namaBank, String masa, String noKartu) {
        String query = "INSERT INTO Kredit (id_transaksi, nama_bank, masa, no_kartu) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, namaBank);
            ps.setString(3, masa);
            ps.setString(4, noKartu);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createPaymentDompetDigital(String idTransaksi, String jenisDompet, String noTelp) {
        String query = "INSERT INTO Dompet_Digital (id_transaksi, jenis_dompet, no_telp) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, jenisDompet);
            ps.setString(3, noTelp);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get transaksi history for customer
    public List<Map<String, Object>> getTransaksiByPelanggan(String idPelanggan) {
        List<Map<String, Object>> transaksiList = new ArrayList<>();
        String query = "SELECT t.id_transaksi, t.tanggal, t.total_harga, t.status, " +
                "p.nama as produk, dt.jumlah " +
                "FROM Transaksi t " +
                "JOIN Detail_Transaksi dt ON t.id_transaksi = dt.id_transaksi " +
                "JOIN Produk p ON dt.id_produk = p.id_produk " +
                "WHERE t.id_pelanggan = ? " +
                "ORDER BY t.tanggal DESC";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPelanggan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> transaksi = new HashMap<>();
                transaksi.put("id_transaksi", rs.getString("id_transaksi"));
                transaksi.put("tanggal", rs.getDate("tanggal"));
                transaksi.put("total_harga", rs.getInt("total_harga"));
                transaksi.put("status", rs.getString("status"));
                transaksi.put("produk", rs.getString("produk"));
                transaksi.put("jumlah", rs.getInt("jumlah"));
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaksiList;
    }

    // Track pengiriman
    public Map<String, Object> trackPengiriman(String idTransaksi) {
        Map<String, Object> trackingInfo = new HashMap<>();
        String query = "SELECT t.id_pengiriman, p.status as status_pengiriman, " +
                "cd.alamat, cd.no_resi, cd.estimasi, e.nama as ekspedisi, " +
                "cc.alamat_gerai, cc.kode_pengambilan, cc.status_pengembalian " +
                "FROM Transaksi t " +
                "JOIN Pengiriman p ON t.id_pengiriman = p.id_pengiriman " +
                "LEFT JOIN Click_and_Deliver cd ON p.id_pengiriman = cd.id_pengiriman " +
                "LEFT JOIN Ekspedisi e ON cd.id_ekspedisi = e.id_ekspedisi " +
                "LEFT JOIN Click_and_Collect cc ON p.id_pengiriman = cc.id_pengiriman " +
                "WHERE t.id_transaksi = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                trackingInfo.put("status_pengiriman", rs.getString("status_pengiriman"));
                trackingInfo.put("alamat", rs.getString("alamat"));
                trackingInfo.put("no_resi", rs.getInt("no_resi"));
                trackingInfo.put("estimasi", rs.getInt("estimasi"));
                trackingInfo.put("ekspedisi", rs.getString("ekspedisi"));
                trackingInfo.put("alamat_gerai", rs.getString("alamat_gerai"));
                trackingInfo.put("kode_pengambilan", rs.getString("kode_pengambilan"));
                trackingInfo.put("status_pengembalian", rs.getString("status_pengembalian"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackingInfo;
    }

    // CRUD Produk
    public boolean createProduk(String idProduk, String nama, String deskripsi, String idMerk, String idPemasok) {
        String query = "INSERT INTO Produk (id_produk, status, nama, deskripsi, id_merk, id_pemasok) " +
                "VALUES (?, 'Tersedia', ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.setString(2, nama);
            ps.setString(3, deskripsi);
            ps.setString(4, idMerk);
            ps.setString(5, idPemasok);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduk(String idProduk, String nama, String deskripsi, String status) {
        String query = "UPDATE Produk SET nama = ?, deskripsi = ?, status = ? WHERE id_produk = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nama);
            ps.setString(2, deskripsi);
            ps.setString(3, status);
            ps.setString(4, idProduk);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduk(String idProduk) {
        String query = "DELETE FROM Produk WHERE id_produk = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idProduk);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CRUD Varian Produk
    public boolean createVarianProduk(String idProduk, String idVarian, String ukuran, String warna,
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
            ps.executeUpdate();
            return true;
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
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // CRUD Voucher
    public boolean createVoucherDiskon(String idVoucher, String kode, int minBelanja,
                                       Date tglMulai, Date tglBerakhir, int kuota,
                                       int persenDiskon, int maksDiskon) {
        String queryVoucher = "INSERT INTO Voucher (id_voucher, kode, min_belanja, tgl_mulai, tgl_berakhir, kuota) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String queryDiskon = "INSERT INTO Diskon (id_voucher, persen_diskon, maks_diskon) VALUES (?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(queryVoucher)) {
                ps.setString(1, idVoucher);
                ps.setString(2, kode);
                ps.setInt(3, minBelanja);
                ps.setDate(4, tglMulai);
                ps.setDate(5, tglBerakhir);
                ps.setInt(6, kuota);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(queryDiskon)) {
                ps.setString(1, idVoucher);
                ps.setInt(2, persenDiskon);
                ps.setInt(3, maksDiskon);
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

    public boolean createVoucherPotongan(String idVoucher, String kode, int minBelanja,
                                         Date tglMulai, Date tglBerakhir, int kuota, int nominal) {
        String queryVoucher = "INSERT INTO Voucher (id_voucher, kode, min_belanja, tgl_mulai, tgl_berakhir, kuota) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String queryPotongan = "INSERT INTO Potongan (id_voucher, nominal) VALUES (?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(queryVoucher)) {
                ps.setString(1, idVoucher);
                ps.setString(2, kode);
                ps.setInt(3, minBelanja);
                ps.setDate(4, tglMulai);
                ps.setDate(5, tglBerakhir);
                ps.setInt(6, kuota);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(queryPotongan)) {
                ps.setString(1, idVoucher);
                ps.setInt(2, nominal);
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

    public boolean createVoucherOngkir(String idVoucher, String kode, int minBelanja,
                                       Date tglMulai, Date tglBerakhir, int kuota,
                                       int persenDiskon, int maksDiskon) {
        String queryVoucher = "INSERT INTO Voucher (id_voucher, kode, min_belanja, tgl_mulai, tgl_berakhir, kuota) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String queryOngkir = "INSERT INTO Ongkir (id_voucher, persen_diskon, maks_diskon) VALUES (?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(queryVoucher)) {
                ps.setString(1, idVoucher);
                ps.setString(2, kode);
                ps.setInt(3, minBelanja);
                ps.setDate(4, tglMulai);
                ps.setDate(5, tglBerakhir);
                ps.setInt(6, kuota);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(queryOngkir)) {
                ps.setString(1, idVoucher);
                ps.setInt(2, persenDiskon);
                ps.setInt(3, maksDiskon);
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

    public boolean updateVoucherKuota(String idVoucher, int kuota) {
        String query = "UPDATE Voucher SET kuota = ? WHERE id_voucher = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, kuota);
            ps.setString(2, idVoucher);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteVoucher(String idVoucher) {
        String query = "DELETE FROM Voucher WHERE id_voucher = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all transaksi for admin
    public List<Map<String, Object>> getAllTransaksi() {
        List<Map<String, Object>> transaksiList = new ArrayList<>();
        String query = "SELECT t.id_transaksi, t.tanggal, t.total_harga, t.status, " +
                "p.nama as pelanggan, t.id_pengiriman " +
                "FROM Transaksi t " +
                "JOIN Pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
                "ORDER BY t.tanggal DESC";

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
                transaksiList.add(transaksi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transaksiList;
    }

    // Update status transaksi
    public boolean updateStatusTransaksi(String idTransaksi, String status) {
        String query = "UPDATE Transaksi SET status = ? WHERE id_transaksi = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idTransaksi);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update nomor resi pengiriman
    public boolean updateNoResi(String idPengiriman, int noResi) {
        String query = "UPDATE Click_and_Deliver SET no_resi = ? WHERE id_pengiriman = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, noResi);
            ps.setString(2, idPengiriman);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update status pengiriman
    public boolean updateStatusPengiriman(String idPengiriman, String status) {
        String query = "UPDATE Pengiriman SET status = ? WHERE id_pengiriman = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idPengiriman);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all pelanggan for admin
    public List<Map<String, Object>> getAllPelanggan() {
        List<Map<String, Object>> pelangganList = new ArrayList<>();
        String query = "SELECT p.id_pelanggan, p.nama, p.email, p.no_telp, p.tgl_daftar, " +
                "p.alamat_utama, t.nama_tier, t.benefit " +
                "FROM Pelanggan p " +
                "JOIN Tier_Loyalitas t ON p.id_tier = t.id_tier";

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
                pelanggan.put("nama_tier", rs.getString("nama_tier"));
                pelanggan.put("benefit", rs.getString("benefit"));
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pelangganList;
    }

    // Get top 5 produk with highest sales
    public List<Map<String, Object>> getTop5ProdukTerlaris() {
        List<Map<String, Object>> produkList = new ArrayList<>();
        String query = "SELECT TOP 5 p.nama as produk, SUM(dt.jumlah) as total_terjual, " +
                "SUM(dt.jumlah * v.harga) as total_penjualan " +
                "FROM Detail_Transaksi dt " +
                "JOIN Produk p ON dt.id_produk = p.id_produk " +
                "JOIN Varian_Produk v ON dt.id_produk = v.id_produk AND dt.id_varian = v.id_varian " +
                "GROUP BY p.nama " +
                "ORDER BY total_terjual DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> produk = new HashMap<>();
                produk.put("produk", rs.getString("produk"));
                produk.put("total_terjual", rs.getInt("total_terjual"));
                produk.put("total_penjualan", rs.getInt("total_penjualan"));
                produkList.add(produk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produkList;
    }

    // Get all ekspedisi for admin
    public List<Map<String, String>> getAllEkspedisiForAdmin() {
        List<Map<String, String>> ekspedisiList = new ArrayList<>();
        String query = "SELECT id_ekspedisi, nama, kode, status FROM Ekspedisi";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> ekspedisi = new HashMap<>();
                ekspedisi.put("id_ekspedisi", rs.getString("id_ekspedisi"));
                ekspedisi.put("nama", rs.getString("nama"));
                ekspedisi.put("kode", rs.getString("kode"));
                ekspedisi.put("status", rs.getString("status"));
                ekspedisiList.add(ekspedisi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ekspedisiList;
    }

    // Update ekspedisi status
    public boolean updateEkspedisiStatus(String idEkspedisi, String status) {
        String query = "UPDATE Ekspedisi SET status = ? WHERE id_ekspedisi = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, idEkspedisi);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
