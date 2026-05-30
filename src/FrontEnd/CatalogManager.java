package src.FrontEnd;

import src.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

public class CatalogManager {
    private final App app;
    private Connection conn;
    private ArrayList<Object[]> katalogItem;
    private DefaultTableModel tb2;

    public CatalogManager(App app, Connection conn, ArrayList<Object[]> katalogItem, DefaultTableModel tb2) {
        this.app = app;
        this.conn = conn;
        this.katalogItem = katalogItem;
        this.tb2 = tb2;
    }

    public void refreshKatalog(JComboBox<String> kategoriCB, JComboBox<String> merkCB) {
        String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                "FROM Varian_Produk vp " +
                "JOIN Produk p ON vp.id_produk = p.id_produk " +
                "JOIN Merk m ON p.id_merk = m.id_merk " +
                "WHERE p.status = 'Tersedia' " +
                "ORDER BY vp.id_produk";
        String query2 = "SELECT k.nama_kategori FROM Produk_Mempunyai_Kategori pmk " +
                "JOIN Kategori k ON k.id_kategori = pmk.id_kategori " +
                "WHERE pmk.id_produk = ?";
        String query3 = "SELECT nama_kategori FROM Kategori ORDER BY nama_kategori";
        String query4 = "SELECT nama FROM Merk ORDER BY nama";

        kategoriCB.removeAllItems();
        merkCB.removeAllItems();

        try {
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery();
            isiTabelKatalog(rs, query2);
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, e.getMessage());
        }

