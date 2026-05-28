package src.backend.ManageProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;
import java.util.Map;
import src.database.PemasokDAO;

public class ManagePemasok {
    private PemasokDAO pemasokDAO;
    private Runnable refreshCallback;

    public ManagePemasok(Connection conn) {
        this.pemasokDAO = new PemasokDAO(conn);
    }

    public ManagePemasok(Connection conn, Runnable refreshCallback) {
        this.pemasokDAO = new PemasokDAO(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataPemasok(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Pemasok");
        model.addColumn("Nama");
        model.addColumn("Email");
        model.addColumn("No Telp");
        model.addColumn("Alamat");

        List<Map<String, Object>> pemasokList = pemasokDAO.getAll();

        for (Map<String, Object> p : pemasokList) {
            model.addRow(new Object[]{
                    p.get("id_pemasok"),
                    p.get("nama"),
                    p.get("email"),
                    p.get("no_telp"),
                    p.get("alamat")
            });
        }
        table.setModel(model);
    }

    public void insertPemasok(JTable tabel, String idPemasok, String namaPemasok, String emailPemasok,
                              String telpPemasok, String alamatPemasok) {
        Map<String, Object> existing = pemasokDAO.getById(idPemasok);
        if (existing != null) {
            JOptionPane.showMessageDialog(null,
                    "ID Pemasok sudah ada. Gunakan ID yang berbeda.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pemasokDAO.insert(idPemasok, namaPemasok, emailPemasok, telpPemasok, alamatPemasok)) {
            JOptionPane.showMessageDialog(null, "Pemasok berhasil ditambahkan");
            loadDataPemasok(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan pemasok.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updatePemasok(JTable tabel, String idPemasok, String namaPemasok, String emailPemasok,
                              String telpPemasok, String alamatPemasok) {
        Map<String, Object> existing = pemasokDAO.getById(idPemasok);
        if (existing == null) {
            JOptionPane.showMessageDialog(null,
                    "Data pemasok dengan ID " + idPemasok + " tidak ditemukan. Gunakan Create untuk data baru.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pemasokDAO.update(idPemasok, namaPemasok, emailPemasok, telpPemasok, alamatPemasok)) {
            JOptionPane.showMessageDialog(null, "Pemasok berhasil diupdate");
            loadDataPemasok(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate pemasok.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deletePemasok(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idPemasok = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus pemasok " + idPemasok + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (pemasokDAO.delete(idPemasok)) {
                JOptionPane.showMessageDialog(null, "Pemasok berhasil dihapus");
                loadDataPemasok(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus pemasok. Masih memiliki produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}