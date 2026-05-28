package src.backend.ManageProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;
import java.util.Map;
import src.database.VarianProdukDAO;
import src.database.ProdukDAO;

public class ManageVarian {
    private VarianProdukDAO varianDAO;
    private ProdukDAO produkDAO;
    private Runnable refreshCallback;

    public ManageVarian(Connection conn) {
        this.varianDAO = new VarianProdukDAO(conn);
        this.produkDAO = new ProdukDAO(conn);
    }

    public ManageVarian(Connection conn, Runnable refreshCallback) {
        this.varianDAO = new VarianProdukDAO(conn);
        this.produkDAO = new ProdukDAO(conn);
        this.refreshCallback = refreshCallback;
    }

    public void initializeComboBox(JComboBox<String> cmbIDProduk) {
        loadComboIDProduk(cmbIDProduk);
    }

    public void loadComboIDProduk(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        List<Map<String, Object>> produkList = produkDAO.getAllForAdmin();
        for (Map<String, Object> p : produkList) {
            comboBox.addItem((String) p.get("id_produk"));
        }
    }

    public void loadDataVarian(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Produk");
        model.addColumn("ID Varian");
        model.addColumn("Ukuran");
        model.addColumn("Warna");
        model.addColumn("Berat");
        model.addColumn("Stok");
        model.addColumn("Harga");
        model.addColumn("Barcode");

        List<Map<String, Object>> varianList = varianDAO.getAllForAdmin();

        for (Map<String, Object> v : varianList) {
            model.addRow(new Object[]{
                    v.get("id_produk"),
                    v.get("id_varian"),
                    v.get("ukuran"),
                    v.get("warna"),
                    v.get("berat"),
                    v.get("stok"),
                    v.get("harga"),
                    v.get("barcode")
            });
        }
        table.setModel(model);
    }

    public boolean isVarianExist(String idProduk, String idVarian) {
        return varianDAO.exists(idProduk, idVarian);
    }

    public void insertVarian(JTable tabel, String idProduk, String idVarian, String ukuran, String warna,
                             int berat, int stok, int harga, String barcode) {
        if (isVarianExist(idProduk, idVarian)) {
            JOptionPane.showMessageDialog(null,
                    "Varian dengan ID " + idVarian + " untuk produk " + idProduk + " sudah ada. Gunakan ID yang berbeda atau gunakan Update.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (varianDAO.insert(idProduk, idVarian, ukuran, warna, berat, stok, harga, barcode)) {
            JOptionPane.showMessageDialog(null, "Varian berhasil ditambahkan");
            loadDataVarian(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan varian.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateVarian(JTable tabel, String idProduk, String idVarian, String ukuran, String warna,
                             int berat, int stok, int harga, String barcode) {
        if (!isVarianExist(idProduk, idVarian)) {
            JOptionPane.showMessageDialog(null,
                    "Varian dengan ID " + idVarian + " untuk produk " + idProduk + " tidak ditemukan. Gunakan Create untuk data baru.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (varianDAO.update(idProduk, idVarian, ukuran, warna, berat, stok, harga, barcode)) {
            JOptionPane.showMessageDialog(null, "Varian berhasil diupdate");
            loadDataVarian(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate varian.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteVarian(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idProduk = model.getValueAt(row, 0).toString();
        String idVarian = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus varian " + idVarian + " dari produk " + idProduk + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (varianDAO.delete(idProduk, idVarian)) {
                JOptionPane.showMessageDialog(null, "Varian berhasil dihapus");
                loadDataVarian(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus varian. Varian sudah pernah terjual.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}