        try {
            PreparedStatement ps = conn.prepareStatement(query3);
            PreparedStatement ps2 = conn.prepareStatement(query4);
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            kategoriCB.addItem("-");
            merkCB.addItem("-");
            while (rs.next()) kategoriCB.addItem(rs.getString(1));
            while (rs2.next()) merkCB.addItem(rs2.getString(1));

            rs.close(); rs2.close();
            ps.close(); ps2.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, e.getMessage());
        }
    }

    private void isiTabelKatalog(ResultSet rs, String query2) throws SQLException {
        tb2.setRowCount(0);
        katalogItem.clear();
        while (rs.next()) {
            String id = rs.getString(1);
            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps2.setString(1, id);
            ResultSet rs2 = ps2.executeQuery();
            StringBuilder kategori = new StringBuilder();
            while (rs2.next()) {
                kategori.append(rs2.getString(1)).append(" ");
            }
            String kategoriFull = kategori.toString().trim();

            tb2.addRow(new Object[]{kategoriFull, rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6), rs.getInt(7)});
            katalogItem.add(new Object[]{kategoriFull, rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getInt(10)});

            rs2.close();
            ps2.close();
        }
    }

    public void filterBarang(String filterText, String selectedKategori, String selectedMerk) {
        String query2 = "SELECT k.nama_kategori FROM Produk_Mempunyai_Kategori pmk " +
                "JOIN Kategori k ON k.id_kategori = pmk.id_kategori " +
                "WHERE pmk.id_produk = ?";

        boolean noFilter  = filterText.isEmpty();
        boolean noKat     = selectedKategori.equals("-");
        boolean noMerk    = selectedMerk.equals("-");

        // Possibility 0: semua kosong — kembalikan normal
        if (noFilter && noKat && noMerk) {
            refreshKatalog(app.getKategoriCB(), app.getMerkCB());
            return;
        }

        String query;

        // Possibility 1: Cari saja
        if (!noFilter && noKat && noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND p.nama LIKE ? ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, "%" + filterText.trim() + "%");
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
            return;
        }

        // Possibility 2: Kategori saja
        if (noFilter && !noKat && noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND ? IN " +
                    "(SELECT bb.nama_kategori FROM Produk_Mempunyai_Kategori aa JOIN Kategori bb ON aa.id_kategori = bb.id_kategori WHERE p.id_produk = aa.id_produk) " +
                    "ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, selectedKategori.trim());
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
            return;
        }

        // Possibility 3: Merk saja
        if (noFilter && noKat && !noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND m.nama = ? ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, selectedMerk.trim());
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
            return;
        }

        // Possibility 4: Kategori + Merk
        if (noFilter && !noKat && !noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND m.nama = ? " +
                    "AND ? IN (SELECT bb.nama_kategori FROM Produk_Mempunyai_Kategori aa JOIN Kategori bb ON aa.id_kategori = bb.id_kategori WHERE p.id_produk = aa.id_produk) " +
                    "ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, selectedMerk.trim());
                ps.setString(2, selectedKategori.trim());
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
            return;
        }

        // Possibility 5: Cari + Merk
        if (!noFilter && noKat && !noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND p.nama LIKE ? AND m.nama = ? ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, "%" + filterText.trim() + "%");
                ps.setString(2, selectedMerk.trim());
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
            return;
        }

        // Possibility 6: Cari + Kategori
        if (!noFilter && !noKat && noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND p.nama LIKE ? " +
                    "AND ? IN (SELECT bb.nama_kategori FROM Produk_Mempunyai_Kategori aa JOIN Kategori bb ON aa.id_kategori = bb.id_kategori WHERE p.id_produk = aa.id_produk) " +
                    "ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, "%" + filterText.trim() + "%");
                ps.setString(2, selectedKategori.trim());
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
            return;
        }

        // Possibility 7: Cari + Kategori + Merk
        if (!noFilter && !noKat && !noMerk) {
            query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                    "FROM Varian_Produk vp JOIN Produk p ON vp.id_produk = p.id_produk JOIN Merk m ON p.id_merk = m.id_merk " +
                    "WHERE p.status = 'Tersedia' AND p.nama LIKE ? AND m.nama = ? " +
                    "AND ? IN (SELECT bb.nama_kategori FROM Produk_Mempunyai_Kategori aa JOIN Kategori bb ON aa.id_kategori = bb.id_kategori WHERE p.id_produk = aa.id_produk) " +
                    "ORDER BY vp.id_produk";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, "%" + filterText.trim() + "%");
                ps.setString(2, selectedMerk.trim());
                ps.setString(3, selectedKategori.trim());
                isiTabelKatalog(ps.executeQuery(), query2);
                ps.close();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(app, e.getMessage()); }
        }
    }

    public void tambahKeKeranjang(int jumlah, int selectedRow, ArrayList<Object[]> keranjangItem) {
        if (jumlah == 0) {
            JOptionPane.showMessageDialog(app, "Jumlah harus lebih dari 0");
            return;
        }
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Pilih Produk yang ingin dibeli!");
            return;
        }
        Object[] isi = katalogItem.get(selectedRow);
        String idProduk = isi[7].toString();
        String idVarian = isi[8].toString();

        boolean ditemukan = false;
        for (Object[] item : keranjangItem) {
            String idProdukKeranjang = item[6].toString();
            String idVarianKeranjang = item[7].toString();
            if (idProdukKeranjang.equals(idProduk) && idVarianKeranjang.equals(idVarian)) {
                int jumlahLama = (int) item[5];
                int total = jumlahLama + jumlah;
                if (total > (int) item[8]) {
                    JOptionPane.showMessageDialog(app, "Jumlah melebihi stok!", "Exceeded From Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item[5] = jumlahLama + jumlah;
                ditemukan = true;
                break;
            }
        }
        if (!ditemukan) {
            if (jumlah > (int) isi[5]) {
                JOptionPane.showMessageDialog(app, "Jumlah melebihi stok!", "Exceeded From Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }
            keranjangItem.add(new Object[]{isi[1], isi[2], isi[3], isi[4], isi[6], jumlah, isi[7], isi[8], isi[5], isi[9]});
        }
        JOptionPane.showMessageDialog(app, "Pesanan berhasil ditambah!", "Success!", JOptionPane.INFORMATION_MESSAGE);
    }
}