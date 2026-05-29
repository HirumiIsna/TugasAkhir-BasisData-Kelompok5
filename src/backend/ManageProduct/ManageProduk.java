package src.backend.ManageProduct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import src.database.ProdukDAO;
import src.database.KategoriDAO;
import src.database.MerkDAO;
import src.database.PemasokDAO;

public class ManageProduk {
    private ProdukDAO produkDAO;
    private KategoriDAO kategoriDAO;
    private MerkDAO merkDAO;
    private PemasokDAO pemasokDAO;
    private JFrame parentFrame;
    private Runnable refreshCallback;

    // Untuk multi kategori
    private List<String> selectedKategoriIds = new ArrayList<>();
    private List<String> selectedKategoriNames = new ArrayList<>();
    private JButton btnPilihKategori;
    private JLabel lblKategoriTerpilih;

    public ManageProduk(Connection conn) {
        this.produkDAO = new ProdukDAO(conn);
        this.kategoriDAO = new KategoriDAO(conn);
        this.merkDAO = new MerkDAO(conn);
        this.pemasokDAO = new PemasokDAO(conn);
    }

    public ManageProduk(Connection conn, JFrame parentFrame, Runnable refreshCallback) {
        this(conn);
        this.parentFrame = parentFrame;
        this.refreshCallback = refreshCallback;
    }

    public void setKategoriComponents(JButton btnPilihKategori, JLabel lblKategoriTerpilih) {
        this.btnPilihKategori = btnPilihKategori;
        this.lblKategoriTerpilih = lblKategoriTerpilih;

        if (btnPilihKategori != null) {
            btnPilihKategori.addActionListener(e -> showKategoriDialog());
        }
    }

