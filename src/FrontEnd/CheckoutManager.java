package src.FrontEnd;

import src.App;

import javax.swing.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class CheckoutManager {
    private final App app;
    private Connection conn;
    private ArrayList<Object[]> keranjangItem;
    private ArrayList<Object[]> dataPengantar;
    private double hargaSub;
    private double ongkir;

    public CheckoutManager(App app, Connection conn, ArrayList<Object[]> keranjangItem, ArrayList<Object[]> dataPengantar) {
        this.app = app;
        this.conn = conn;
        this.keranjangItem = keranjangItem;
        this.dataPengantar = dataPengantar;
    }

    public void refreshHarga() {
        hargaSub = 0;
        ongkir = 0;
        double berat = 0;
        for (Object[] x : keranjangItem) {
            hargaSub += ((int) x[4] * (int) x[5]);
            berat += (int) x[9] * (int) x[5];
        }
        ongkir = 10000 * (berat / 1000);
        DecimalFormat df = new DecimalFormat("#,###");
        app.setTFtotal("RP. " + df.format(hargaSub));
        String selectedKirim = app.getCbPengirimanSelectedItem();
        if ("Collect".equals(selectedKirim)) {
            app.setLbOngkir("RP. 0");
            ongkir = 0;
        } else {
            app.setLbOngkir("RP. " + df.format(ongkir));
        }
    }

    public void refreshEkspedisi(String akunAlamat) {
        try {
            String query = "SELECT nama, id_ekspedisi FROM Ekspedisi ORDER BY nama;";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            app.clearCbEkspedisi();
            dataPengantar.clear();
            while (rs.next()) {
                dataPengantar.add(new Object[]{rs.getString(1), rs.getString(2)});
                app.addCbEkspedisiItem(rs.getString(1));
            }
            app.setTaAlamatEditable(true);
            app.setTaAlamatText(akunAlamat);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, e.getMessage());
        }
    }

    public void checkoutRun() {
        if (keranjangItem.isEmpty()) {
            JOptionPane.showMessageDialog(app, "Keranjang Kosong");
            return;
        }

        if ("-".equals(app.getCbMethodSelectedItem())) {
            JOptionPane.showMessageDialog(app, "Pilih Opsi Pembayaran!");
            return;
        }

        if (app.getTfOpsiText().isEmpty()) {
            String[] text = app.getLbMethod1Text().split(" ");
            JOptionPane.showMessageDialog(app, "Isi Informasi " + text[0]);
            return;
        }

        double persentaseDiskonTier = 0.0;
        try {
            String queryTier = "SELECT id_tier FROM Pelanggan WHERE id_pelanggan = ?";
            PreparedStatement psTier = conn.prepareStatement(queryTier);
            psTier.setString(1, app.getLoggedinUserID());
            ResultSet rsTier = psTier.executeQuery();
            if (rsTier.next()) {
                String idTier = rsTier.getString("id_tier");
                switch (idTier) {
                    case "TR02": persentaseDiskonTier = 0.02; break;
                    case "TR03": persentaseDiskonTier = 0.04; break;
                    case "TR04": persentaseDiskonTier = 0.06; break;
                    case "TR05": persentaseDiskonTier = 0.08; break;
                }
            }
            rsTier.close();
            psTier.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, "Gagal mengambil data Tier: " + e.getMessage());
            return;
        }

        double nominalDiskonTier = hargaSub * persentaseDiskonTier;
        double hargaSubSetelahTier = hargaSub - nominalDiskonTier;
        double hargaAkhir = ongkir + hargaSubSetelahTier;
        String idVoucherDipakai = null;

        // Voucher validation (copy dari App.checkoutRun)
        if (!app.getTfVoucherText().isEmpty()) {
            try {
                String query = "SELECT v.id_voucher,v.min_belanja,v.tgl_mulai,v.tgl_berakhir,v.kuota, " +
                        "CASE WHEN p.id_voucher IS NOT NULL THEN 'POTONGAN' " +
                        "WHEN o.id_voucher IS NOT NULL THEN 'ONGKIR' " +
                        "WHEN d.id_voucher IS NOT NULL THEN 'DISKON' " +
                        "ELSE 'TIDAK VALID' END AS tipe_voucher " +
                        "FROM Voucher v " +
                        "LEFT JOIN Potongan p ON v.id_voucher = p.id_voucher " +
                        "LEFT JOIN Ongkir o ON v.id_voucher = o.id_voucher " +
                        "LEFT JOIN Diskon d ON v.id_voucher = d.id_voucher " +
                        "WHERE v.kode = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, app.getTfVoucherText());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(app, "Invalid Kode Voucher!");
                    return;
                }
                if (rs.getInt(5) <= 0) {
                    JOptionPane.showMessageDialog(app, "Voucher habis");
                    return;
                }
                int minBelanja = rs.getInt(2);
                if (minBelanja > hargaAkhir) {
                    JOptionPane.showMessageDialog(app, "Minimal pembelian Rp. " + minBelanja);
                    return;
                }
                Date tgl_max = rs.getDate(4);
                Date tgl_min = rs.getDate(3);
                Date now = new Date(System.currentTimeMillis());
                if (!(now.after(tgl_min) && now.before(tgl_max))) {
                    JOptionPane.showMessageDialog(app, "Voucher kadaluarsa");
                    return;
                }
                String tipeVoucher = rs.getString(6);
                String idVoucher = rs.getString(1);
                idVoucherDipakai = idVoucher;

                if ("POTONGAN".equalsIgnoreCase(tipeVoucher)) {
                    String queryPotongan = "SELECT nominal FROM Potongan WHERE id_voucher = ?";
                    PreparedStatement ps2 = conn.prepareStatement(queryPotongan);
                    ps2.setString(1, idVoucher);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        int nominal = rs2.getInt("nominal");
                        hargaAkhir -= nominal;
                        if (hargaAkhir < 0) hargaAkhir = 0;
                    }
                } else if ("ONGKIR".equalsIgnoreCase(tipeVoucher)) {
                    String queryOngkir = "SELECT persen_diskon, maks_diskon FROM Ongkir WHERE id_voucher = ?";
                    PreparedStatement ps2 = conn.prepareStatement(queryOngkir);
                    ps2.setString(1, idVoucher);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        double persenDiskon = rs2.getDouble("persen_diskon");
                        int maksDiskon = rs2.getInt("maks_diskon");
                        int potongan = (int) (ongkir * (persenDiskon / 100.0));
                        if (potongan > maksDiskon) potongan = maksDiskon;
                        double ongkirAkhir = ongkir - potongan;
                        if (ongkirAkhir < 0) ongkirAkhir = 0;
                        hargaAkhir = hargaSub + ongkirAkhir;
                    }
                } else if ("DISKON".equalsIgnoreCase(tipeVoucher)) {
                    String queryDiskon = "SELECT persen_diskon, maks_diskon FROM Diskon WHERE id_voucher = ?";
                    PreparedStatement ps2 = conn.prepareStatement(queryDiskon);
                    ps2.setString(1, idVoucher);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) {
                        double persenDiskon = rs2.getDouble("persen_diskon");
                        int maksDiskon = rs2.getInt("maks_diskon");
                        int potongan = (int) (hargaAkhir * (persenDiskon / 100.0));
                        if (potongan > maksDiskon) potongan = maksDiskon;
                        hargaAkhir -= potongan;
                        if (hargaAkhir < 0) hargaAkhir = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(app, e.getMessage());
                return;
            }
        }

        int hargaAkhirFix = (int) hargaAkhir;
        int confirm = JOptionPane.showConfirmDialog(app, "Total Pembayaran : Rp. " + hargaAkhirFix + "\nLanjut checkout?", "Konfirmasi Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            conn.setAutoCommit(false);
            LocalDate now = LocalDate.now();
            String idTransaksi = "TR" + System.currentTimeMillis() % 1000000000;
            String idPengiriman = "PG" + System.currentTimeMillis() % 1000000000;
            int totalBerat = 0;
            for (Object[] item : keranjangItem) totalBerat += ((int) item[9] * (int) item[5]);

            // Insert Pengiriman
            String queryPengiriman = "INSERT INTO Pengiriman VALUES (?, ?)";
            PreparedStatement psPengiriman = conn.prepareStatement(queryPengiriman);
            psPengiriman.setString(1, idPengiriman);
            psPengiriman.setString(2, "Barang belum diambil");
            psPengiriman.executeUpdate();

            // Click and Collect / Delivery
            if ("Collect".equals(app.getCbPengirimanSelectedItem())) {
                String kodePengambilan = "AMB" + (int) (Math.random() * 999999);
                LocalDate batasAmbil = now.plusWeeks(1);
                String queryCollect = "INSERT INTO Click_and_Collect VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psCollect = conn.prepareStatement(queryCollect);
                psCollect.setString(1, idPengiriman);
                psCollect.setString(2, app.getTaAlamatText());
                psCollect.setDate(3, Date.valueOf(batasAmbil));
                psCollect.setString(4, kodePengambilan);
                psCollect.setString(5, "Belum Diambil");
                psCollect.executeUpdate();
            } else {
                String namaEkspedisi = app.getCbEkspedisiSelectedItem();
                String queryCariEkspedisi = "SELECT id_ekspedisi FROM Ekspedisi WHERE nama = ?";
                PreparedStatement psCari = conn.prepareStatement(queryCariEkspedisi);
                psCari.setString(1, namaEkspedisi);
                ResultSet rsEkspedisi = psCari.executeQuery();
                String idEkspedisi = "";
                if (rsEkspedisi.next()) idEkspedisi = rsEkspedisi.getString(1);
                String queryDelivery = "INSERT INTO Click_and_Deliver VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psDelivery = conn.prepareStatement(queryDelivery);
                int noResi = (int) (Math.random() * 9000) + 1000;
                psDelivery.setString(1, idPengiriman);
                psDelivery.setString(2, app.getTaAlamatText());
                psDelivery.setInt(3, noResi);
                psDelivery.setInt(4, 7);
                psDelivery.setString(5, idEkspedisi);
                psDelivery.executeUpdate();
            }

            // Insert Transaksi
            String queryTransaksi = "INSERT INTO Transaksi VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psTransaksi = conn.prepareStatement(queryTransaksi);
            psTransaksi.setString(1, idTransaksi);
            psTransaksi.setDate(2, Date.valueOf(now));
            psTransaksi.setInt(3, hargaAkhirFix);
            psTransaksi.setInt(4, totalBerat);
            psTransaksi.setInt(5, (int) (hargaSub + ongkir - hargaAkhirFix));
            psTransaksi.setString(6, "Pending");
            psTransaksi.setString(7, idPengiriman);
            psTransaksi.setString(8, app.getLoggedinUserID());
            if (idVoucherDipakai != null) psTransaksi.setString(9, idVoucherDipakai);
            else psTransaksi.setNull(9, Types.VARCHAR);
            psTransaksi.executeUpdate();

            // Metode Pembayaran
            String method = app.getCbMethodSelectedItem();
            if ("Bank".equals(method)) {
                String queryBank = "INSERT INTO Transfer_Bank VALUES (?, ?, ?)";
                PreparedStatement psBank = conn.prepareStatement(queryBank);
                psBank.setString(1, idTransaksi);
                psBank.setString(2, app.getCbOpsiSelectedItem());
                psBank.setString(3, app.getTfOpsiText());
                psBank.executeUpdate();
            } else if ("Kredit".equals(method)) {
                String queryKredit = "INSERT INTO Kredit VALUES (?, ?, ?, ?)";
                PreparedStatement psKredit = conn.prepareStatement(queryKredit);
                psKredit.setString(1, idTransaksi);
                psKredit.setString(2, app.getCbOpsiSelectedItem());
                psKredit.setDate(3, Date.valueOf(LocalDate.of(2030, 1, 1)));
                psKredit.setString(4, app.getTfOpsiText());
                psKredit.executeUpdate();
            } else if ("Dompet Digital".equals(method)) {
                String queryDompet = "INSERT INTO Dompet_Digital VALUES (?, ?, ?)";
                PreparedStatement psDompet = conn.prepareStatement(queryDompet);
                psDompet.setString(1, idTransaksi);
                psDompet.setString(2, app.getCbOpsiSelectedItem());
                psDompet.setString(3, app.getTfOpsiText());
                psDompet.executeUpdate();
            }

            // Detail Transaksi & update stok
            for (Object[] item : keranjangItem) {
                String queryDetail = "INSERT INTO Detail_Transaksi VALUES (?, ?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(queryDetail);
                psDetail.setString(1, idTransaksi);
                psDetail.setString(2, item[6].toString());
                psDetail.setString(3, item[7].toString());
                psDetail.setInt(4, (int) item[5]);
                psDetail.executeUpdate();

                String queryUpdateStok = "UPDATE Varian_Produk SET stok = stok - ? WHERE id_varian = ? AND id_produk = ?";
                PreparedStatement psStok = conn.prepareStatement(queryUpdateStok);
                psStok.setInt(1, (int) item[5]);
                psStok.setString(2, item[7].toString());
                psStok.setString(3, item[6].toString());
                psStok.executeUpdate();
            }

            // Update kuota voucher
            if (idVoucherDipakai != null) {
                String queryVoucher = "UPDATE Voucher SET kuota = kuota - 1 WHERE id_voucher = ?";
                PreparedStatement psVoucher = conn.prepareStatement(queryVoucher);
                psVoucher.setString(1, idVoucherDipakai);
                psVoucher.executeUpdate();
            }

            // Insert Poin History
            int perubahanPoin = (int) (hargaAkhirFix / 100000);
            if (perubahanPoin > 0) {
                String queryPoin = "INSERT INTO Poin_History (id_history, tanggal, id_transaksi, perubahan_point, id_pelanggan) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psPoin = conn.prepareStatement(queryPoin);
                String idHistory = "HIS-" + now.toString().replace("-", "") + "-" + (int) (Math.random() * 900 + 100);
                psPoin.setString(1, idHistory);
                psPoin.setDate(2, Date.valueOf(now));
                psPoin.setString(3, idTransaksi);
                psPoin.setInt(4, perubahanPoin);
                psPoin.setString(5, app.getLoggedinUserID());
                psPoin.executeUpdate();
                psPoin.close();
            }

            conn.commit();
            JOptionPane.showMessageDialog(app, "Checkout berhasil!");
            keranjangItem.clear();
            app.refreshDataPengguna();

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            JOptionPane.showMessageDialog(app, e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}