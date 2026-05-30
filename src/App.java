package src;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import src.backend.ManageLogistik.ManageEkspedisi;
import src.backend.ManageLogistik.ManagePengiriman;
import src.backend.ManageOrder.ManagePelanggan;
import src.backend.ManageOrder.ManagePoinHistory;
import src.backend.ManageOrder.ManageTransaksi;
import src.backend.ManageProduct.ManageKategori;
import src.backend.ManageProduct.ManageMerk;
import src.backend.ManageProduct.ManagePemasok;
import src.backend.ManageProduct.ManageProduk;
import src.backend.ManageProduct.ManageVarian;
import src.backend.ManagePromo.ManageTierLoyalitas;
import src.backend.ManagePromo.ManageVoucher;
import src.backend.database.*;

public class App extends JFrame {
    DefaultTableModel tb1;

    // DAO
    private PelangganDAO pelangganDAO;
    private PoinHistoryDAO poinHistoryDAO;
    private TierLoyalitasDAO tierLoyalitasDAO;

    // User
    String loggedinUserID;
    String loggedinuserNama;

    // Komponen
    private JPanel MainPanel;
    private JPanel Front;
    private JPanel pageUtama;
    private JPanel Back;
    private JLabel Judul;
    private JButton buttonFront;
    private JButton buttonBack;
    private JTabbedPane tabbedPane1;
    private JTextField textField1;
    private JTextField textFieldFront;
    private JButton registAkunButton;
    private JPanel RegistP;
    private JTextField namaRegist;
    private JTextField emailRegist;
    private JTextField telpRegist;
    private JTextField alamatRegist;
    private JTextField IDRegist;
    private JButton backButton;
    private JButton daftarButton;
    private JPanel Akun;
    private JButton gantiButton;
    private JButton gantiButton1;
    private JButton gantiButton2;
    private JButton gantiButton3;
    private JButton gantiButton4;
    private JLabel akunID;
    private JLabel akunNama;
    private JLabel akunEmail;
    private JLabel akunTelp;
    private JLabel akunCreated;
    private JLabel akunAlamat;
    private JLabel akunTier;
    private JLabel akunBenefit;
    private JLabel JudulAkun;
    private JTable table1;
    private JTabbedPane tabbedPane2;
    private JTextArea ID;

    // Backend
    private ManageProduk manageProduk;
    private ManageVarian manageVarian;
    private ManageKategori manageKategori;
    private ManageMerk manageMerk;
    private ManagePemasok managePemasok;
    private ManagePelanggan managePelanggan;
    private ManageTransaksi manageTransaksi;
    private ManagePoinHistory managePoinHistory; // [BARU]

    // Komponen Backend Baru
    private ManageVoucher manageVoucher;
    private ManageTierLoyalitas manageTierLoyalitas;
    private ManageEkspedisi manageEkspedisi;
    private ManagePengiriman managePengiriman;

    // Komponen GUI Produk (yang sudah ada di form)
    // GUI Produk
    private JButton simpanButtonProduk;
    private JButton updateButtonProduk;
    private JTextField TF_IDProduk;
    private JPanel IDProduk;
    private JTextField TF_NamaProduk;
    private JPanel NamaProduk;
    private JTextField TF_DeskripsiProduk;
    private JPanel DeskripsiProduk;
    private JPanel MerkProduk;
    private JComboBox<String> CMB_MerkProduk;
    private JComboBox<String> CMB_KategoriProduk;
    private JPanel KategoriProduk;
    private JPanel PemasokProduk;
    private JComboBox<String> CMB_PemasokProduk;
    private JPanel ListMerkPanel;
    private JTable TabelManageMerk;
    private JTextField TF_IDMerk;
    private JTextField TF_NamaMerk;
    private JTextField TF_DeskripsiMerk;
    private JPanel ListKategoriPanel;
    private JTable TabelManageKategori;
    private JTextField TF_IDVarian;
    private JComboBox<String> CMB_IDProduk;
    private JTextField TF_WarnaVarian;
    private JTextField TF_StokVarian;
    private JTextField TF_HargaVarian;
    private JTable TabelManageProduk;
    private JButton refreshDataButtonProduk;
    private JButton deleteButtonProduk;
    private JButton deleteButtonVarian;
    private JButton updateButtonVarian;
    private JButton refreshDataButtonVarian;
    private JButton simpanButtonVarian;
    private JTable TabelManageVarian;
    private JButton simpanButtonKategori;
    private JButton updateButtonKategori;
    private JButton refreshDataButtonKategori;
    private JButton deleteButtonKategori;
    private JButton simpanButtonMerk;
    private JButton updateButtonMerk;
    private JButton refreshDataButtonMerk;
    private JButton deleteButtonMerk;
    private JTextField TF_BeratVarian;
    private JTextField TF_IDKategori;
    private JTextField TF_NamaKategori;
    private JTextField TF_DeskripsiKategori;
    private JTextField TF_IDPemasok;
    private JTextField TF_NamaPemasok;
    private JTextField TF_EmailPemasok;
    private JTextField TF_TelpPemasok;
    private JTextField TF_AlamatPemasok;
    private JButton updateButtonPemasok;
    private JTable TabelManagePemasok;
    private JButton refreshDataButtonPemasok;
    private JButton deleteButtonPemasok;
    private JButton simpanButtonPemasok;
    private JScrollPane JScrollPane;
    private JTextField TF_UkuranVarian;
    private JTextField TF_BarcodeVarian;

    // Komponen GUI Voucher
    private JTable TabelManageVoucher;
    private JTextField TF_IDVoucher;
    private JTextField TF_KodeVoucher;
    private JTextField TF_MinBelanjaVoucher;
    private JTextField TF_TglMulaiVoucher;
    private JTextField TF_TglBerakhirVoucher;
    private JTextField TF_KuotaVoucher;
    private JComboBox<String> CMB_TipeVoucher;
    private JTextField TF_PersenDiskon;
    private JTextField TF_MaksDiskon;
    private JTextField TF_NominalPotongan;
    private JButton simpanButtonVoucher;
    private JButton updateButtonVoucher;
    private JButton deleteButtonVoucher;
    private JButton refreshDataButtonVoucher;

    // Komponen GUI Tier Loyalitas
    private JTable TabelManageTier;
    private JTextField TF_IDTier;
    private JTextField TF_NamaTier;
    private JTextField TF_MinPoinTier;
    private JTextField TF_BenefitTier;
    private JButton simpanButtonTier;
    private JButton updateButtonTier;
    private JButton deleteButtonTier;
    private JButton refreshDataButtonTier;

