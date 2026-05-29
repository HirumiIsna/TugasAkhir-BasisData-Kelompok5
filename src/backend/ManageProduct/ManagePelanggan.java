package src.backend.ManageProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import src.database.PelangganDAO;

public class ManagePelanggan {
    private PelangganDAO pelangganDAO;
    private Runnable refreshCallback;

    public ManagePelanggan(Connection conn) {
        this.pelangganDAO = new PelangganDAO(conn);
    }

    public ManagePelanggan(Connection conn, Runnable refreshCallback) {
        this.pelangganDAO = new PelangganDAO(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataPelanggan(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Pelanggan");
        model.addColumn("Nama");
        model.addColumn("Email");
        model.addColumn("No Telp");
        model.addColumn("Alamat Utama");
        model.addColumn("Tgl Daftar");
        model.addColumn("Tier Loyalitas");

        List<Map<String, Object>> pelangganList = pelangganDAO.getAll();

        for (Map<String, Object> p : pelangganList) {
            model.addRow(new Object[]{
                    p.get("id_pelanggan"),
                    p.get("nama"),
                    p.get("email"),
                    p.get("no_telp"),
                    p.get("alamat_utama"),
                    p.get("tgl_daftar"),
                    // Menggabungkan nama tier dan ID tier agar informatif
                    p.get("nama_tier") + " (" + p.get("id_tier") + ")"
            });
        }
        table.setModel(model);
    }

    public void insertPelanggan(JTable table, String id, String nama, String telp, String alamat) {
        if (pelangganDAO.insert(id, nama, "", telp, alamat)) {
            JOptionPane.showMessageDialog(null, "Pelanggan berhasil disimpan.");
            loadDataPelanggan(table);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan pelanggan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updatePelanggan(JTable table, String id, String nama, String telp, String alamat) {
        boolean s1 = pelangganDAO.updateNama(id, nama);
        boolean s2 = pelangganDAO.updateNoTelp(id, telp);
        boolean s3 = pelangganDAO.updateAlamat(id, alamat);

        if (s1 && s2 && s3) {
            JOptionPane.showMessageDialog(null, "Pelanggan berhasil diupdate.");
            loadDataPelanggan(table);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate pelanggan.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method (opsional) jika admin diberikan hak untuk menghapus pelanggan
    public void deletePelanggan(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data pelanggan terlebih dahulu.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idPelanggan = model.getValueAt(row, 0).toString();
        String nama = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus pelanggan " + nama + " (" + idPelanggan + ")?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (pelangganDAO.delete(idPelanggan)) {
                JOptionPane.showMessageDialog(null, "Pelanggan berhasil dihapus.");
                loadDataPelanggan(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Gagal menghapus pelanggan. Mungkin pelanggan masih memiliki riwayat transaksi atau poin.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}