package src;

import src.backend.ManageProduct.ManageKategori;
import src.backend.ManageProduct.ManageMerk;
import src.backend.ManageProduct.ManagePemasok;
import src.backend.ManageProduct.ManageProduk;
import src.backend.ManageProduct.ManageVarian;
import src.backend.ManageProduct.ManagePelanggan;
import src.backend.ManageProduct.ManageTransaksi;
import src.backend.ManageProduct.ManagePoinHistory; // [BARU]
import src.database.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

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

    // GUI Poin History [BARU]
    private JTextField TF_IDPelangganPoin;
    private JTextField TF_IDTransaksiPoin;
    private JTextField TF_PerubahanPoin;
    private JButton simpanButtonPoinHistory;
    private JButton refreshDataButtonPoinHistory;
    private JTable TabelManagePoinHistory;

    // Untuk mode create varian
    private JTextField TF_IDProdukVarian;

    // Card Layout
    private CardLayout c1;

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
            refreshAllTabs.run();
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
            refreshAllTabs.run();
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

    private void clearFormPoinHistory() {
        TF_IDPelangganPoin.setText("");
        TF_IDTransaksiPoin.setText("");
        TF_PerubahanPoin.setText("");
    }
    private void clearFormProduk() {
        TF_IDProduk.setText(""); TF_NamaProduk.setText(""); TF_DeskripsiProduk.setText("");
        if (CMB_MerkProduk.getItemCount()     > 0) CMB_MerkProduk.setSelectedIndex(0);
        if (CMB_KategoriProduk.getItemCount() > 0) CMB_KategoriProduk.setSelectedIndex(0);
        if (CMB_PemasokProduk.getItemCount()  > 0) CMB_PemasokProduk.setSelectedIndex(0);
    }
    private void clearFormVarian() {
        TF_IDProdukVarian.setText(""); TF_IDVarian.setText(""); TF_UkuranVarian.setText("");
        TF_WarnaVarian.setText(""); TF_BeratVarian.setText(""); TF_StokVarian.setText("");
        TF_HargaVarian.setText(""); TF_BarcodeVarian.setText("");
        if (CMB_IDProduk.getItemCount() > 0) CMB_IDProduk.setSelectedIndex(0);
    }
    private void clearFormKategori() {
        TF_IDKategori.setText(""); TF_NamaKategori.setText(""); TF_DeskripsiKategori.setText("");
    }
    private void clearFormMerk() {
        TF_IDMerk.setText(""); TF_NamaMerk.setText(""); TF_DeskripsiMerk.setText("");
    }
    private void clearFormPemasok() {
        TF_IDPemasok.setText(""); TF_NamaPemasok.setText("");
        TF_EmailPemasok.setText(""); TF_TelpPemasok.setText(""); TF_AlamatPemasok.setText("");
    }
    private void clearFormPelanggan() {
        TF_IDPelanggan.setText(""); TF_NamaPelanggan.setText("");
        TF_TelpPelanggan.setText(""); TF_AlamatPelanggan.setText("");
    }
    private void clearFormTransaksi() {
        TF_IDTransaksi.setText("");
        if (CMB_StatusTransaksi.getItemCount() > 0) CMB_StatusTransaksi.setSelectedIndex(0);
    }

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
            case 2: success = pelangganDAO.updateNama(loggedinUserID, informasi); if (success) loggedinuserNama = informasi; break;
            case 3: success = pelangganDAO.updateEmail(loggedinUserID, informasi);  break;
            case 4: success = pelangganDAO.updateNoTelp(loggedinUserID, informasi); break;
            case 5: success = pelangganDAO.updateAlamat(loggedinUserID, informasi); break;
            default: JOptionPane.showMessageDialog(this, "Something went wrong"); return;
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
                tb1.addRow(new Object[]{
                        history.get("tanggal"),
                        history.get("id_transaksi"),
                        history.get("perubahan_point")
                });
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
        String id = IDRegist.getText().trim(), nama = namaRegist.getText().trim();
        String email = emailRegist.getText().trim(), telp = telpRegist.getText().trim();
        String alamat = alamatRegist.getText().trim();
        if (id.isEmpty() || nama.isEmpty() || email.isEmpty() || telp.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak boleh kosong"); return;
        }
        if (pelangganDAO.getById(id) != null) {
            JOptionPane.showMessageDialog(this, "ID Pelanggan sudah digunakan!", "Error", JOptionPane.WARNING_MESSAGE); return;
        }
        if (pelangganDAO.insert(id, nama, email, telp, alamat)) {
            JOptionPane.showMessageDialog(this, "Akun berhasil ditambahkan!");
            IDRegist.setText(""); namaRegist.setText(""); emailRegist.setText("");
            telpRegist.setText(""); alamatRegist.setText("");
            c1.show(MainPanel, "pageUtama");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan akun!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loginFrontend() {
        String data = textFieldFront.getText().trim();
        if (data.isEmpty()) { JOptionPane.showMessageDialog(this, "ID Kosong"); return; }
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
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        new App();
    }
}