    // Komponen GUI Ekspedisi
    private JTable TabelManageEkspedisi;
    private JTextField TF_IDEkspedisi;
    private JTextField TF_NamaEkspedisi;
    private JTextField TF_KodeEkspedisi;
    private JComboBox<String> CMB_StatusEkspedisi;
    private JButton simpanButtonEkspedisi;
    private JButton updateButtonEkspedisi;
    private JButton deleteButtonEkspedisi;
    private JButton refreshDataButtonEkspedisi;

    // Komponen GUI Pengiriman
    private JTable TabelManagePengiriman;
    private JTextField TF_TrackIdTransaksi;
    private JTextArea TA_TrackingResult;
    private JButton trackButton;
    private JButton refreshDataButtonPengiriman;

    
    // GUI Pelanggan
    private JTextField TF_IDPelanggan;
    private JTextField TF_NamaPelanggan;
    private JTextField TF_TelpPelanggan;
    private JTextField TF_AlamatPelanggan;
    private JButton simpanButtonPelanggan;
    private JButton updateButtonPelanggan;
    private JButton deleteButtonPelanggan;
    private JButton refreshDataButtonPelanggan;
    private JTable TabelManagePelanggan;

    // GUI Transaksi
    private JTextField TF_IDTransaksi;
    private JComboBox<String> CMB_StatusTransaksi;
    private JButton updateButtonTransaksi;
    private JButton refreshDataButtonTransaksi;
    private JTable TabelManageTransaksi;

    // GUI Poin History
    private JTextField TF_IDPelangganPoin;
    private JTextField TF_IDTransaksiPoin;
    private JTextField TF_PerubahanPoin;
    private JButton simpanButtonPoinHistory;
    private JButton refreshDataButtonPoinHistory;
    private JTable TabelManagePoinHistory;

    // Untuk mode create varian (input ID produk manual)
    private JTextField TF_IDProdukVarian;

    // Card Layout
    private CardLayout c1;

