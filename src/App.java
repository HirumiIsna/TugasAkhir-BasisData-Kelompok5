package src;

import src.FrontEnd.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class App extends JFrame {
    // Table
    DefaultTableModel tb1, tb2, tb3;

    // Sql
    static String url = configLoginSql.url;
    static String userName = configLoginSql.userName;
    static String password = configLoginSql.password;
    Connection conn = configLoginSql.setConnection();

    // User
    private String loggedinUserID;
    private String loggedinuserNama;

    // Data
    ArrayList<Object[]> katalogItem = new ArrayList<>();
    ArrayList<Object[]> keranjangItem = new ArrayList<>();
    ArrayList<Object[]> dataPengantar = new ArrayList<>();

    // Komponen GUI (semua komponen dari form)
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
    private JTable table2;
    private JLabel welcome;
    private JTextField filterTF;
    private JComboBox<String> kategoriCB;
    private JComboBox<String> merkCB;
    private JLabel cariLB;
    private JLabel kategoriLB;
    private JLabel merkLB;
    private JButton tambahKeKeranjangButton;
    private JButton cariFilter;
    private JSpinner jumlahSelected;
    private JTable tableKeranjang;
    private JLabel LBjumlah;
    private JSpinner spinnerJumlah;
    private JScrollPane JKeranjang;
    private JButton deleteSelectedButton;
    private JButton deleteAllButton;
    private JTextArea taAlamat;
    private JComboBox cbEkspedisi;
    private JComboBox cbPengiriman;
    private JButton checkoutButton;
    private JComboBox cbMethod;
    private JTextField tfVoucher;
    private JLabel TFtotal;
    private JLabel lbOngkir;
    private JLabel lbMethod1;
    private JLabel lbMethod2;
    private JComboBox cbOpsi;
    private JTextField tfOpsi;
    private JLabel TierInfo;
    private JPanel TransaksiHistory;
    private JTextArea ID;

    private CardLayout c1;

    // Managers
    private LoginManager loginManager;
    private UserManager userManager;
    private CatalogManager catalogManager;
    private CartManager cartManager;
    private CheckoutManager checkoutManager;

    public App() {
        setContentPane(MainPanel);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        kategoriCB.setMaximumRowCount(5);
        merkCB.setMaximumRowCount(5);

        c1 = (CardLayout) MainPanel.getLayout();
        c1.show(MainPanel, "pageUtama");

        // Set up tabel
        setupTables();

        // Inisialisasi Manager
        loginManager = new LoginManager(this);
        userManager = new UserManager(this, conn);
        catalogManager = new CatalogManager(this, conn, katalogItem, tb2);
        cartManager = new CartManager(this, keranjangItem, tb3);
        checkoutManager = new CheckoutManager(this, conn, keranjangItem, dataPengantar);

        // Listeners
        buttonBack.addActionListener(e -> c1.show(MainPanel, "Back"));
        buttonFront.addActionListener(e -> loginManager.loginFrontend(textFieldFront.getText()));
        registAkunButton.addActionListener(e -> c1.show(MainPanel, "regist"));
        backButton.addActionListener(e -> c1.show(MainPanel, "pageUtama"));
        daftarButton.addActionListener(e -> loginManager.registPembeli(
                IDRegist.getText(), namaRegist.getText(), emailRegist.getText(),
                telpRegist.getText(), alamatRegist.getText()));
        gantiButton.addActionListener(e -> userManager.gantiInformasiAkun(1, loggedinUserID));
        gantiButton1.addActionListener(e -> userManager.gantiInformasiAkun(2, loggedinUserID));
        gantiButton2.addActionListener(e -> userManager.gantiInformasiAkun(3, loggedinUserID));
        gantiButton3.addActionListener(e -> userManager.gantiInformasiAkun(4, loggedinUserID));
        gantiButton4.addActionListener(e -> userManager.gantiInformasiAkun(5, loggedinUserID));
        cariFilter.addActionListener(e -> catalogManager.filterBarang(
                filterTF.getText(), kategoriCB.getSelectedItem().toString(), merkCB.getSelectedItem().toString()));
        tambahKeKeranjangButton.addActionListener(e -> catalogManager.tambahKeKeranjang(
                (int) jumlahSelected.getValue(), table2.getSelectedRow(), keranjangItem));
        deleteSelectedButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Apakah ingin lanjut?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) return;
            cartManager.deleteSelected(tableKeranjang.getSelectedRow());
        });
        deleteAllButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Apakah ingin lanjut?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) return;
            cartManager.deleteAll();
        });
        checkoutButton.addActionListener(e -> checkoutManager.checkoutRun());

        // Pengiriman dan pembayaran listeners
        cbPengiriman.addItem("Delivery");
        cbPengiriman.addItem("Collect");
        cbPengiriman.addActionListener(e -> {
            if ("Delivery".equals(cbPengiriman.getSelectedItem().toString())) {
                checkoutManager.refreshEkspedisi(akunAlamat.getText());
                checkoutManager.refreshHarga();
            } else if ("Collect".equals(cbPengiriman.getSelectedItem().toString())) {
                cbEkspedisi.removeAllItems();
                taAlamat.setText("Ambil di Matahari terdekat!");
                taAlamat.setEditable(false);
                checkoutManager.refreshHarga();
            }
        });
        cbMethod.addItem("-"); cbMethod.addItem("Bank"); cbMethod.addItem("Kredit"); cbMethod.addItem("Dompet Digital");
        cbMethod.addActionListener(e -> {
            String selected = cbMethod.getSelectedItem().toString();
            if ("-".equals(selected)) {
                lbMethod1.setText("-"); lbMethod2.setText("2");
                cbOpsi.removeAllItems();
                tfOpsi.setText("");
            } else if ("Bank".equals(selected)) {
                lbMethod1.setText("Bank : ");
                lbMethod2.setText("Nomor rek : ");
                cbOpsi.removeAllItems();
                String[] banks = {"BCA","Mandiri","BRI","BNI","CIMB Niaga","BSI","Permata Bank"};
                for (String b : banks) cbOpsi.addItem(b);
            } else if ("Kredit".equals(selected)) {
                lbMethod1.setText("Bank : ");
                lbMethod2.setText("Nomor Kredit : ");
                cbOpsi.removeAllItems();
                String[] banks = {"BCA","Mandiri","BRI","BNI","CIMB Niaga","BSI","Permata Bank"};
                for (String b : banks) cbOpsi.addItem(b);
            } else if ("Dompet Digital".equals(selected)) {
                lbMethod1.setText("Dompet : ");
                lbMethod2.setText("No. HP : ");
                cbOpsi.removeAllItems();
                String[] dompets = {"DANA","OVO","GoPay","ShopeePay","LinkAja"};
                for (String d : dompets) cbOpsi.addItem(d);
            }
        });

        // Tab listener
        tabbedPane1.addChangeListener(e -> refreshDataPengguna());

        // Special tab untuk transaksi (index 2)
        tabbedPane1.addChangeListener(e -> {
            if (tabbedPane1.getSelectedIndex() == 2) {
                TransactionView tv = new TransactionView(this);
                JPanel panel = tv.buildTransactionsPanel("ALL", "DESC");
                tabbedPane1.setComponentAt(2, panel);
            }
        });

        setVisible(true);
    }

    private void setupTables() {
        tb1 = new DefaultTableModel() { public boolean isCellEditable(int r, int c) { return false; } };
        tb1.addColumn("Tanggal"); tb1.addColumn("ID Transaksi"); tb1.addColumn("Perubahan Poin");
        table1.setModel(tb1);

        tb2 = new DefaultTableModel() { public boolean isCellEditable(int r, int c) { return false; } };
        tb2.addColumn("Kategori"); tb2.addColumn("Nama"); tb2.addColumn("Merk");
        tb2.addColumn("Ukuran"); tb2.addColumn("Warna"); tb2.addColumn("Stok"); tb2.addColumn("Harga");
        table2.setModel(tb2);

        tb3 = new DefaultTableModel() { public boolean isCellEditable(int r, int c) { return false; } };
        tb3.addColumn("Nama"); tb3.addColumn("Merk"); tb3.addColumn("Ukuran");
        tb3.addColumn("Warna"); tb3.addColumn("Harga"); tb3.addColumn("Jumlah");
        tableKeranjang.setModel(tb3);

        table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKeranjang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableKeranjang.getSelectedRow();
                if (row != -1) {
                    Object[] select = keranjangItem.get(row);
                    int max = (int) select[8];
                    SpinnerNumberModel model2 = new SpinnerNumberModel(1, 1, max, 1);
                    spinnerJumlah.setModel(model2);
                    spinnerJumlah.setValue((int) select[5]);
                }
            }
        });
        spinnerJumlah.addChangeListener(e -> {
            int row = tableKeranjang.getSelectedRow();
            if (row != -1) cartManager.ubahSelectedKeranjang(row, (int) spinnerJumlah.getValue());
        });

        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 999, 1);
        jumlahSelected.setModel(model);
    }

    // ===================== GETTERS & SETTERS UNTUK MANAGER =====================
    public void setLoggedinUserID(String id) { this.loggedinUserID = id; }
    public void setLoggedinuserNama(String nama) { this.loggedinuserNama = nama; }
    public String getLoggedinUserID() { return loggedinUserID; }
    public String getLoggedinuserNama() { return loggedinuserNama; }
    public void setWelcomeText(String text) { welcome.setText(text); }
    public void showFrontPanel() { c1.show(MainPanel, "Front"); }
    public JTabbedPane getTabbedPane() { return tabbedPane1; }
    public Connection getConnection() { return conn; }

    public void setAkunID(String text) { akunID.setText(text); }
    public void setAkunNama(String text) { akunNama.setText(text); }
    public void setAkunEmail(String text) { akunEmail.setText(text); }
    public void setAkunTelp(String text) { akunTelp.setText(text); }
    public void setAkunCreated(String text) { akunCreated.setText(text); }
    public void setAkunAlamat(String text) { akunAlamat.setText(text); }
    public void setAkunTier(String text) { akunTier.setText(text); }
    public void setAkunBenefit(String text) { akunBenefit.setText(text); }

    public void setTFtotal(String text) { TFtotal.setText(text); }
    public void setLbOngkir(String text) { lbOngkir.setText(text); }
    public String getCbPengirimanSelectedItem() { return (String) cbPengiriman.getSelectedItem(); }
    public String getCbMethodSelectedItem() { return (String) cbMethod.getSelectedItem(); }
    public String getCbOpsiSelectedItem() { return (String) cbOpsi.getSelectedItem(); }
    public String getCbEkspedisiSelectedItem() { return (String) cbEkspedisi.getSelectedItem(); }
    public String getTfOpsiText() { return tfOpsi.getText(); }
    public String getTfVoucherText() { return tfVoucher.getText(); }
    public String getTaAlamatText() { return taAlamat.getText(); }
    public String getLbMethod1Text() { return lbMethod1.getText(); }
    public void setTaAlamatEditable(boolean editable) { taAlamat.setEditable(editable); }
    public void setTaAlamatText(String text) { taAlamat.setText(text); }
    public void clearCbEkspedisi() { cbEkspedisi.removeAllItems(); }
    public void addCbEkspedisiItem(String item) { cbEkspedisi.addItem(item); }

    public JComboBox<String> getKategoriCB() { return kategoriCB; }
    public JComboBox<String> getMerkCB() { return merkCB; }

    // ===================== METHOD REFRESH YANG DIPANGGIL DARI MANAGER =====================
    public void refreshDataPengguna() {
        int index = tabbedPane1.getSelectedIndex();
        if (index == 0) {
            catalogManager.refreshKatalog(kategoriCB, merkCB);
        } else if (index == 1) {
            tb3.setRowCount(0);
            for (Object[] o : keranjangItem) tb3.addRow(o);
            userManager.loadDataPelanggan(loggedinUserID);
            TierInfo.setText(akunBenefit.getText());
            checkoutManager.refreshEkspedisi(akunAlamat.getText());
            checkoutManager.refreshHarga();
        } else if (index == 3) {
            try {
                String query = "SELECT tanggal, id_transaksi, perubahan_point FROM Poin_History WHERE id_pelanggan = ? ORDER BY tanggal DESC";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, loggedinUserID);
                ResultSet rs = ps.executeQuery();
                tb1.setRowCount(0);
                while (rs.next()) {
                    tb1.addRow(new Object[]{rs.getDate(1).toString(), rs.getString(2), rs.getInt(3)});
                }
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, e.getMessage()); }
        } else if (index == 4) {
            userManager.loadDataPelanggan(loggedinUserID);
        }
    }

    public void refreshHarga() {
        checkoutManager.refreshHarga();
    }

    public static void main(String[] args) {
        new App();
    }
}