    private void showKategoriDialog() {
        // Simpan pilihan lama
        List<String> tempIds = new ArrayList<>(selectedKategoriIds);
        List<String> tempNames = new ArrayList<>(selectedKategoriNames);

        JDialog dialog = new JDialog(parentFrame, "Pilih Kategori", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());

        // Panel untuk checkbox list
        JPanel panelCheckbox = new JPanel();
        panelCheckbox.setLayout(new BoxLayout(panelCheckbox, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(panelCheckbox);

        // Load semua kategori
        List<Map<String, String>> allKategori = kategoriDAO.getAll();
        List<JCheckBox> checkBoxList = new ArrayList<>();
        List<String> kategoriIdList = new ArrayList<>();
        List<String> kategoriNamaList = new ArrayList<>();

        for (Map<String, String> kat : allKategori) {
            String id = kat.get("id_kategori");
            String nama = kat.get("nama_kategori");
            kategoriIdList.add(id);
            kategoriNamaList.add(nama);

            JCheckBox cb = new JCheckBox(nama);
            cb.setSelected(selectedKategoriIds.contains(id));
            checkBoxList.add(cb);
            panelCheckbox.add(cb);
        }

        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Batal");
        JButton clearBtn = new JButton("Clear All");

        okBtn.addActionListener(e -> {
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            for (int i = 0; i < checkBoxList.size(); i++) {
                if (checkBoxList.get(i).isSelected()) {
                    selectedKategoriIds.add(kategoriIdList.get(i));
                    selectedKategoriNames.add(kategoriNamaList.get(i));
                }
            }
            updateKategoriLabel();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> {
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            selectedKategoriIds.addAll(tempIds);
            selectedKategoriNames.addAll(tempNames);
            updateKategoriLabel();
            dialog.dispose();
        });

        clearBtn.addActionListener(e -> {
            for (JCheckBox cb : checkBoxList) {
                cb.setSelected(false);
            }
        });

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(clearBtn);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateKategoriLabel() {
        if (lblKategoriTerpilih != null) {
            StringBuilder sb = new StringBuilder();
            for (String name : selectedKategoriNames) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(name);
            }
            lblKategoriTerpilih.setText(sb.length() > 0 ? sb.toString() : "- Belum pilih kategori -");
        }
    }

    public List<String> getSelectedKategoriIds() {
        return selectedKategoriIds;
    }

    public void loadSelectedKategori(String idProduk) {
        selectedKategoriIds.clear();
        selectedKategoriNames.clear();

        List<Map<String, String>> kategoriList = produkDAO.getKategoriByProdukId(idProduk);
        for (Map<String, String> kat : kategoriList) {
            selectedKategoriIds.add(kat.get("id_kategori"));
            selectedKategoriNames.add(kat.get("nama_kategori"));
        }
        updateKategoriLabel();
    }

    public void refreshCombos() {
        // Refresh combo merk dan pemasok
        if (cmbMerk != null) {
            String selected = (String) cmbMerk.getSelectedItem();
            cmbMerk.removeAllItems();
            List<Map<String, String>> merkList = merkDAO.getAll();
            for (Map<String, String> m : merkList) {
                cmbMerk.addItem(m.get("nama"));
            }
            if (selected != null) cmbMerk.setSelectedItem(selected);
        }
        if (cmbPemasok != null) {
            String selected = (String) cmbPemasok.getSelectedItem();
            cmbPemasok.removeAllItems();
            List<Map<String, Object>> pemasokList = pemasokDAO.getAll();
            for (Map<String, Object> p : pemasokList) {
                cmbPemasok.addItem((String) p.get("nama"));
            }
            if (selected != null) cmbPemasok.setSelectedItem(selected);
        }
    }

    // Combo box references
    private JComboBox<String> cmbMerk;
    private JComboBox<String> cmbPemasok;

    public void loadComboMerk(JComboBox<String> comboBox) {
        this.cmbMerk = comboBox;
        comboBox.removeAllItems();
        List<Map<String, String>> merkList = merkDAO.getAll();
        for (Map<String, String> m : merkList) {
            comboBox.addItem(m.get("nama"));
        }
    }

    public void loadComboPemasok(JComboBox<String> comboBox) {
        this.cmbPemasok = comboBox;
        comboBox.removeAllItems();
        List<Map<String, Object>> pemasokList = pemasokDAO.getAll();
        for (Map<String, Object> p : pemasokList) {
            comboBox.addItem((String) p.get("nama"));
        }
    }

    public void loadDataProduk(JTable table) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Produk");
        model.addColumn("Nama Produk");
        model.addColumn("Deskripsi");
        model.addColumn("Merk");
        model.addColumn("Kategori");
        model.addColumn("Pemasok");

        List<Map<String, Object>> produkList = produkDAO.getAllForAdmin();
        for (Map<String, Object> produk : produkList) {
            String idProduk = (String) produk.get("id_produk");
            List<Map<String, String>> kategoriList = produkDAO.getKategoriByProdukId(idProduk);
            StringBuilder kategoriNames = new StringBuilder();
            for (int i = 0; i < kategoriList.size(); i++) {
                if (i > 0) kategoriNames.append(", ");
                kategoriNames.append(kategoriList.get(i).get("nama_kategori"));
            }
            model.addRow(new Object[]{
                    produk.get("id_produk"),
                    produk.get("nama"),
                    produk.get("deskripsi"),
                    produk.get("merk"),
                    kategoriNames.toString(),
                    produk.get("id_pemasok")
            });
        }
        table.setModel(model);
    }

    public String getIdMerk(String namaMerk) {
        List<Map<String, String>> merkList = merkDAO.getAll();
        for (Map<String, String> m : merkList) {
            if (m.get("nama").equals(namaMerk)) return m.get("id_merk");
        }
        return "";
    }

    public String getIdPemasok(String namaPemasok) {
        List<Map<String, Object>> pemasokList = pemasokDAO.getAll();
        for (Map<String, Object> p : pemasokList) {
            if (p.get("nama").equals(namaPemasok)) return (String) p.get("id_pemasok");
        }
        return "";
    }

    public boolean isProdukExist(String idProduk) {
        return produkDAO.getById(idProduk) != null;
    }

    public void insertProduk(JTable tabel, String idProduk, String status, String nama, String deskripsi,
                             String namaMerk, String namaPemasok) {
        if (selectedKategoriIds.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Pilih minimal satu kategori!");
            return;
        }
        if (isProdukExist(idProduk)) {
            JOptionPane.showMessageDialog(parentFrame, "ID Produk sudah ada!");
            return;
        }
        String idMerk = getIdMerk(namaMerk);
        String idPemasok = getIdPemasok(namaPemasok);
        if (produkDAO.insertWithKategori(idProduk, nama, deskripsi, idMerk, idPemasok, selectedKategoriIds)) {
            JOptionPane.showMessageDialog(parentFrame, "Produk berhasil ditambahkan");
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            updateKategoriLabel();
            loadDataProduk(tabel);
            refreshAllTabs();
        }
    }

    public void updateProduk(JTable tabel, String idProduk, String nama, String deskripsi,
                             String namaMerk, String namaPemasok) {
        if (!isProdukExist(idProduk)) {
            JOptionPane.showMessageDialog(parentFrame, "Produk tidak ditemukan!");
            return;
        }
        if (selectedKategoriIds.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Pilih minimal satu kategori!");
            return;
        }
        String idMerk = getIdMerk(namaMerk);
        String idPemasok = getIdPemasok(namaPemasok);
        if (produkDAO.updateWithKategori(idProduk, nama, deskripsi, "Tersedia", selectedKategoriIds)) {
            JOptionPane.showMessageDialog(parentFrame, "Produk berhasil diupdate");
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            updateKategoriLabel();
            loadDataProduk(tabel);
            refreshAllTabs();
        }
    }

    public void deleteProduk(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Pilih data terlebih dahulu");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idProduk = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Hapus produk " + idProduk + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && produkDAO.delete(idProduk)) {
            JOptionPane.showMessageDialog(parentFrame, "Produk berhasil dihapus");
            loadDataProduk(tabel);
            refreshAllTabs();
        }
    }

    public void clearSelectedKategori() {
        selectedKategoriIds.clear();
        selectedKategoriNames.clear();
        updateKategoriLabel();
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) refreshCallback.run();
    }
}