    // Constructor
    public App() {
        setContentPane(MainPanel);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pelangganDAO     = new PelangganDAO();
        poinHistoryDAO   = new PoinHistoryDAO();
        tierLoyalitasDAO = new TierLoyalitasDAO();

        c1 = (CardLayout) MainPanel.getLayout();
        c1.show(MainPanel, "pageUtama");

        buttonBack.addActionListener((e) -> c1.show(MainPanel, "Back"));
        buttonFront.addActionListener((e) -> loginFrontend());
        registAkunButton.addActionListener((e) -> c1.show(MainPanel, "regist"));
        backButton.addActionListener((e) -> c1.show(MainPanel, "pageUtama"));
        daftarButton.addActionListener((e) -> registPembeli());
        gantiButton.addActionListener((e) -> gantiInformasiAkun(1));
        gantiButton1.addActionListener((e) -> gantiInformasiAkun(2));
        gantiButton2.addActionListener((e) -> gantiInformasiAkun(3));
        gantiButton3.addActionListener((e) -> gantiInformasiAkun(4));
        gantiButton4.addActionListener((e) -> gantiInformasiAkun(5));

        tabbedPane1.addChangeListener((e) -> refreshDataPengguna());

        tb1 = new DefaultTableModel();
        tb1.addColumn("Tanggal");
        tb1.addColumn("ID Transaksi");
        tb1.addColumn("Perubahan Poin");
        table1.setModel(tb1);

        // ==================== INISIALISASI KOMPONEN FORM (tab baru) ====================
        initNewComponents();

        // ==================== INISIALISASI MANAGER (pakai method reference, bukan lambda) ====================
        // Semua manager diinisialisasi dulu sebelum callback refreshAllTabs dipakai,
        // sehingga tidak ada NullPointerException saat callback dipanggil.
        manageProduk        = new ManageProduk(DatabaseConnection.getConnection(), this, this::refreshAllTabs);
        manageVarian        = new ManageVarian(DatabaseConnection.getConnection(), this::refreshAllTabs);
        manageKategori      = new ManageKategori(DatabaseConnection.getConnection(), this::refreshAllTabs);
        manageMerk          = new ManageMerk(DatabaseConnection.getConnection(), this::refreshAllTabs);
        managePemasok       = new ManagePemasok(DatabaseConnection.getConnection(), this::refreshAllTabs);
        manageVoucher       = new ManageVoucher(DatabaseConnection.getConnection(), this::refreshAllTabs);
        manageTierLoyalitas = new ManageTierLoyalitas(DatabaseConnection.getConnection(), this::refreshAllTabs);
        manageEkspedisi     = new ManageEkspedisi(DatabaseConnection.getConnection(), this::refreshAllTabs);
        managePengiriman    = new ManagePengiriman(DatabaseConnection.getConnection(), this::refreshAllTabs);

        // ==================== TAMBAHKAN TAB BARU ====================
        try {
            tabbedPane2.addTab("Manage Voucher",        createVoucherPanel());
            tabbedPane2.addTab("Manage Tier Loyalitas", createTierPanel());
            tabbedPane2.addTab("Manage Ekspedisi",      createEkspedisiPanel());
            tabbedPane2.addTab("Manage Pengiriman",     createPengirimanPanel());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membuat tab: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // ==================== LOAD DATA AWAL ====================
        refreshAllTabs();

        // ==================== EVENT PRODUK ====================
        manageProduk.loadComboMerk(CMB_MerkProduk);
        manageProduk.loadComboPemasok(CMB_PemasokProduk);

        Runnable refreshAllTabs = () -> {
            manageProduk.loadDataProduk(TabelManageProduk);
            manageVarian.loadDataVarian(TabelManageVarian);
            manageKategori.loadDataKategori(TabelManageKategori);
            manageMerk.loadDataMerk(TabelManageMerk);
            managePemasok.loadDataPemasok(TabelManagePemasok);
            managePelanggan.loadDataPelanggan(TabelManagePelanggan);
            manageTransaksi.loadDataTransaksi(TabelManageTransaksi);
            managePoinHistory.loadDataPoinHistory(TabelManagePoinHistory); // [BARU]
            manageVarian.refreshComboIDProduk();
        };

        manageProduk      = new ManageProduk(DatabaseConnection.getConnection(), this, refreshAllTabs);
        manageVarian      = new ManageVarian(DatabaseConnection.getConnection(), refreshAllTabs);
        manageKategori    = new ManageKategori(DatabaseConnection.getConnection(), refreshAllTabs);
        manageMerk        = new ManageMerk(DatabaseConnection.getConnection(), refreshAllTabs);
        managePemasok     = new ManagePemasok(DatabaseConnection.getConnection(), refreshAllTabs);
        managePelanggan   = new ManagePelanggan(DatabaseConnection.getConnection(), refreshAllTabs);
        manageTransaksi   = new ManageTransaksi(DatabaseConnection.getConnection(), refreshAllTabs);
        managePoinHistory = new ManagePoinHistory(DatabaseConnection.getConnection(), refreshAllTabs); // [BARU]

        manageProduk.loadDataProduk(TabelManageProduk);
        manageVarian.loadDataVarian(TabelManageVarian);
        manageKategori.loadDataKategori(TabelManageKategori);
        manageMerk.loadDataMerk(TabelManageMerk);
        managePemasok.loadDataPemasok(TabelManagePemasok);
        managePelanggan.loadDataPelanggan(TabelManagePelanggan);
        manageTransaksi.loadDataTransaksi(TabelManageTransaksi);
        managePoinHistory.loadDataPoinHistory(TabelManagePoinHistory); // [BARU]

        manageProduk.loadComboMerk(CMB_MerkProduk);
        manageProduk.loadComboPemasok(CMB_PemasokProduk);
        CMB_KategoriProduk.setVisible(false);

        JButton btnPilihKategori = new JButton("Pilih Kategori");
        JLabel lblKategoriTerpilih = new JLabel("- Belum pilih kategori -");
        lblKategoriTerpilih.setForeground(Color.BLUE);
        lblKategoriTerpilih.setFont(new Font("Arial", Font.PLAIN, 11));

        Container parentKategori = CMB_KategoriProduk.getParent();
        if (parentKategori instanceof JPanel) {
            JPanel panelKategori = (JPanel) parentKategori;
            panelKategori.remove(CMB_KategoriProduk);
            panelKategori.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            panelKategori.add(btnPilihKategori);
            panelKategori.add(lblKategoriTerpilih);
            panelKategori.revalidate();
            panelKategori.repaint();
        }

        manageProduk.setKategoriComponents(btnPilihKategori, lblKategoriTerpilih);

        updateButtonTransaksi.addActionListener(e -> {
            if (TF_IDTransaksi.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih transaksi dari tabel terlebih dahulu!");
                return;
            }
            manageTransaksi.updateStatusTransaksi(TabelManageTransaksi,
                    TF_IDTransaksi.getText().trim(),
                    CMB_StatusTransaksi.getSelectedItem().toString());
            clearFormTransaksi();
        });
        refreshDataButtonTransaksi.addActionListener(e -> manageTransaksi.loadDataTransaksi(TabelManageTransaksi));
        TabelManageTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageTransaksi.getSelectedRow();
                if (row >= 0) {
                    TF_IDTransaksi.setText(TabelManageTransaksi.getValueAt(row, 0).toString());
                    CMB_StatusTransaksi.setSelectedItem(TabelManageTransaksi.getValueAt(row, 4).toString());
                }
            }
        });

        simpanButtonPoinHistory.addActionListener(e -> {
            try {
                int perubahan = Integer.parseInt(TF_PerubahanPoin.getText().trim());
                managePoinHistory.insertPoinHistory(
                        TabelManagePoinHistory,
                        TF_IDPelangganPoin.getText().trim(),
                        TF_IDTransaksiPoin.getText().trim(),
                        perubahan);
                clearFormPoinHistory();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Perubahan Poin harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        refreshDataButtonPoinHistory.addActionListener(e ->
                managePoinHistory.loadDataPoinHistory(TabelManagePoinHistory));
        TabelManagePoinHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManagePoinHistory.getSelectedRow();
                if (row >= 0) {
                    TF_IDPelangganPoin.setText(TabelManagePoinHistory.getValueAt(row, 2).toString());
                    TF_IDTransaksiPoin.setText(
                            TabelManagePoinHistory.getValueAt(row, 5) != null
                                    ? TabelManagePoinHistory.getValueAt(row, 5).toString() : "");
                    TF_PerubahanPoin.setText(TabelManagePoinHistory.getValueAt(row, 4).toString());
                }
            }
        });

        simpanButtonProduk.addActionListener(e -> {
            if (TF_IDProduk.getText().trim().isEmpty() || TF_NamaProduk.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Produk dan Nama Produk wajib diisi!");
                return;
            }
            List<String> selectedKategori = manageProduk.getSelectedKategoriIds();
            if (selectedKategori.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih minimal satu kategori untuk produk!");
                return;
            }
            manageProduk.insertProduk(TabelManageProduk,
                    TF_IDProduk.getText(), "Tersedia",
                    TF_NamaProduk.getText(), TF_DeskripsiProduk.getText(),
                    CMB_MerkProduk.getSelectedItem().toString(),
                    CMB_PemasokProduk.getSelectedItem().toString());
            clearFormProduk();
        });
        updateButtonProduk.addActionListener(e -> {
            if (TF_IDProduk.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih produk yang akan diupdate!");
                return;
            }
            List<String> selectedKategori = manageProduk.getSelectedKategoriIds();
            if (selectedKategori.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih minimal satu kategori untuk produk!");
                return;
            }
            manageProduk.updateProduk(TabelManageProduk,
                    TF_IDProduk.getText(), TF_NamaProduk.getText(),
                    TF_DeskripsiProduk.getText(),
                    CMB_MerkProduk.getSelectedItem().toString(),
                    CMB_PemasokProduk.getSelectedItem().toString());
            clearFormProduk();
        });
        deleteButtonProduk.addActionListener(e -> manageProduk.deleteProduk(TabelManageProduk));
        refreshDataButtonProduk.addActionListener(e -> {
            manageProduk.loadDataProduk(TabelManageProduk);
            manageProduk.refreshCombos();
            refreshAllTabs();
        });
        TabelManageProduk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageProduk.getSelectedRow();
                if (row >= 0) {
                    TF_IDProduk.setText(TabelManageProduk.getValueAt(row, 0).toString());
                    TF_NamaProduk.setText(TabelManageProduk.getValueAt(row, 1).toString());
                    TF_DeskripsiProduk.setText(TabelManageProduk.getValueAt(row, 2).toString());
                    CMB_MerkProduk.setSelectedItem(TabelManageProduk.getValueAt(row, 3).toString());
                    CMB_PemasokProduk.setSelectedItem(TabelManageProduk.getValueAt(row, 5).toString());
                    manageProduk.loadSelectedKategori(TabelManageProduk.getValueAt(row, 0).toString());
                }
            }
        });

        // ==================== EVENT VARIAN ====================
        TF_IDProdukVarian = new JTextField(15);
        manageVarian.setCreateModeComponents(TF_IDProdukVarian);
        manageVarian.setUpdateModeComponents(CMB_IDProduk);
        simpanButtonVarian.addActionListener(e -> {
            try {
                manageVarian.insertVarian(TabelManageVarian,
                        TF_IDProdukVarian.getText(), TF_IDVarian.getText(),
                        TF_UkuranVarian.getText(), TF_WarnaVarian.getText(),
                        Integer.parseInt(TF_BeratVarian.getText()),
                        Integer.parseInt(TF_StokVarian.getText()),
                        Integer.parseInt(TF_HargaVarian.getText()),
                        TF_BarcodeVarian.getText());
                clearFormVarian();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Berat, Stok, dan Harga harus berupa angka!");
            }
        });
        updateButtonVarian.addActionListener(e -> {
            try {
                manageVarian.updateVarian(TabelManageVarian,
                        CMB_IDProduk.getSelectedItem().toString(),
                        TF_IDVarian.getText(), TF_UkuranVarian.getText(),
                        TF_WarnaVarian.getText(),
                        Integer.parseInt(TF_BeratVarian.getText()),
                        Integer.parseInt(TF_StokVarian.getText()),
                        Integer.parseInt(TF_HargaVarian.getText()),
                        TF_BarcodeVarian.getText());
                clearFormVarian();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Berat, Stok, dan Harga harus berupa angka!");
            }
        });
        deleteButtonVarian.addActionListener(e -> manageVarian.deleteVarian(TabelManageVarian));
        refreshDataButtonVarian.addActionListener(e -> {
            manageVarian.loadDataVarian(TabelManageVarian);
            refreshAllTabs();
        });
        TabelManageVarian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageVarian.getSelectedRow();
                if (row >= 0) {
                    CMB_IDProduk.setSelectedItem(TabelManageVarian.getValueAt(row, 0).toString());
                    TF_IDVarian.setText(TabelManageVarian.getValueAt(row, 1).toString());
                    TF_UkuranVarian.setText(TabelManageVarian.getValueAt(row, 2).toString());
                    TF_WarnaVarian.setText(TabelManageVarian.getValueAt(row, 3).toString());
                    TF_BeratVarian.setText(TabelManageVarian.getValueAt(row, 4).toString());
                    TF_StokVarian.setText(TabelManageVarian.getValueAt(row, 5).toString());
                    TF_HargaVarian.setText(TabelManageVarian.getValueAt(row, 6).toString());
                    TF_BarcodeVarian.setText(TabelManageVarian.getValueAt(row, 7).toString());
                }
            }
        });

        // ==================== EVENT KATEGORI ====================
        simpanButtonKategori.addActionListener(e -> {
            if (TF_IDKategori.getText().trim().isEmpty() || TF_NamaKategori.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Kategori dan Nama Kategori wajib diisi!");
                return;
            }
            manageKategori.insertKategori(TabelManageKategori,
                    TF_IDKategori.getText(), TF_NamaKategori.getText(), TF_DeskripsiKategori.getText());
            clearFormKategori();
        });
        updateButtonKategori.addActionListener(e -> {
            if (TF_IDKategori.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih kategori yang akan diupdate!");
                return;
            }
            manageKategori.updateKategori(TabelManageKategori,
                    TF_IDKategori.getText(), TF_NamaKategori.getText(), TF_DeskripsiKategori.getText());
            clearFormKategori();
        });
        deleteButtonKategori.addActionListener(e -> manageKategori.deleteKategori(TabelManageKategori));
        refreshDataButtonKategori.addActionListener(e -> {
            manageKategori.loadDataKategori(TabelManageKategori);
            refreshAllTabs.run();
        });
        TabelManageKategori.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageKategori.getSelectedRow();
                if (row >= 0) {
                    TF_IDKategori.setText(TabelManageKategori.getValueAt(row, 0).toString());
                    TF_NamaKategori.setText(TabelManageKategori.getValueAt(row, 1).toString());
                    TF_DeskripsiKategori.setText(TabelManageKategori.getValueAt(row, 2).toString());
                }
            }
        });

        // ==================== EVENT MERK ====================
        simpanButtonMerk.addActionListener(e -> {
            if (TF_IDMerk.getText().trim().isEmpty() || TF_NamaMerk.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Merk dan Nama Merk wajib diisi!");
                return;
            }
            manageMerk.insertMerk(TabelManageMerk,
                    TF_IDMerk.getText(), TF_NamaMerk.getText(), TF_DeskripsiMerk.getText());
            clearFormMerk();
        });
        updateButtonMerk.addActionListener(e -> {
            if (TF_IDMerk.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih merk yang akan diupdate!");
                return;
            }
            manageMerk.updateMerk(TabelManageMerk,
                    TF_IDMerk.getText(), TF_NamaMerk.getText(), TF_DeskripsiMerk.getText());
            clearFormMerk();
        });
        deleteButtonMerk.addActionListener(e -> manageMerk.deleteMerk(TabelManageMerk));
        refreshDataButtonMerk.addActionListener(e -> {
            manageMerk.loadDataMerk(TabelManageMerk);
            refreshAllTabs.run();
        });
        TabelManageMerk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageMerk.getSelectedRow();
                if (row >= 0) {
                    TF_IDMerk.setText(TabelManageMerk.getValueAt(row, 0).toString());
                    TF_NamaMerk.setText(TabelManageMerk.getValueAt(row, 1).toString());
                    TF_DeskripsiMerk.setText(TabelManageMerk.getValueAt(row, 2).toString());
                }
            }
        });

        // ==================== EVENT PEMASOK ====================
        simpanButtonPemasok.addActionListener(e -> {
            if (TF_IDPemasok.getText().trim().isEmpty() || TF_NamaPemasok.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Pemasok dan Nama Pemasok wajib diisi!");
                return;
            }
            managePemasok.insertPemasok(TabelManagePemasok,
                    TF_IDPemasok.getText(), TF_NamaPemasok.getText(),
                    TF_EmailPemasok.getText(), TF_TelpPemasok.getText(), TF_AlamatPemasok.getText());
            clearFormPemasok();
        });
        updateButtonPemasok.addActionListener(e -> {
            if (TF_IDPemasok.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih pemasok yang akan diupdate!");
                return;
            }
            managePemasok.updatePemasok(TabelManagePemasok,
                    TF_IDPemasok.getText(), TF_NamaPemasok.getText(),
                    TF_EmailPemasok.getText(), TF_TelpPemasok.getText(), TF_AlamatPemasok.getText());
            clearFormPemasok();
        });
        deleteButtonPemasok.addActionListener(e -> managePemasok.deletePemasok(TabelManagePemasok));
        refreshDataButtonPemasok.addActionListener(e -> {
            managePemasok.loadDataPemasok(TabelManagePemasok);
            refreshAllTabs.run();
        });
        TabelManagePemasok.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManagePemasok.getSelectedRow();
                if (row >= 0) {
                    TF_IDPemasok.setText(TabelManagePemasok.getValueAt(row, 0).toString());
                    TF_NamaPemasok.setText(TabelManagePemasok.getValueAt(row, 1).toString());
                    TF_EmailPemasok.setText(TabelManagePemasok.getValueAt(row, 2).toString());
                    TF_TelpPemasok.setText(TabelManagePemasok.getValueAt(row, 3).toString());
                    TF_AlamatPemasok.setText(TabelManagePemasok.getValueAt(row, 4).toString());
                }
            }
        });

        simpanButtonPelanggan.addActionListener(e -> {
            if (TF_IDPelanggan.getText().trim().isEmpty() || TF_NamaPelanggan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID dan Nama Pelanggan wajib diisi!");
                return;
            }
            managePelanggan.insertPelanggan(TabelManagePelanggan,
                    TF_IDPelanggan.getText(), TF_NamaPelanggan.getText(),
                    TF_TelpPelanggan.getText(), TF_AlamatPelanggan.getText());
            clearFormPelanggan();
        });
        updateButtonPelanggan.addActionListener(e -> {
            if (TF_IDPelanggan.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih pelanggan yang akan diupdate!");
                return;
            }
            managePelanggan.updatePelanggan(TabelManagePelanggan,
                    TF_IDPelanggan.getText(), TF_NamaPelanggan.getText(),
                    TF_TelpPelanggan.getText(), TF_AlamatPelanggan.getText());
            clearFormPelanggan();
        });
        deleteButtonPelanggan.addActionListener(e -> {
            managePelanggan.deletePelanggan(TabelManagePelanggan);
            clearFormPelanggan();
        });
        refreshDataButtonPelanggan.addActionListener(e -> managePelanggan.loadDataPelanggan(TabelManagePelanggan));
        TabelManagePelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManagePelanggan.getSelectedRow();
                if (row >= 0) {
                    TF_IDPelanggan.setText(TabelManagePelanggan.getValueAt(row, 0).toString());
                    TF_NamaPelanggan.setText(TabelManagePelanggan.getValueAt(row, 1).toString());
                    TF_TelpPelanggan.setText(TabelManagePelanggan.getValueAt(row, 3).toString());
                    TF_AlamatPelanggan.setText(TabelManagePelanggan.getValueAt(row, 4).toString());
                }
            }
        });

        setVisible(true);
    }



    // ==================== REFRESH ALL TABS (method terpisah, bukan lambda) ====================
    // Dengan method terpisah, tidak ada risiko NullPointerException karena
    // null-check dilakukan setiap kali method dipanggil.
    private void refreshAllTabs() {
        if (manageProduk != null)        manageProduk.loadDataProduk(TabelManageProduk);
        if (manageVarian != null)        manageVarian.loadDataVarian(TabelManageVarian);
        if (manageKategori != null)      manageKategori.loadDataKategori(TabelManageKategori);
        if (manageMerk != null)          manageMerk.loadDataMerk(TabelManageMerk);
        if (managePemasok != null)       managePemasok.loadDataPemasok(TabelManagePemasok);
        if (manageVoucher != null)       manageVoucher.loadDataVoucher(TabelManageVoucher);
        if (manageTierLoyalitas != null) manageTierLoyalitas.loadDataTier(TabelManageTier);
        if (manageEkspedisi != null)     manageEkspedisi.loadDataEkspedisi(TabelManageEkspedisi);
        if (managePengiriman != null)    managePengiriman.loadDataPengiriman(TabelManagePengiriman);
        if (manageVarian != null)        manageVarian.refreshComboIDProduk();
    }

    private void initNewComponents() {
        TabelManageVoucher    = new JTable(); TabelManageVoucher.setModel(new DefaultTableModel());
        TabelManageTier       = new JTable(); TabelManageTier.setModel(new DefaultTableModel());
        TabelManageEkspedisi  = new JTable(); TabelManageEkspedisi.setModel(new DefaultTableModel());
        TabelManagePengiriman = new JTable(); TabelManagePengiriman.setModel(new DefaultTableModel());

        TF_IDVoucher          = new JTextField(15);
        TF_KodeVoucher        = new JTextField(15);
        TF_MinBelanjaVoucher  = new JTextField(15);
        TF_TglMulaiVoucher    = new JTextField(15);
        TF_TglBerakhirVoucher = new JTextField(15);
        TF_KuotaVoucher       = new JTextField(15);
        CMB_TipeVoucher       = new JComboBox<>(new String[]{"DISKON", "POTONGAN", "ONGKIR"});
        TF_PersenDiskon       = new JTextField(15);
        TF_MaksDiskon         = new JTextField(15);
        TF_NominalPotongan    = new JTextField(15);

        TF_IDTier      = new JTextField(15);
        TF_NamaTier    = new JTextField(15);
        TF_MinPoinTier = new JTextField(15);
        TF_BenefitTier = new JTextField(15);

        TF_IDEkspedisi      = new JTextField(15);
        TF_NamaEkspedisi    = new JTextField(15);
        TF_KodeEkspedisi    = new JTextField(15);
        CMB_StatusEkspedisi = new JComboBox<>(new String[]{"Aktif", "Nonaktif"});

        TF_TrackIdTransaksi = new JTextField(15);
        TA_TrackingResult   = new JTextArea(10, 50);
        TA_TrackingResult.setEditable(false);

        simpanButtonVoucher         = new JButton("Simpan");
        updateButtonVoucher         = new JButton("Update");
        deleteButtonVoucher         = new JButton("Delete");
        refreshDataButtonVoucher    = new JButton("Refresh");

        simpanButtonTier            = new JButton("Simpan");
        updateButtonTier            = new JButton("Update");
        deleteButtonTier            = new JButton("Delete");
        refreshDataButtonTier       = new JButton("Refresh");

        simpanButtonEkspedisi       = new JButton("Simpan");
        updateButtonEkspedisi       = new JButton("Update");
        deleteButtonEkspedisi       = new JButton("Delete");
        refreshDataButtonEkspedisi  = new JButton("Refresh");

        trackButton                 = new JButton("Track");
        refreshDataButtonPengiriman = new JButton("Refresh");
    }

    private JPanel createVoucherPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Voucher"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("ID Voucher:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_IDVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Kode Voucher:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_KodeVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Minimal Belanja:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_MinBelanjaVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Tanggal Mulai (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; formPanel.add(TF_TglMulaiVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Tanggal Berakhir (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; formPanel.add(TF_TglBerakhirVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Kuota:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_KuotaVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Tipe Voucher:"), gbc);
        gbc.gridx = 1; formPanel.add(CMB_TipeVoucher, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Persen Diskon:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_PersenDiskon, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Maks Diskon:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_MaksDiskon, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Nominal Potongan:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_NominalPotongan, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(simpanButtonVoucher);
        buttonPanel.add(updateButtonVoucher);
        buttonPanel.add(deleteButtonVoucher);
        buttonPanel.add(refreshDataButtonVoucher);

        JScrollPane scrollPane = new JScrollPane(TabelManageVoucher);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Logika enable/disable field sesuai tipe
        CMB_TipeVoucher.addActionListener(e -> {
            String tipe = (String) CMB_TipeVoucher.getSelectedItem();
            boolean isDiskonOngkir = "DISKON".equals(tipe) || "ONGKIR".equals(tipe);
            TF_PersenDiskon.setEnabled(isDiskonOngkir);
            TF_MaksDiskon.setEnabled(isDiskonOngkir);
            TF_NominalPotongan.setEnabled("POTONGAN".equals(tipe));
        });
        TF_PersenDiskon.setEnabled(true);
        TF_MaksDiskon.setEnabled(true);
        TF_NominalPotongan.setEnabled(false);

        simpanButtonVoucher.addActionListener(e -> {
            String tipe = (String) CMB_TipeVoucher.getSelectedItem();
            try {
                if ("DISKON".equals(tipe)) {
                    manageVoucher.insertVoucherDiskon(TabelManageVoucher, TF_IDVoucher.getText(), TF_KodeVoucher.getText(),
                            Integer.parseInt(TF_MinBelanjaVoucher.getText()), java.sql.Date.valueOf(TF_TglMulaiVoucher.getText()),
                            java.sql.Date.valueOf(TF_TglBerakhirVoucher.getText()), Integer.parseInt(TF_KuotaVoucher.getText()),
                            Integer.parseInt(TF_PersenDiskon.getText()), Integer.parseInt(TF_MaksDiskon.getText()));
                } else if ("POTONGAN".equals(tipe)) {
                    manageVoucher.insertVoucherPotongan(TabelManageVoucher, TF_IDVoucher.getText(), TF_KodeVoucher.getText(),
                            Integer.parseInt(TF_MinBelanjaVoucher.getText()), java.sql.Date.valueOf(TF_TglMulaiVoucher.getText()),
                            java.sql.Date.valueOf(TF_TglBerakhirVoucher.getText()), Integer.parseInt(TF_KuotaVoucher.getText()),
                            Integer.parseInt(TF_NominalPotongan.getText()));
                } else {
                    manageVoucher.insertVoucherOngkir(TabelManageVoucher, TF_IDVoucher.getText(), TF_KodeVoucher.getText(),
                            Integer.parseInt(TF_MinBelanjaVoucher.getText()), java.sql.Date.valueOf(TF_TglMulaiVoucher.getText()),
                            java.sql.Date.valueOf(TF_TglBerakhirVoucher.getText()), Integer.parseInt(TF_KuotaVoucher.getText()),
                            Integer.parseInt(TF_PersenDiskon.getText()), Integer.parseInt(TF_MaksDiskon.getText()));
                }
                clearFormVoucher();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        updateButtonVoucher.addActionListener(e -> {
            try {
                manageVoucher.updateVoucher(TabelManageVoucher, TF_IDVoucher.getText(),
                        Integer.parseInt(TF_KuotaVoucher.getText()), java.sql.Date.valueOf(TF_TglMulaiVoucher.getText()),
                        java.sql.Date.valueOf(TF_TglBerakhirVoucher.getText()));
                clearFormVoucher();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        deleteButtonVoucher.addActionListener(e -> manageVoucher.deleteVoucher(TabelManageVoucher));
        refreshDataButtonVoucher.addActionListener(e -> manageVoucher.loadDataVoucher(TabelManageVoucher));

        TabelManageVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageVoucher.getSelectedRow();
                if (row >= 0) {
                    TF_IDVoucher.setText(TabelManageVoucher.getValueAt(row, 0).toString());
                    TF_KodeVoucher.setText(TabelManageVoucher.getValueAt(row, 1).toString());
                    String minBelanja = TabelManageVoucher.getValueAt(row, 2).toString().replace("Rp ", "").replace(",", "");
                    TF_MinBelanjaVoucher.setText(minBelanja);
                    TF_TglMulaiVoucher.setText(TabelManageVoucher.getValueAt(row, 3).toString());
                    TF_TglBerakhirVoucher.setText(TabelManageVoucher.getValueAt(row, 4).toString());
                    TF_KuotaVoucher.setText(TabelManageVoucher.getValueAt(row, 5).toString());
                    CMB_TipeVoucher.setSelectedItem(TabelManageVoucher.getValueAt(row, 6).toString());
                }
            }
        });

        return panel;
    }

    private JPanel createTierPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Tier Loyalitas"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("ID Tier:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_IDTier, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Nama Tier:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_NamaTier, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Minimal Poin:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_MinPoinTier, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Benefit:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_BenefitTier, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(simpanButtonTier);
        buttonPanel.add(updateButtonTier);
        buttonPanel.add(deleteButtonTier);
        buttonPanel.add(refreshDataButtonTier);

        JScrollPane scrollPane = new JScrollPane(TabelManageTier);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        simpanButtonTier.addActionListener(e -> {
            if (TF_IDTier.getText().trim().isEmpty() || TF_NamaTier.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Tier dan Nama Tier wajib diisi!");
                return;
            }
            try {
                manageTierLoyalitas.insertTier(TabelManageTier, TF_IDTier.getText(), TF_NamaTier.getText(),
                        Integer.parseInt(TF_MinPoinTier.getText()), TF_BenefitTier.getText());
                clearFormTier();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Minimal Poin harus berupa angka!");
            }
        });

        updateButtonTier.addActionListener(e -> {
            if (TF_IDTier.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih tier yang akan diupdate!");
                return;
            }
            try {
                manageTierLoyalitas.updateTier(TabelManageTier, TF_IDTier.getText(), TF_NamaTier.getText(),
                        Integer.parseInt(TF_MinPoinTier.getText()), TF_BenefitTier.getText());
                clearFormTier();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Minimal Poin harus berupa angka!");
            }
        });

        deleteButtonTier.addActionListener(e -> manageTierLoyalitas.deleteTier(TabelManageTier));
        refreshDataButtonTier.addActionListener(e -> manageTierLoyalitas.loadDataTier(TabelManageTier));

        TabelManageTier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageTier.getSelectedRow();
                if (row >= 0) {
                    TF_IDTier.setText(TabelManageTier.getValueAt(row, 0).toString());
                    TF_NamaTier.setText(TabelManageTier.getValueAt(row, 1).toString());
                    TF_MinPoinTier.setText(TabelManageTier.getValueAt(row, 2).toString());
                    TF_BenefitTier.setText(TabelManageTier.getValueAt(row, 3).toString());
                }
            }
        });

        return panel;
    }

    private JPanel createEkspedisiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Ekspedisi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("ID Ekspedisi:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_IDEkspedisi, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Nama Ekspedisi:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_NamaEkspedisi, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Kode:"), gbc);
        gbc.gridx = 1; formPanel.add(TF_KodeEkspedisi, gbc); row++;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; formPanel.add(CMB_StatusEkspedisi, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(simpanButtonEkspedisi);
        buttonPanel.add(updateButtonEkspedisi);
        buttonPanel.add(deleteButtonEkspedisi);
        buttonPanel.add(refreshDataButtonEkspedisi);

        JScrollPane scrollPane = new JScrollPane(TabelManageEkspedisi);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        simpanButtonEkspedisi.addActionListener(e -> {
            if (TF_IDEkspedisi.getText().trim().isEmpty() || TF_NamaEkspedisi.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Ekspedisi dan Nama Ekspedisi wajib diisi!");
                return;
            }
            manageEkspedisi.insertEkspedisi(TabelManageEkspedisi, TF_IDEkspedisi.getText(), TF_NamaEkspedisi.getText(),
                    TF_KodeEkspedisi.getText(), (String) CMB_StatusEkspedisi.getSelectedItem());
            clearFormEkspedisi();
        });

        updateButtonEkspedisi.addActionListener(e -> {
            if (TF_IDEkspedisi.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih ekspedisi yang akan diupdate!");
                return;
            }
            manageEkspedisi.updateEkspedisi(TabelManageEkspedisi, TF_IDEkspedisi.getText(), TF_NamaEkspedisi.getText(),
                    TF_KodeEkspedisi.getText(), (String) CMB_StatusEkspedisi.getSelectedItem());
            clearFormEkspedisi();
        });

        deleteButtonEkspedisi.addActionListener(e -> manageEkspedisi.deleteEkspedisi(TabelManageEkspedisi));
        refreshDataButtonEkspedisi.addActionListener(e -> manageEkspedisi.loadDataEkspedisi(TabelManageEkspedisi));

        TabelManageEkspedisi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManageEkspedisi.getSelectedRow();
                if (row >= 0) {
                    TF_IDEkspedisi.setText(TabelManageEkspedisi.getValueAt(row, 0).toString());
                    TF_NamaEkspedisi.setText(TabelManageEkspedisi.getValueAt(row, 1).toString());
                    TF_KodeEkspedisi.setText(TabelManageEkspedisi.getValueAt(row, 2).toString());
                    CMB_StatusEkspedisi.setSelectedItem(TabelManageEkspedisi.getValueAt(row, 3).toString());
                }
            }
        });

        return panel;
    }

    private JPanel createPengirimanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel trackPanel = new JPanel(new FlowLayout());
        trackPanel.setBorder(BorderFactory.createTitledBorder("Tracking Pengiriman"));
        trackPanel.add(new JLabel("ID Transaksi:"));
        trackPanel.add(TF_TrackIdTransaksi);
        trackPanel.add(trackButton);
        trackPanel.add(refreshDataButtonPengiriman);

        JScrollPane scrollPaneTracking = new JScrollPane(TA_TrackingResult);
        scrollPaneTracking.setBorder(BorderFactory.createTitledBorder("Hasil Tracking"));

        JScrollPane scrollPaneTable = new JScrollPane(TabelManagePengiriman);
        scrollPaneTable.setBorder(BorderFactory.createTitledBorder("Daftar Pengiriman"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneTable, scrollPaneTracking);
        splitPane.setResizeWeight(0.6);

        panel.add(trackPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        trackButton.addActionListener(e ->
                managePengiriman.trackPengiriman(TA_TrackingResult, TF_TrackIdTransaksi.getText().trim()));
        refreshDataButtonPengiriman.addActionListener(e ->
                managePengiriman.loadDataPengiriman(TabelManagePengiriman));

        TabelManagePengiriman.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelManagePengiriman.getSelectedRow();
                if (row >= 0) {
                    String idPengiriman = TabelManagePengiriman.getValueAt(row, 0).toString();
                    String status = TabelManagePengiriman.getValueAt(row, 1).toString();
                    String[] statusOptions = {"Barang belum diambil", "Masih dalam perjalanan", "Barang telah diterima"};
                    String newStatus = (String) JOptionPane.showInputDialog(null,
                            "Update status pengiriman:", "Update Status",
                            JOptionPane.QUESTION_MESSAGE, null, statusOptions, status);
                    if (newStatus != null && !newStatus.equals(status)) {
                        managePengiriman.updateStatusPengiriman(TabelManagePengiriman, idPengiriman, newStatus);
                    }
                }
            }
        });

        return panel;
    }

    // ==================== METHOD CLEAR FORM ====================
    private void clearFormPoinHistory() {
        TF_IDPelangganPoin.setText("");
        TF_IDTransaksiPoin.setText("");
        TF_PerubahanPoin.setText("");
    }

    private void clearFormPelanggan() {
        TF_IDPelanggan.setText(""); TF_NamaPelanggan.setText("");
        TF_TelpPelanggan.setText(""); TF_AlamatPelanggan.setText("");
    }
    
    private void clearFormTransaksi() {
        TF_IDTransaksi.setText("");
        if (CMB_StatusTransaksi.getItemCount() > 0) CMB_StatusTransaksi.setSelectedIndex(0);
    }

    private void clearFormProduk() {
        TF_IDProduk.setText("");
        TF_NamaProduk.setText("");
        TF_DeskripsiProduk.setText("");
        if (CMB_MerkProduk.getItemCount() > 0)   CMB_MerkProduk.setSelectedIndex(0);
        if (CMB_PemasokProduk.getItemCount() > 0) CMB_PemasokProduk.setSelectedIndex(0);
        manageProduk.clearSelectedKategori();
    }

    private void clearFormVarian() {
        TF_IDProdukVarian.setText("");
        TF_IDVarian.setText("");
        TF_UkuranVarian.setText("");
        TF_WarnaVarian.setText("");
        TF_BeratVarian.setText("");
        TF_StokVarian.setText("");
        TF_HargaVarian.setText("");
        TF_BarcodeVarian.setText("");
        if (CMB_IDProduk.getItemCount() > 0) CMB_IDProduk.setSelectedIndex(0);
    }

    private void clearFormKategori() {
        TF_IDKategori.setText("");
        TF_NamaKategori.setText("");
        TF_DeskripsiKategori.setText("");
    }

    private void clearFormMerk() {
        TF_IDMerk.setText("");
        TF_NamaMerk.setText("");
        TF_DeskripsiMerk.setText("");
    }

    private void clearFormPemasok() {
        TF_IDPemasok.setText("");
        TF_NamaPemasok.setText("");
        TF_EmailPemasok.setText("");
        TF_TelpPemasok.setText("");
        TF_AlamatPemasok.setText("");
    }

    private void clearFormVoucher() {
        TF_IDVoucher.setText("");
        TF_KodeVoucher.setText("");
        TF_MinBelanjaVoucher.setText("");
        TF_TglMulaiVoucher.setText("");
        TF_TglBerakhirVoucher.setText("");
        TF_KuotaVoucher.setText("");
        TF_PersenDiskon.setText("");
        TF_MaksDiskon.setText("");
        TF_NominalPotongan.setText("");
        CMB_TipeVoucher.setSelectedIndex(0);
    }

    private void clearFormTier() {
        TF_IDTier.setText("");
        TF_NamaTier.setText("");
        TF_MinPoinTier.setText("");
        TF_BenefitTier.setText("");
    }

    private void clearFormEkspedisi() {
        TF_IDEkspedisi.setText("");
        TF_NamaEkspedisi.setText("");
        TF_KodeEkspedisi.setText("");
        CMB_StatusEkspedisi.setSelectedIndex(0);
    }

    // ==================== METHOD MENGGUNAKAN DAO ====================

    private void gantiInformasiAkun(int e) {
        String informasi = JOptionPane.showInputDialog(this, "Masukan Data Pengganti : ");
        if (informasi == null || informasi.isEmpty()) return;

        boolean success = false;
        switch (e) {
            case 1:
                if (pelangganDAO.getById(informasi) != null) {
                    JOptionPane.showMessageDialog(this, "ID sudah digunakan oleh pelanggan lain!"); return;
                }
                success = pelangganDAO.updateId(loggedinUserID, informasi);
                if (success) loggedinUserID = informasi;
                break;
            case 2:
                success = pelangganDAO.updateNama(loggedinUserID, informasi);
                if (success) loggedinuserNama = informasi;
                break;
            case 3:
                success = pelangganDAO.updateEmail(loggedinUserID, informasi);
                break;
            case 4:
                success = pelangganDAO.updateNoTelp(loggedinUserID, informasi);
                break;
            case 5:
                success = pelangganDAO.updateAlamat(loggedinUserID, informasi);
                break;
        }
        if (success) {
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            refreshDataPengguna();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (success) { JOptionPane.showMessageDialog(this, "Data berhasil diupdate!"); refreshDataPengguna(); }
        else JOptionPane.showMessageDialog(this, "Gagal mengupdate data!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshDataPengguna() {
        int index = tabbedPane1.getSelectedIndex();
        if (index == 3) {
            List<Map<String, Object>> historyList = poinHistoryDAO.getByPelangganId(loggedinUserID);
            tb1.setRowCount(0);
            for (Map<String, Object> history : historyList) {
                tb1.addRow(new Object[]{history.get("tanggal"), history.get("id_transaksi"), history.get("perubahan_point")});
            }
        }
        if (index == 4) {
            Map<String, Object> pelanggan = pelangganDAO.getById(loggedinUserID);
            if (pelanggan != null) {
                akunID.setText((String) pelanggan.get("id_pelanggan"));
                akunNama.setText((String) pelanggan.get("nama"));
                akunEmail.setText((String) pelanggan.get("email"));
                akunTelp.setText((String) pelanggan.get("no_telp"));
                akunCreated.setText(pelanggan.get("tgl_daftar") != null ? pelanggan.get("tgl_daftar").toString() : "");
                akunAlamat.setText((String) pelanggan.get("alamat_utama"));
                String idTier = (String) pelanggan.get("id_tier");
                Map<String, Object> tier = tierLoyalitasDAO.getById(idTier);
                if (tier != null) {
                    akunTier.setText(idTier + " - " + tier.get("nama_tier"));
                    akunBenefit.setText((String) tier.get("benefit"));
                }
            }
        }
    }

    private void registPembeli() {
        String id     = IDRegist.getText().trim();
        String nama   = namaRegist.getText().trim();
        String email  = emailRegist.getText().trim();
        String telp   = telpRegist.getText().trim();
        String alamat = alamatRegist.getText().trim();

        if (id.isEmpty() || nama.isEmpty() || email.isEmpty() || telp.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak boleh kosong");
            return;
        }
        if (pelangganDAO.getById(id) != null) {
            JOptionPane.showMessageDialog(this, "ID Pelanggan sudah digunakan!", "Error", JOptionPane.WARNING_MESSAGE); return;
        }
        if (pelangganDAO.insert(id, nama, email, telp, alamat)) {
            JOptionPane.showMessageDialog(this, "Akun berhasil ditambahkan!");
            IDRegist.setText("");
            namaRegist.setText("");
            emailRegist.setText("");
            telpRegist.setText("");
            alamatRegist.setText("");
            c1.show(MainPanel, "pageUtama");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan akun!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loginFrontend() {
        String data = textFieldFront.getText().trim();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Kosong");
            return;
        }
        Map<String, Object> pelanggan = pelangganDAO.getById(data);
        if (pelanggan == null) {
            JOptionPane.showMessageDialog(this, "Data Pelanggan tidak Ditemukan!", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        loggedinUserID   = (String) pelanggan.get("id_pelanggan");
        loggedinuserNama = (String) pelanggan.get("nama");
        c1.show(MainPanel, "Front");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new App();
    }
}