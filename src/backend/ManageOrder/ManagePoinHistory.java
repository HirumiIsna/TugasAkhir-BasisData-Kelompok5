package src.backend.ManageOrder;

import src.backend.database.PoinHistoryDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ManagePoinHistory {
    private PoinHistoryDAO poinHistoryDAO;
    private Runnable refreshCallback;

    public ManagePoinHistory(Connection conn, Runnable refreshCallback) {
        this.poinHistoryDAO  = new PoinHistoryDAO(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataPoinHistory(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID History");
        model.addColumn("Tanggal");
        model.addColumn("ID Pelanggan");
        model.addColumn("Nama Pelanggan");
        model.addColumn("Perubahan Poin");
        model.addColumn("ID Transaksi");

        List<Map<String, Object>> list = poinHistoryDAO.getAll();
        for (Map<String, Object> ph : list) {
            model.addRow(new Object[]{
                    ph.get("id_history"),
                    ph.get("tanggal"),
                    ph.get("id_pelanggan"),
                    ph.get("pelanggan_nama"),
                    ph.get("perubahan_point"),
                    ph.get("id_transaksi") != null ? ph.get("id_transaksi") : "-"
            });
        }
        table.setModel(model);
    }

    public void insertPoinHistory(JTable table, String idPelanggan, String idTransaksi, int perubahanPoin) {
        if (idPelanggan.isEmpty()) {
            JOptionPane.showMessageDialog(null, "ID Pelanggan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String transId = idTransaksi.isEmpty() ? null : idTransaksi;
        boolean success = poinHistoryDAO.insert(idPelanggan, transId, perubahanPoin);
        if (success) {
            JOptionPane.showMessageDialog(null, "Berhasil menambahkan riwayat poin!");
            if (refreshCallback != null) refreshCallback.run();
            else loadDataPoinHistory(table);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Gagal menambahkan riwayat poin. Periksa kembali ID Pelanggan!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}