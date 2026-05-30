package src.backend.ManagePromo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import src.backend.database.TierLoyalitasDAO;

public class ManageTierLoyalitas {
    private TierLoyalitasDAO tierDAO;
    private Runnable refreshCallback;

    public ManageTierLoyalitas(Connection conn) {
        this.tierDAO = new TierLoyalitasDAO(conn);
    }

    public ManageTierLoyalitas(Connection conn, Runnable refreshCallback) {
        this(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataTier(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Tier");
        model.addColumn("Nama Tier");
        model.addColumn("Min Poin");
        model.addColumn("Benefit");

        List<Map<String, Object>> tierList = tierDAO.getAll();

        for (Map<String, Object> t : tierList) {
            model.addRow(new Object[]{
                    t.get("id_tier"),
                    t.get("nama_tier"),
                    t.get("min_poin"),
                    t.get("benefit")
            });
        }
        table.setModel(model);
    }

    public void insertTier(JTable tabel, String idTier, String namaTier, int minPoin, String benefit) {
        if (tierDAO.getById(idTier) != null) {
            JOptionPane.showMessageDialog(null, "ID Tier sudah ada! Gunakan ID yang berbeda.");
            return;
        }

        if (tierDAO.insert(idTier, namaTier, minPoin, benefit)) {
            JOptionPane.showMessageDialog(null, "Tier berhasil ditambahkan");
            loadDataTier(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menambahkan tier!");
        }
    }

    public void updateTier(JTable tabel, String idTier, String namaTier, int minPoin, String benefit) {
        if (tierDAO.getById(idTier) == null) {
            JOptionPane.showMessageDialog(null, "Tier tidak ditemukan! Gunakan Create untuk data baru.");
            return;
        }

        if (tierDAO.update(idTier, namaTier, minPoin, benefit)) {
            JOptionPane.showMessageDialog(null, "Tier berhasil diupdate");
            loadDataTier(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate tier!");
        }
    }

    public void deleteTier(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idTier = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus tier " + idTier + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (tierDAO.delete(idTier)) {
                JOptionPane.showMessageDialog(null, "Tier berhasil dihapus");
                loadDataTier(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus tier! Masih digunakan oleh pelanggan.");
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}