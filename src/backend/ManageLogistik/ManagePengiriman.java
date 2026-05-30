package src.backend.ManageLogistik;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import src.backend.database.PengirimanDAO;
import src.backend.database.TransaksiDAO;

public class ManagePengiriman {
    private PengirimanDAO pengirimanDAO;
    private TransaksiDAO transaksiDAO;
    private Runnable refreshCallback;

    public ManagePengiriman(Connection conn) {
        this.pengirimanDAO = new PengirimanDAO(conn);
        this.transaksiDAO = new TransaksiDAO(conn);
    }

    public ManagePengiriman(Connection conn, Runnable refreshCallback) {
        this(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataPengiriman(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Pengiriman");
        model.addColumn("Status");
        model.addColumn("ID Transaksi");
        model.addColumn("Pelanggan");

        List<Map<String, Object>> transaksiList = transaksiDAO.getAll();

        for (Map<String, Object> t : transaksiList) {
            String idPengiriman = (String) t.get("id_pengiriman");
            Map<String, Object> pengiriman = pengirimanDAO.getById(idPengiriman);
            if (pengiriman != null) {
                model.addRow(new Object[]{
                        idPengiriman,
                        pengiriman.get("status"),
                        t.get("id_transaksi"),
                        t.get("pelanggan")
                });
            }
        }
        table.setModel(model);
    }

    public void trackPengiriman(JTextArea resultArea, String idTransaksi) {
        if (idTransaksi == null || idTransaksi.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Masukkan ID Transaksi!");
            return;
        }

        Map<String, Object> tracking = pengirimanDAO.trackByTransaksiId(idTransaksi);
        if (tracking.isEmpty()) {
            resultArea.setText("Data pengiriman tidak ditemukan!");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("=== STATUS PENGIRIMAN ===\n\n");
            sb.append("Status Pengiriman: ").append(tracking.get("status_pengiriman")).append("\n");

            if (tracking.get("alamat") != null) {
                sb.append("\n--- CLICK & DELIVER ---\n");
                sb.append("Alamat: ").append(tracking.get("alamat")).append("\n");
                sb.append("No Resi: ").append(tracking.get("no_resi")).append("\n");
                sb.append("Ekspedisi: ").append(tracking.get("ekspedisi")).append("\n");
                sb.append("Estimasi: ").append(tracking.get("estimasi")).append(" hari\n");
            }

            if (tracking.get("alamat_gerai") != null) {
                sb.append("\n--- CLICK & COLLECT ---\n");
                sb.append("Alamat Gerai: ").append(tracking.get("alamat_gerai")).append("\n");
                sb.append("Kode Pengambilan: ").append(tracking.get("kode_pengambilan")).append("\n");
                sb.append("Status Pengambilan: ").append(tracking.get("status_pengembalian")).append("\n");
            }

            resultArea.setText(sb.toString());
        }
    }

    public void updateStatusPengiriman(JTable tabel, String idPengiriman, String statusBaru) {
        if (idPengiriman == null || idPengiriman.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih pengiriman terlebih dahulu!");
            return;
        }

        Map<String, Object> existing = pengirimanDAO.getById(idPengiriman);
        if (existing == null) {
            JOptionPane.showMessageDialog(null, "Pengiriman tidak ditemukan!");
            return;
        }

        if (pengirimanDAO.updateStatus(idPengiriman, statusBaru)) {
            JOptionPane.showMessageDialog(null, "Status pengiriman berhasil diupdate");
            loadDataPengiriman(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate status pengiriman!");
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}