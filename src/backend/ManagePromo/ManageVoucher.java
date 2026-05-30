package src.backend.ManagePromo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import src.backend.database.VoucherDAO;
import src.backend.database.DiskonDAO;
import src.backend.database.PotonganDAO;
import src.backend.database.OngkirDAO;

public class ManageVoucher {
    private VoucherDAO voucherDAO;
    private DiskonDAO diskonDAO;
    private PotonganDAO potonganDAO;
    private OngkirDAO ongkirDAO;
    private Connection conn;
    private Runnable refreshCallback;

    public ManageVoucher(Connection conn) {
        this.conn = conn;
        this.voucherDAO = new VoucherDAO(conn);
        this.diskonDAO = new DiskonDAO(conn);
        this.potonganDAO = new PotonganDAO(conn);
        this.ongkirDAO = new OngkirDAO(conn);
    }

    public ManageVoucher(Connection conn, Runnable refreshCallback) {
        this(conn);
        this.refreshCallback = refreshCallback;
    }

    public void loadDataVoucher(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Voucher");
        model.addColumn("Kode");
        model.addColumn("Min Belanja");
        model.addColumn("Tgl Mulai");
        model.addColumn("Tgl Berakhir");
        model.addColumn("Kuota");
        model.addColumn("Tipe");
        model.addColumn("Detail");

        List<Map<String, Object>> voucherList = voucherDAO.getAllWithType();

        for (Map<String, Object> v : voucherList) {
            String detail = "";
            String type = (String) v.get("type");
            if (type.equals("DISKON")) {
                detail = v.get("persen_diskon") + "% (max Rp" + v.get("maks_diskon") + ")";
            } else if (type.equals("POTONGAN")) {
                detail = "Rp" + v.get("nominal");
            } else if (type.equals("ONGKIR")) {
                detail = v.get("persen_diskon") + "% (max Rp" + v.get("maks_diskon") + ")";
            }

            model.addRow(new Object[]{
                    v.get("id_voucher"),
                    v.get("kode"),
                    "Rp " + String.format("%,d", v.get("min_belanja")),
                    v.get("tgl_mulai"),
                    v.get("tgl_berakhir"),
                    v.get("kuota"),
                    type,
                    detail
            });
        }
        table.setModel(model);
    }

    public void insertVoucherDiskon(JTable tabel, String idVoucher, String kode, int minBelanja,
                                    Date tglMulai, Date tglBerakhir, int kuota,
                                    int persenDiskon, int maksDiskon) {
        // Cek apakah ID sudah ada
        if (voucherDAO.getById(idVoucher) != null) {
            JOptionPane.showMessageDialog(null, "ID Voucher sudah ada! Gunakan ID yang berbeda.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            if (voucherDAO.insertBase(idVoucher, kode, minBelanja, tglMulai, tglBerakhir, kuota)) {
                if (diskonDAO.insert(idVoucher, persenDiskon, maksDiskon)) {
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Voucher Diskon berhasil ditambahkan");
                    loadDataVoucher(tabel);
                    refreshAllTabs();
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Gagal menambahkan detail diskon!");
                }
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal menambahkan voucher!");
            }
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void insertVoucherPotongan(JTable tabel, String idVoucher, String kode, int minBelanja,
                                      Date tglMulai, Date tglBerakhir, int kuota, int nominal) {
        if (voucherDAO.getById(idVoucher) != null) {
            JOptionPane.showMessageDialog(null, "ID Voucher sudah ada! Gunakan ID yang berbeda.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            if (voucherDAO.insertBase(idVoucher, kode, minBelanja, tglMulai, tglBerakhir, kuota)) {
                if (potonganDAO.insert(idVoucher, nominal)) {
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Voucher Potongan berhasil ditambahkan");
                    loadDataVoucher(tabel);
                    refreshAllTabs();
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Gagal menambahkan detail potongan!");
                }
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal menambahkan voucher!");
            }
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void insertVoucherOngkir(JTable tabel, String idVoucher, String kode, int minBelanja,
                                    Date tglMulai, Date tglBerakhir, int kuota,
                                    int persenDiskon, int maksDiskon) {
        if (voucherDAO.getById(idVoucher) != null) {
            JOptionPane.showMessageDialog(null, "ID Voucher sudah ada! Gunakan ID yang berbeda.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            if (voucherDAO.insertBase(idVoucher, kode, minBelanja, tglMulai, tglBerakhir, kuota)) {
                if (ongkirDAO.insert(idVoucher, persenDiskon, maksDiskon)) {
                    conn.commit();
                    JOptionPane.showMessageDialog(null, "Voucher Ongkir berhasil ditambahkan");
                    loadDataVoucher(tabel);
                    refreshAllTabs();
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, "Gagal menambahkan detail ongkir!");
                }
            } else {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Gagal menambahkan voucher!");
            }
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void updateVoucher(JTable tabel, String idVoucher, int kuota, Date tglMulai, Date tglBerakhir) {
        Map<String, Object> existing = voucherDAO.getById(idVoucher);
        if (existing == null) {
            JOptionPane.showMessageDialog(null, "Voucher tidak ditemukan! Gunakan Create untuk data baru.");
            return;
        }

        if (voucherDAO.updateKuota(idVoucher, kuota) && voucherDAO.updateDates(idVoucher, tglMulai, tglBerakhir)) {
            JOptionPane.showMessageDialog(null, "Voucher berhasil diupdate");
            loadDataVoucher(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengupdate voucher!");
        }
    }

    public void deleteVoucher(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idVoucher = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null,
                "Yakin ingin menghapus voucher " + idVoucher + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (voucherDAO.delete(idVoucher)) {
                JOptionPane.showMessageDialog(null, "Voucher berhasil dihapus");
                loadDataVoucher(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus voucher!");
            }
        }
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}