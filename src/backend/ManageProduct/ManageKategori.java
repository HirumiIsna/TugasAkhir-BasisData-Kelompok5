package src.backend.ManageProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import src.backend.database.KategoriDAO;

public class ManageKategori {
    private KategoriDAO kategoriDAO;
    private Runnable refreshCallback;

    public ManageKategori(Connection conn) {
        this.kategoriDAO = new KategoriDAO(conn);
    }

    public ManageKategori(Connection conn, Runnable refreshCallback) {
        this.kategoriDAO = new KategoriDAO(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataKategori(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Kategori");
        model.addColumn("Nama");
        model.addColumn("Deskripsi");

        List<Map<String, String>> kategoriList = kategoriDAO.getAllWithDesc();

        for (Map<String, String> kat : kategoriList) {
            model.addRow(new Object[]{
                    kat.get("id_kategori"),
                    kat.get("nama_kategori"),
                    kat.get("deskripsi")
            });
        }
        table.setModel(model);
    }

    public void insertKategori(JTable tabel, String idKategori, String namaKategori, String deskripsiKategori) {
        Map<String, Object> existing = kategoriDAO.getById(idKategori);
        if (existing != null) {
            JOptionPane.showMessageDialog(null,
                    "ID Kategori sudah ada. Gunakan ID yang berbeda.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Map<String, String>> allKategori = kategoriDAO.getAll();
        for (Map<String, String> kat : allKategori) {
            if (kat.get("nama_kategori").equalsIgnoreCase(namaKategori)) {
                JOptionPane.showMessageDialog(null,
                        "Nama Kategori sudah ada. Gunakan nama yang berbeda.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (kategoriDAO.insert(idKategori, namaKategori, deskripsiKategori)) {
            JOptionPane.showMessageDialog(null, "Kategori berhasil ditambahkan");
            loadDataKategori(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan kategori.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateKategori(JTable tabel, String idKategori, String namaKategori, String deskripsi) {
        Map<String, Object> existing = kategoriDAO.getById(idKategori);
        if (existing == null) {
            JOptionPane.showMessageDialog(null,
                    "Data kategori dengan ID " + idKategori + " tidak ditemukan. Gunakan Create untuk data baru.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (kategoriDAO.update(idKategori, namaKategori, deskripsi)) {
            JOptionPane.showMessageDialog(null, "Kategori berhasil diupdate");
            loadDataKategori(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate kategori.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteKategori(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idKategori = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus kategori " + idKategori + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (kategoriDAO.delete(idKategori)) {
                JOptionPane.showMessageDialog(null, "Kategori berhasil dihapus");
                loadDataKategori(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus kategori. Masih digunakan oleh produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}