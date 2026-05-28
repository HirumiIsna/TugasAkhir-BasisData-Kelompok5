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
    private JTable currentTable;

    private List<String> selectedKategoriIds = new ArrayList<>();
    private List<String> selectedKategoriNames = new ArrayList<>();

    private JLabel lblKategoriTerpilih;
    private JButton btnPilihKategori;

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

    public void initializeComboBox(JComboBox<String> cmbMerk, JComboBox<String> cmbKategori, JComboBox<String> cmbPemasok) {
        loadComboMerk(cmbMerk);
        loadComboKategori(cmbKategori);
        loadComboPemasok(cmbPemasok);
    }

    public void initializeComboBoxForProduk(JComboBox<String> cmbMerk, JComboBox<String> cmbPemasok,
                                            JLabel lblKategori, JButton btnPilih) {
        loadComboMerk(cmbMerk);
        loadComboPemasok(cmbPemasok);
        this.lblKategoriTerpilih = lblKategori;
        this.btnPilihKategori = btnPilih;

        if (btnPilih != null) {
            btnPilih.addActionListener(e -> showKategoriDialog());
        }
        updateKategoriLabel();
    }

    public void loadComboMerk(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        List<Map<String, String>> merkList = merkDAO.getAll();
        for (Map<String, String> merk : merkList) {
            comboBox.addItem(merk.get("nama"));
        }
    }

    public void loadComboKategori(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        List<Map<String, String>> kategoriList = kategoriDAO.getAll();
        for (Map<String, String> kat : kategoriList) {
            comboBox.addItem(kat.get("nama_kategori"));
        }
    }

    public void loadComboPemasok(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        List<Map<String, Object>> pemasokList = pemasokDAO.getAll();
        for (Map<String, Object> p : pemasokList) {
            comboBox.addItem((String) p.get("nama"));
        }
    }

    private void showKategoriDialog() {
        List<String> tempSelectedIds = new ArrayList<>(selectedKategoriIds);
        List<String> tempSelectedNames = new ArrayList<>(selectedKategoriNames);

        JDialog dialog = new JDialog(parentFrame, "Pilih Kategori (Multi Pilih)", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(34, 139, 34));
        JLabel titleLabel = new JLabel("Pilih Kategori untuk Produk", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        dialog.add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Kategori Tersedia"));
        DefaultListModel<String> availableModel = new DefaultListModel<>();
        JList<String> availableList = new JList<>(availableModel);
        availableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Kategori Dipilih"));
        DefaultListModel<String> selectedModel = new DefaultListModel<>();
        JList<String> selectedList = new JList<>(selectedModel);
        selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        List<Map<String, String>> allKategori = kategoriDAO.getAll();
        for (Map<String, String> kat : allKategori) {
            availableModel.addElement(kat.get("id_kategori") + " - " + kat.get("nama_kategori"));
        }

        for (String id : selectedKategoriIds) {
            for (Map<String, String> kat : allKategori) {
                if (kat.get("id_kategori").equals(id)) {
                    String item = kat.get("id_kategori") + " - " + kat.get("nama_kategori");
                    selectedModel.addElement(item);
                    availableModel.removeElement(item);
                    break;
                }
            }
        }

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton addBtn = new JButton(">> Tambah >>");
        JButton addAllBtn = new JButton(">>> Tambah Semua >>>");
        JButton removeBtn = new JButton("<< Hapus <<");
        JButton removeAllBtn = new JButton("<<< Hapus Semua <<<");

        buttonPanel.add(addBtn);
        buttonPanel.add(addAllBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(removeAllBtn);

        addBtn.addActionListener(e -> {
            List<String> selected = availableList.getSelectedValuesList();
            for (String item : selected) {
                selectedModel.addElement(item);
                availableModel.removeElement(item);
            }
        });

        addAllBtn.addActionListener(e -> {
            for (int i = 0; i < availableModel.getSize(); i++) {
                selectedModel.addElement(availableModel.getElementAt(i));
            }
            availableModel.clear();
        });

        removeBtn.addActionListener(e -> {
            List<String> selected = selectedList.getSelectedValuesList();
            for (String item : selected) {
                selectedModel.removeElement(item);
                availableModel.addElement(item);
            }
        });

        removeAllBtn.addActionListener(e -> {
            for (int i = 0; i < selectedModel.getSize(); i++) {
                availableModel.addElement(selectedModel.getElementAt(i));
            }
            selectedModel.clear();
        });

        centerPanel.add(leftPanel);
        centerPanel.add(buttonPanel);
        centerPanel.add(rightPanel);

        leftPanel.add(new JScrollPane(availableList), BorderLayout.CENTER);
        rightPanel.add(new JScrollPane(selectedList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Batal");

        okBtn.addActionListener(e -> {
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            for (int i = 0; i < selectedModel.getSize(); i++) {
                String item = selectedModel.getElementAt(i);
                String[] parts = item.split(" - ");
                selectedKategoriIds.add(parts[0]);
                selectedKategoriNames.add(parts[1]);
            }
            updateKategoriLabel();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> {
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            selectedKategoriIds.addAll(tempSelectedIds);
            selectedKategoriNames.addAll(tempSelectedNames);
            updateKategoriLabel();
            dialog.dispose();
        });

        bottomPanel.add(okBtn);
        bottomPanel.add(cancelBtn);

        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
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

    public void loadDataProduk(JTable table) {
        this.currentTable = table;
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
            if (m.get("nama").equals(namaMerk)) {
                return m.get("id_merk");
            }
        }
        return "";
    }

    public String getIdPemasok(String namaPemasok) {
        List<Map<String, Object>> pemasokList = pemasokDAO.getAll();
        for (Map<String, Object> p : pemasokList) {
            if (p.get("nama").equals(namaPemasok)) {
                return (String) p.get("id_pemasok");
            }
        }
        return "";
    }

    public boolean isProdukExist(String idProduk) {
        Map<String, Object> produk = produkDAO.getById(idProduk);
        return produk != null;
    }

    public void insertProduk(JTable tabel, String idProduk, String status, String nama, String deskripsi,
                             String namaMerk, String namaPemasok) {
        if (selectedKategoriIds.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null,
                    "Pilih minimal satu kategori untuk produk.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isProdukExist(idProduk)) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null,
                    "Produk dengan ID " + idProduk + " sudah ada. Gunakan ID yang berbeda atau gunakan Update.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idMerk = getIdMerk(namaMerk);
        String idPemasok = getIdPemasok(namaPemasok);

        if (produkDAO.insertWithKategori(idProduk, nama, deskripsi, idMerk, idPemasok, selectedKategoriIds)) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Produk berhasil ditambahkan");
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            updateKategoriLabel();
            loadDataProduk(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Gagal menambahkan produk.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void insertProduk(JTable tabel, String idProduk, String status, String nama, String deskripsi,
                             String namaMerk, String namaPemasok, String namaKategori) {
        selectedKategoriIds.clear();
        selectedKategoriNames.clear();
        List<Map<String, String>> allKategori = kategoriDAO.getAll();
        for (Map<String, String> kat : allKategori) {
            if (kat.get("nama_kategori").equals(namaKategori)) {
                selectedKategoriIds.add(kat.get("id_kategori"));
                selectedKategoriNames.add(kat.get("nama_kategori"));
                break;
            }
        }
        updateKategoriLabel();
        insertProduk(tabel, idProduk, status, nama, deskripsi, namaMerk, namaPemasok);
    }

    public void updateProduk(JTable tabel, String idProduk, String nama, String deskripsi,
                             String namaMerk, String namaPemasok) {
        if (!isProdukExist(idProduk)) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null,
                    "Produk dengan ID " + idProduk + " tidak ditemukan. Gunakan Create untuk data baru.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedKategoriIds.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null,
                    "Pilih minimal satu kategori untuk produk.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idMerk = getIdMerk(namaMerk);
        String idPemasok = getIdPemasok(namaPemasok);

        if (produkDAO.updateWithKategori(idProduk, nama, deskripsi, "Tersedia", selectedKategoriIds)) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Produk berhasil diupdate");
            selectedKategoriIds.clear();
            selectedKategoriNames.clear();
            updateKategoriLabel();
            loadDataProduk(tabel);
            refreshAllTabs();
        } else {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Gagal mengupdate produk.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProduk(JTable tabel, String idProduk, String nama, String deskripsi,
                             String namaMerk, String namaPemasok, String namaKategori) {
        selectedKategoriIds.clear();
        selectedKategoriNames.clear();
        List<Map<String, String>> allKategori = kategoriDAO.getAll();
        for (Map<String, String> kat : allKategori) {
            if (kat.get("nama_kategori").equals(namaKategori)) {
                selectedKategoriIds.add(kat.get("id_kategori"));
                selectedKategoriNames.add(kat.get("nama_kategori"));
                break;
            }
        }
        updateKategoriLabel();
        updateProduk(tabel, idProduk, nama, deskripsi, namaMerk, namaPemasok);
    }

    public void deleteProduk(JTable tabel) {
        int row = tabel.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Pilih data terlebih dahulu.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tabel.getModel();
        String idProduk = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(parentFrame != null ? parentFrame : null,
                "Yakin ingin menghapus produk " + idProduk + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (produkDAO.delete(idProduk)) {
                JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Produk berhasil dihapus");
                loadDataProduk(tabel);
                refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(parentFrame != null ? parentFrame : null, "Gagal menghapus produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void clearSelectedKategori() {
        selectedKategoriIds.clear();
        selectedKategoriNames.clear();
        updateKategoriLabel();
    }

    public List<String> getSelectedKategoriIds() {
        return selectedKategoriIds;
    }

    public List<String> getSelectedKategoriNames() {
        return selectedKategoriNames;
    }

    private void refreshAllTabs() {
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }
}