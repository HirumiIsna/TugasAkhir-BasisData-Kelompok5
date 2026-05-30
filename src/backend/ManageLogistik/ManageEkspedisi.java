package src.backend.ManageLogistik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import src.backend.database.EkspedisiDAO;

public class ManageEkspedisi {
    private EkspedisiDAO ekspedisiDAO;
    private Runnable refreshCallback;

    public ManageEkspedisi(Connection conn) {
        this.ekspedisiDAO = new EkspedisiDAO(conn);
    }

    public ManageEkspedisi(Connection conn, Runnable refreshCallback) {
        this(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataEkspedisi(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Ekspedisi");
        model.addColumn("Nama");
        model.addColumn("Kode");
        model.addColumn("Status");

        List<Map<String, String>> ekspedisiList = ekspedisiDAO.getAll();

        for (Map<String, String> e : ekspedisiList) {
            model.addRow(new Object[]{
                    e.get("id_ekspedisi"),
                    e.get("nama"),
                    e.get("kode"),
                    e.get("status")
            });
        }
        table.setModel(model);
    }

    public void insertEkspedisi(JTable tabel, String idEkspedisi, String nama, String kode, String status) {
        if (ekspedisiDAO.getById(idEkspedisi) != null) {
            JOptionPane.showMessageDialog(null, "ID Ekspedisi sudah ada! Gunakan ID yang berbeda.");
            return;
        }

        if (ekspedisiDAO.insert(idEkspedisi, nama, kode, status)) {
            JOptionPane.showMessageDialog(null, "Ekspedisi berhasil ditambahkan");
            loadDataEkspedisi(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan ekspedisi!");
        }
    }

    public void updateEkspedisi(JTable tabel, String idEkspedisi, String nama, String kode, String status) {
        if (ekspedisiDAO.getById(idEkspedisi) == null) {
            JOptionPane.showMessageDialog(null, "Ekspedisi tidak ditemukan! Gunakan Create untuk data baru.");
            return;
        }

        if (ekspedisiDAO.update(idEkspedisi, nama, kode, status)) {
            JOptionPane.showMessageDialog(null, "Ekspedisi berhasil diupdate");
            loadDataEkspedisi(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate ekspedisi!");
        }
    }

    public void updateStatusEkspedisi(JTable tabel, String idEkspedisi, String status) {
        if (ekspedisiDAO.getById(idEkspedisi) == null) {
            JOptionPane.showMessageDialog(null, "Ekspedisi tidak ditemukan!");
            return;
        }

        if (ekspedisiDAO.updateStatus(idEkspedisi, status)) {
            JOptionPane.showMessageDialog(null, "Status ekspedisi berhasil diupdate");
            loadDataEkspedisi(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate status ekspedisi!");
        }
    }

    public void deleteEkspedisi(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idEkspedisi = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus ekspedisi " + idEkspedisi + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (ekspedisiDAO.delete(idEkspedisi)) {
                JOptionPane.showMessageDialog(null, "Ekspedisi berhasil dihapus");
                loadDataEkspedisi(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus ekspedisi!");
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}