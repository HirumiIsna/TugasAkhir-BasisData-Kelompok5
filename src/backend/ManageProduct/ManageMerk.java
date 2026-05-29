package src.backend.ManageProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import src.database.MerkDAO;

public class ManageMerk {
    private MerkDAO merkDAO;
    private Runnable refreshCallback;

    public ManageMerk(Connection conn) {
        this.merkDAO = new MerkDAO(conn);
    }

    public ManageMerk(Connection conn, Runnable refreshCallback) {
        this.merkDAO = new MerkDAO(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataMerk(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Merk");
        model.addColumn("Nama");
        model.addColumn("Deskripsi");

        List<Map<String, String>> merkList = merkDAO.getAllWithDesc();

        for (Map<String, String> merk : merkList) {
            model.addRow(new Object[]{
                    merk.get("id_merk"),
                    merk.get("nama"),
                    merk.get("deskripsi")
            });
        }
        table.setModel(model);
    }

    public void insertMerk(JTable tabel, String idMerk, String namaMerk, String deskripsiMerk) {
        Map<String, Object> existing = merkDAO.getById(idMerk);
        if (existing != null) {
            JOptionPane.showMessageDialog(null,
                    "ID Merk sudah ada. Gunakan ID yang berbeda.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Map<String, String>> allMerk = merkDAO.getAll();
        for (Map<String, String> m : allMerk) {
            if (m.get("nama").equalsIgnoreCase(namaMerk)) {
                JOptionPane.showMessageDialog(null,
                        "Nama Merk sudah ada. Gunakan nama yang berbeda.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (merkDAO.insert(idMerk, namaMerk, deskripsiMerk)) {
            JOptionPane.showMessageDialog(null, "Merk berhasil ditambahkan");
            loadDataMerk(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan merk.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateMerk(JTable tabel, String idMerk, String namaMerk, String deskripsi) {
        Map<String, Object> existing = merkDAO.getById(idMerk);
        if (existing == null) {
            JOptionPane.showMessageDialog(null,
                    "Data merk dengan ID " + idMerk + " tidak ditemukan. Gunakan Create untuk data baru.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (merkDAO.update(idMerk, namaMerk, deskripsi)) {
            JOptionPane.showMessageDialog(null, "Merk berhasil diupdate");
            loadDataMerk(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate merk.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteMerk(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idMerk = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus merk " + idMerk + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (merkDAO.delete(idMerk)) {
                JOptionPane.showMessageDialog(null, "Merk berhasil dihapus");
                loadDataMerk(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus merk. Masih digunakan oleh produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}