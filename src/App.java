package src;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
    String loggedinUserID;
    String loggedinuserNama;

    // Nyimpan subtotal
    double hargaSub = 0;
    double ongkir = 0;

    // Arraylist Katalog
    ArrayList<Object[]> katalogItem = new ArrayList<>();
    ArrayList<Object[]> keranjangItem = new ArrayList<>();
    ArrayList<Object[]> dataPengantar = new ArrayList<>();

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

    // Card Layout
    private CardLayout c1;

    // Constrcutor
    public App(){
        setContentPane(MainPanel);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        kategoriCB.setMaximumRowCount(5);
        merkCB.setMaximumRowCount(5);

        c1 = (CardLayout) MainPanel.getLayout();
        c1.show(MainPanel, "pageUtama");

        buttonBack.addActionListener((e) -> c1.show(MainPanel, "Back"));
        buttonFront.addActionListener((e) -> loginFrontend());
        registAkunButton.addActionListener((e) -> {c1.show(MainPanel, "regist");});
        backButton.addActionListener((e) -> c1.show(MainPanel, "pageUtama"));
        daftarButton.addActionListener((e) -> registPembeli());
        gantiButton.addActionListener((e) -> gantiInformasiAkun(1));
        gantiButton1.addActionListener((e) -> gantiInformasiAkun(2));
        gantiButton2.addActionListener((e) -> gantiInformasiAkun(3));
        gantiButton3.addActionListener((e) -> gantiInformasiAkun(4));
        gantiButton4.addActionListener((e) -> gantiInformasiAkun(5));
        cariFilter.addActionListener((e) -> filterBarang());
        tambahKeKeranjangButton.addActionListener((e) -> tambahKeKeranjang());

        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 999, 1);
        jumlahSelected.setModel(model);

        tabbedPane1.addChangeListener((e) -> refreshDataPengguna());

        tb1 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tb1.addColumn("Tanggal");
        tb1.addColumn("ID Transaksi"); tb1.addColumn("Perubahan Poin");


        tb2 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tb2.addColumn("Kategori");
        tb2.addColumn("Nama"); tb2.addColumn("Merk");
        tb2.addColumn("Ukuran"); tb2.addColumn("Warna");
        tb2.addColumn("Stok"); tb2.addColumn("Harga");
        table2.setModel(tb2);

        tb3 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tb3.addColumn("Nama"); tb3.addColumn("Merk"); tb3.addColumn("Ukuran");
        tb3.addColumn("Warna"); tb3.addColumn("Harga"); tb3.addColumn("Jumlah");
        tableKeranjang.setModel(tb3);

        table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKeranjang.getSelectionModel().addListSelectionListener((e) -> synchronSpinner(e));
        spinnerJumlah.addChangeListener((e) -> ubahSelectedKeranjang());
        deleteSelectedButton.addActionListener((e) -> {
            int result = JOptionPane.showConfirmDialog(this, "Apakah ingin lanjut?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.NO_OPTION) return;
            int index = tableKeranjang.getSelectedRow();
            if(index == -1){
                JOptionPane.showMessageDialog(this, "Pilih item terlebih dahulu!");
                return;
            }
            keranjangItem.remove(index);
            refreshDataPengguna();
            refreshHarga();
        });
        deleteAllButton.addActionListener((e) -> {
            int result = JOptionPane.showConfirmDialog(this, "Apakah ingin lanjut?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

            if(result == JOptionPane.NO_OPTION) return;

            int index = tableKeranjang.getSelectedRow();

            keranjangItem.clear();
            refreshDataPengguna();
            refreshHarga();
        });

        table1.setModel(tb1);

        cbPengiriman.addItem("Delivery"); cbPengiriman.addItem("Collect");

        cbPengiriman.addActionListener((e) -> {
            if(cbPengiriman.getSelectedItem().toString().equals("Delivery")){
                refreshEkspedisi();
                refreshHarga();
                return;
            }

            if(cbPengiriman.getSelectedItem().toString().equals("Collect")){
                cbEkspedisi.removeAllItems();
                taAlamat.setText("Ambil di Matahari terdekat!");
                taAlamat.setEditable(false);
                refreshHarga();
            }
        });
        cbMethod.addItem("-"); cbMethod.addItem("Bank"); cbMethod.addItem("Kredit"); cbMethod.addItem("Dompet Digital");
        cbMethod.addActionListener((e) -> {
            String selected = cbMethod.getSelectedItem().toString();
            if(selected.equals("-")){
                lbMethod1.setText("-"); lbMethod2.setText("2");
                cbOpsi.removeAllItems();
                tfOpsi.setText("");
                return;
            }

            if(selected.equals("Bank")){
                lbMethod1.setText("Bank : ");
                lbMethod2.setText("Nomor rek : ");

                cbOpsi.removeAllItems();
                cbOpsi.addItem("BCA");cbOpsi.addItem("Mandiri");cbOpsi.addItem("BRI");
                cbOpsi.addItem("BNI");cbOpsi.addItem("CIMB Niaga");cbOpsi.addItem("BSI");
                cbOpsi.addItem("Permata Bank");
                return;
            }

            if(selected.equals("Kredit")){
                lbMethod1.setText("Bank : ");
                lbMethod2.setText("Nomor Kredit : ");

                cbOpsi.removeAllItems();
                cbOpsi.addItem("BCA");cbOpsi.addItem("Mandiri");
                cbOpsi.addItem("BRI");cbOpsi.addItem("BNI");
                cbOpsi.addItem("CIMB Niaga");cbOpsi.addItem("BSI");
                cbOpsi.addItem("Permata Bank");
                return;
            }

            if(selected.equals("Dompet Digital")){
                lbMethod1.setText("Dompet : ");
                lbMethod2.setText("No. HP : ");

                cbOpsi.removeAllItems();
                cbOpsi.addItem("DANA");cbOpsi.addItem("OVO");
                cbOpsi.addItem("GoPay");cbOpsi.addItem("ShopeePay");
                cbOpsi.addItem("LinkAja");
                return;
            }
        });

        checkoutButton.addActionListener((e) -> checkoutRun());

        tabbedPane1.addChangeListener(e -> {
            if (tabbedPane1.getSelectedIndex() == 2) {
                JPanel panel = buildTransactionsPanel("ALL", "DESC");
                tabbedPane1.setComponentAt(2, panel);
            }
        });


        setVisible(true);
    }

    private void checkoutRun(){
        if(keranjangItem.isEmpty()){
            JOptionPane.showMessageDialog(this, "Keranjang Kosong");
            return;
        }

        if(cbMethod.getSelectedItem().toString().equals("-")){
            JOptionPane.showMessageDialog(this, "Pilih Opsi Pembayaran!");
            return;
        }

        if(tfOpsi.getText().isEmpty()){
            String[] text = lbMethod1.getText().split(" ");
            JOptionPane.showMessageDialog(this, "Isi Informasi " + text[0]);
            return;
        }

        double persentaseDiskonTier = 0.0;
        try {
            String queryTier = "SELECT id_tier FROM Pelanggan WHERE id_pelanggan = ?";
            PreparedStatement psTier = conn.prepareStatement(queryTier);
            psTier.setString(1, loggedinUserID);
            ResultSet rsTier = psTier.executeQuery();

            if (rsTier.next()) {
                String idTier = rsTier.getString("id_tier");
                switch (idTier) {
                    case "TR02": persentaseDiskonTier = 0.02; break; // 2%
                    case "TR03": persentaseDiskonTier = 0.04; break; // 4%
                    case "TR04": persentaseDiskonTier = 0.06; break; // 6%
                    case "TR05": persentaseDiskonTier = 0.08; break; // 8%
                }
            }
            rsTier.close();
            psTier.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data Tier: " + e.getMessage());
            return;
        }

        double nominalDiskonTier = hargaSub * persentaseDiskonTier;
        double hargaSubSetelahTier = hargaSub - nominalDiskonTier;

        double hargaAkhir = ongkir + hargaSubSetelahTier;

        // simpan voucher sementara
        String idVoucherDipakai = null;

        // Validasi Voucher
        if(!tfVoucher.getText().trim().isEmpty()){
            try{
                String query =
                        "SELECT v.id_voucher,v.min_belanja,v.tgl_mulai,v.tgl_berlaku,v.kuota, " +
                                "CASE " +
                                "WHEN p.id_voucher IS NOT NULL THEN 'POTONGAN' " +
                                "WHEN o.id_voucher IS NOT NULL THEN 'ONGKIR' " +
                                "WHEN d.id_voucher IS NOT NULL THEN 'DISKON' " +
                                "ELSE 'TIDAK VALID' " +
                                "END AS tipe_voucher " +
                                "FROM Voucher v " +
                                "LEFT JOIN Potongan p ON v.id_voucher = p.id_voucher " +
                                "LEFT JOIN Ongkir o ON v.id_voucher = o.id_voucher " +
                                "LEFT JOIN Diskon d ON v.id_voucher = d.id_voucher " +
                                "WHERE v.kode = ?";

                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, tfVoucher.getText().trim());

                ResultSet rs = ps.executeQuery();

                if(!rs.next()){
                    JOptionPane.showMessageDialog(this, "Invalid Kode Voucher!");
                    return;
                }

                // cek kuota
                if(rs.getInt(5) <= 0){
                    JOptionPane.showMessageDialog(this, "Voucher habis");
                    return;
                }

                // cek minimum belanja
                int minBelanja = rs.getInt(2);

                if(minBelanja > hargaAkhir){
                    JOptionPane.showMessageDialog(this,
                            "Minimal pembelian Rp. " + minBelanja);
                    return;
                }

                // cek tanggal
                Date tnggl_max = rs.getDate(4);
                Date tnggl_min = rs.getDate(3);

                Date now = new Date(System.currentTimeMillis());

                if(!(now.after(tnggl_min) && now.before(tnggl_max))){
                    JOptionPane.showMessageDialog(this,
                            "Voucher kadaluarsa");
                    return;
                }

                String tipeVoucher = rs.getString(6);
                String idVoucher = rs.getString(1);

                idVoucherDipakai = idVoucher;

                // POTONGAN
                if(tipeVoucher.equalsIgnoreCase("POTONGAN")){
                    String queryPotongan =
                            "SELECT nominal FROM Potongan WHERE id_voucher = ?";

                    PreparedStatement ps2 = conn.prepareStatement(queryPotongan);

                    ps2.setString(1, idVoucher);

                    ResultSet rs2 = ps2.executeQuery();

                    if(rs2.next()){
                        int nominal = rs2.getInt("nominal");
                        hargaAkhir -= nominal;
                        if(hargaAkhir < 0){
                            hargaAkhir = 0;
                        }
                    }
                }

                else if(tipeVoucher.equalsIgnoreCase("ONGKIR")){

                    String queryOngkir = "SELECT persen_diskon, maks_diskon FROM Ongkir WHERE id_voucher = ?";

                    PreparedStatement ps2 = conn.prepareStatement(queryOngkir);

                    ps2.setString(1, idVoucher);

                    ResultSet rs2 = ps2.executeQuery();

                    if(rs2.next()){
                        double persenDiskon = rs2.getDouble("persen_diskon");

                        int maksDiskon = rs2.getInt("maks_diskon");

                        int potongan = (int)(ongkir * (persenDiskon / 100.0));

                        if(potongan > maksDiskon){
                            potongan = maksDiskon;
                        }

                        double ongkirAkhir = ongkir - potongan;

                        if(ongkirAkhir < 0){
                            ongkirAkhir = 0;
                        }

                        hargaAkhir = hargaSub + ongkirAkhir;
                    }
                }

                else if(tipeVoucher.equalsIgnoreCase("DISKON")){

                    String queryDiskon = "SELECT persen_diskon, maks_diskon FROM Diskon WHERE id_voucher = ?";

                    PreparedStatement ps2 = conn.prepareStatement(queryDiskon);
                    ps2.setString(1, idVoucher);
                    ResultSet rs2 = ps2.executeQuery();

                    if(rs2.next()){
                        double persenDiskon = rs2.getDouble("persen_diskon");

                        int maksDiskon = rs2.getInt("maks_diskon");
                        int potongan = (int)(hargaAkhir * (persenDiskon / 100.0));

                        if(potongan > maksDiskon){
                            potongan = maksDiskon;
                        }

                        hargaAkhir -= potongan;

                        if(hargaAkhir < 0){
                            hargaAkhir = 0;
                        }
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        e.getMessage());
                return;
            }
        }

        // Konfirmasi Akhir
        int hargaAkhirFix = (int) hargaAkhir;

        int confirm = JOptionPane.showConfirmDialog(this, "Total Pembayaran : Rp. " + hargaAkhirFix +
                        "\nLanjut checkout?",
                "Konfirmasi Checkout",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm != JOptionPane.YES_OPTION){
            return;
        }

        try {
            conn.setAutoCommit(false);

            LocalDate now = LocalDate.now();

            // GENERATE ID
            String idTransaksi = "TR" + System.currentTimeMillis()%1000000000;

            String idPengiriman = "PG" + System.currentTimeMillis()%1000000000;

            // TOTAL BERAT
            int totalBerat = 0;

            for(Object[] item : keranjangItem){
                totalBerat += ((int)item[9] * (int)item[5]);
            }

            // INSERT PENGIRIMAN
            String queryPengiriman = "INSERT INTO Pengiriman VALUES (?, ?)";
            PreparedStatement psPengiriman = conn.prepareStatement(queryPengiriman);

            psPengiriman.setString(1, idPengiriman);
            psPengiriman.setString(2, "Barang belum diambil");

            psPengiriman.executeUpdate();

            // CLICK AND COLLECT
            if(cbPengiriman.getSelectedItem().toString().equals("Collect")){
                String kodePengambilan = "AMB" + (int)(Math.random()*999999);
                LocalDate batasAmbil = now.plusWeeks(1);

                String queryCollect = "INSERT INTO Click_and_Collect VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psCollect = conn.prepareStatement(queryCollect);

                psCollect.setString(1, idPengiriman);
                psCollect.setString(2, taAlamat.getText());
                psCollect.setDate(3, Date.valueOf(batasAmbil));
                psCollect.setString(4, kodePengambilan);
                psCollect.setString(5, "Belum Diambil");

                psCollect.executeUpdate();
            }
            // CLICK AND DELIVERY
            else {
                String namaEkspedisi = cbEkspedisi.getSelectedItem().toString();
                String queryCariEkspedisi = "SELECT id_ekspedisi " +
                        "FROM Ekspedisi " +
                        "WHERE nama = ?";

                PreparedStatement psCari = conn.prepareStatement(queryCariEkspedisi);

                psCari.setString(1, namaEkspedisi);

                ResultSet rsEkspedisi = psCari.executeQuery();

                String idEkspedisi = "";

                if(rsEkspedisi.next()){
                    idEkspedisi = rsEkspedisi.getString(1);
                }

                String queryDelivery = "INSERT INTO Click_and_Deliver VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psDelivery = conn.prepareStatement(queryDelivery);

                int noResi = (int)(Math.random() * 9000) + 1000;
                psDelivery.setString(1, idPengiriman);
                psDelivery.setString(2, taAlamat.getText());
                psDelivery.setInt(3, noResi);
                psDelivery.setInt(4, 7);
                psDelivery.setString(5, idEkspedisi);

                psDelivery.executeUpdate();
            }

            // INSERT TRANSAKSI
            String queryTransaksi = "INSERT INTO Transaksi " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement psTransaksi = conn.prepareStatement(queryTransaksi);

            psTransaksi.setString(1, idTransaksi);
            psTransaksi.setDate(2, Date.valueOf(now));
            psTransaksi.setInt(3, hargaAkhirFix);
            psTransaksi.setInt(4, totalBerat); // total_berat
            psTransaksi.setInt(5, (int)(hargaSub + ongkir - hargaAkhirFix)); // potongan_harga
            psTransaksi.setString(6, "Pending"); // status
            psTransaksi.setString(7, idPengiriman);
            psTransaksi.setString(8, loggedinUserID);

            if(idVoucherDipakai != null){
                psTransaksi.setString(9, idVoucherDipakai);
            } else {
                psTransaksi.setNull(9, Types.VARCHAR);
            }

            psTransaksi.executeUpdate();

            // METODE PEMBAYARAN
            String method = cbMethod.getSelectedItem().toString();

            // TRANSFER BANK
            if(method.equals("Bank")){
                String queryBank = "INSERT INTO Transfer_Bank " +
                        "VALUES (?, ?, ?)";

                PreparedStatement psBank = conn.prepareStatement(queryBank);

                psBank.setString(1, idTransaksi);

                psBank.setString(2, cbOpsi.getSelectedItem().toString());

                psBank.setString(3, tfOpsi.getText());

                psBank.executeUpdate();
            }

            // KREDIT
            else if(method.equals("Kredit")){
                String queryKredit = "INSERT INTO Kredit " +
                        "VALUES (?, ?, ?, ?)";

                PreparedStatement psKredit = conn.prepareStatement(queryKredit);

                psKredit.setString(1, idTransaksi);

                psKredit.setString(2, cbOpsi.getSelectedItem().toString());

                psKredit.setDate(3, Date.valueOf(LocalDate.of(2030,1,1)));

                psKredit.setString(4, tfOpsi.getText());

                psKredit.executeUpdate();
            }

            // DOMPET DIGITAL
            else if(method.equals("Dompet Digital")){
                String queryDompet = "INSERT INTO Dompet_Digital " +
                        "VALUES (?, ?, ?)";

                PreparedStatement psDompet = conn.prepareStatement(queryDompet);

                psDompet.setString(1, idTransaksi);

                psDompet.setString(2, cbOpsi.getSelectedItem().toString());

                psDompet.setString(3, tfOpsi.getText());

                psDompet.executeUpdate();
            }

            // DETAIL TRANSAKSI
            for(Object[] item : keranjangItem){

                String queryDetail = "INSERT INTO Detail_Transaksi " +
                        "VALUES (?, ?, ?, ?)";

                PreparedStatement psDetail = conn.prepareStatement(queryDetail);

                psDetail.setString(1, idTransaksi);

                psDetail.setString(2, item[6].toString());

                psDetail.setString(3, item[7].toString());

                psDetail.setInt(4, (int)item[5]);

                psDetail.executeUpdate();

                // update stok
                String queryUpdateStok =
                        "UPDATE Varian_Produk " +
                                "SET stok = stok - ? " +
                                "WHERE id_varian = ? AND id_produk = ?";

                PreparedStatement psStok = conn.prepareStatement(queryUpdateStok);

                psStok.setInt(1, (int)item[5]);
                psStok.setString(2, item[7].toString());
                psStok.setString(3, item[6].toString());

                psStok.executeUpdate();
            }

            // UPDATE VOUCHER
            if(idVoucherDipakai != null){
                String queryVoucher = "UPDATE Voucher " +
                        "SET kuota = kuota - 1 " +
                        "WHERE id_voucher = ?";

                PreparedStatement psVoucher = conn.prepareStatement(queryVoucher);

                psVoucher.setString(1, idVoucherDipakai);

                psVoucher.executeUpdate();
            }

            // INSERT POIN HISTORY
            int perubahanPoin = (int) (hargaAkhirFix / 100000);

            if (perubahanPoin > 0) {
                String queryPoin = "INSERT INTO Poin_History (id_history, tanggal, id_transaksi, perubahan_point, id_pelanggan) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement psPoin = conn.prepareStatement(queryPoin);

                String idHistory = "HIS-" + now.toString().replace("-", "") + "-" + (int)(Math.random() * 900 + 100);

                psPoin.setString(1, idHistory);
                psPoin.setDate(2, Date.valueOf(now));
                psPoin.setString(3, idTransaksi);
                psPoin.setInt(4, perubahanPoin);
                psPoin.setString(5, loggedinUserID);

                psPoin.executeUpdate();
                psPoin.close();
            }

            conn.commit();

            JOptionPane.showMessageDialog(this,
                    "Checkout berhasil!");

            keranjangItem.clear();

            refreshDataPengguna();

        } catch (Exception e){
            try{
                conn.rollback();
            } catch (Exception ex){
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, e.getMessage());
        } finally {
            try{
                conn.setAutoCommit(true);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void refreshEkspedisi(){
        try{
            String query = "SELECT nama, id_ekspedisi FROM Ekspedisi ORDER BY nama;";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            cbEkspedisi.removeAllItems();

            dataPengantar.clear();

            while(rs.next()){
                dataPengantar.add(new Object[]{
                        rs.getString(1),
                        rs.getString(2)
                });
                cbEkspedisi.addItem(rs.getString(1));
            }

            taAlamat.setEditable(true);
            taAlamat.setText(akunAlamat.getText());
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void ubahSelectedKeranjang(){
        int index = tableKeranjang.getSelectedRow();

        if(index == -1) return;

        Object[] data = keranjangItem.get(index);
        data[5] = (int) spinnerJumlah.getValue();

        tb3.setValueAt(data[5], index, 5);
        refreshHarga();;
    }

    private void synchronSpinner(ListSelectionEvent e){
        if (e.getValueIsAdjusting()) return;

        int row = tableKeranjang.getSelectedRow();

        if (row == -1) return;

        Object[] select = keranjangItem.get(row);
        int max = (int)select[8];

        SpinnerNumberModel model2 = new SpinnerNumberModel(1, 1, max, 1);
        spinnerJumlah.setModel(model2);

        spinnerJumlah.setValue((int) select[5]);
    }

    private void tambahKeKeranjang(){
        int jumlah = (int) jumlahSelected.getValue();

        if(jumlah == 0){
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0");
            return;
        }

        int index = table2.getSelectedRow();

        if(index == -1) {
            JOptionPane.showMessageDialog(this, "Pilih Produk yang ingin dibeli!");
            return;
        }

        Object[] isi = katalogItem.get(index);

        String idProduk = isi[7].toString();
        String idVarian = isi[8].toString();

        boolean ditemukan = false;
        for(Object[] item : keranjangItem){
            String idProdukKeranjang = item[6].toString();
            String idVarianKeranjang = item[7].toString();
            if(idProdukKeranjang.equals(idProduk) && idVarianKeranjang.equals(idVarian)){
                int jumlahLama = (int) item[5];
                int total = jumlahLama + jumlah;
                if (total > (int)item[8]) {
                    JOptionPane.showMessageDialog(this, "Jumlah melebihi stok!", "Exceeded From Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item[5] = jumlahLama + jumlah;
                ditemukan = true;
                break;
            }
        }

        if(!ditemukan){
            if (jumlah > (int)isi[5]){
                JOptionPane.showMessageDialog(this, "Jumlah melebihi stok!", "Exceeded From Stock", JOptionPane.WARNING_MESSAGE);
                return;
            }

            keranjangItem.add(new Object[]{isi[1], isi[2], isi[3], isi[4], isi[6], jumlah, isi[7], isi[8], isi[5], isi[9]});
        }

        JOptionPane.showMessageDialog(this, "Pesanan berhasil ditambah!", "Success!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void filterBarang(){
        // Possibility 0 : Kembalikan normal jika kosong semua
        if(filterTF.getText().isEmpty() && kategoriCB.getSelectedItem().toString().equals("-") && merkCB.getSelectedItem().toString().equals("-")) refreshKatalog();

        String query2 = "SELECT k.nama_kategori FROM Produk_Mempunyai_Kategori pmk\n" +
                "JOIN Kategori k ON k.id_kategori = pmk.id_kategori\n" +
                "WHERE pmk.id_produk = ?;";

        // Possibility 1: Cari
        if(!filterTF.getText().isEmpty() && kategoriCB.getSelectedItem().toString().equals("-") && merkCB.getSelectedItem().toString().equals("-")){
            String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat FROM Varian_Produk vp\n" +
                    "JOIN Produk p ON vp.id_produk = p.id_produk\n" +
                    "JOIN Merk m ON p.id_merk = m.id_merk\n" +
                    "WHERE p.status = 'Tersedia' AND p.nama LIKE ?\n" +
                    "ORDER BY vp.id_produk; ";

            try{
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, "%" + filterTF.getText().trim() + "%");

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 2: Kategori
        if(filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && merkCB.getSelectedItem().toString().equals("-")){
            String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat FROM Varian_Produk vp\n" +
                    "JOIN Produk p ON vp.id_produk = p.id_produk\n" +
                    "JOIN Merk m ON p.id_merk = m.id_merk\n" +
                    "WHERE p.status = 'Tersedia' AND ? IN (SELECT bb.nama_kategori FROM Produk_Mempunyai_Kategori aa JOIN Kategori bb ON aa.id_kategori = bb.id_kategori WHERE p.id_produk = aa.id_produk)\n" +
                    "ORDER BY vp.id_produk; ";

            try{
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, kategoriCB.getSelectedItem().toString().trim());

                System.out.println(kategoriCB.getSelectedItem().toString());

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 3: Merk
        if(filterTF.getText().isEmpty() && kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat FROM Varian_Produk vp\n" +
                    "JOIN Produk p ON vp.id_produk = p.id_produk\n" +
                    "JOIN Merk m ON p.id_merk = m.id_merk\n" +
                    "WHERE p.status = 'Tersedia' AND m.nama = ?\n" +
                    "ORDER BY vp.id_produk; ";

            try{
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, merkCB.getSelectedItem().toString().trim());

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 4 : Kategori + Merk
        if(filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                            "FROM Varian_Produk vp " +
                            "JOIN Produk p ON vp.id_produk = p.id_produk " +
                            "JOIN Merk m ON p.id_merk = m.id_merk " +
                            "WHERE p.status = 'Tersedia' " +
                            "AND m.nama = ? " +
                            "AND ? IN ( " +
                            "   SELECT bb.nama_kategori " +
                            "   FROM Produk_Mempunyai_Kategori aa " +
                            "   JOIN Kategori bb ON aa.id_kategori = bb.id_kategori " +
                            "   WHERE p.id_produk = aa.id_produk " +
                            ") " +
                            "ORDER BY vp.id_produk;";

            try{
                PreparedStatement ps = conn.prepareStatement(query);

                ps.setString(1, merkCB.getSelectedItem().toString().trim());
                ps.setString(2, kategoriCB.getSelectedItem().toString().trim());

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);

            } catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 5: Cari + Merk
        if(!filterTF.getText().isEmpty() && kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                            "FROM Varian_Produk vp " +
                            "JOIN Produk p ON vp.id_produk = p.id_produk " +
                            "JOIN Merk m ON p.id_merk = m.id_merk " +
                            "WHERE p.status = 'Tersedia' " +
                            "AND p.nama LIKE ? " +
                            "AND m.nama = ? " +
                            "ORDER BY vp.id_produk;";

            try{
                PreparedStatement ps = conn.prepareStatement(query);

                ps.setString(1, "%" + filterTF.getText().trim() + "%");
                ps.setString(2, merkCB.getSelectedItem().toString().trim());

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);

            } catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 6: Cari + Kategori
        if(!filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                            "FROM Varian_Produk vp " +
                            "JOIN Produk p ON vp.id_produk = p.id_produk " +
                            "JOIN Merk m ON p.id_merk = m.id_merk " +
                            "WHERE p.status = 'Tersedia' " +
                            "AND p.nama LIKE ? " +
                            "AND ? IN ( " +
                            "   SELECT bb.nama_kategori " +
                            "   FROM Produk_Mempunyai_Kategori aa " +
                            "   JOIN Kategori bb ON aa.id_kategori = bb.id_kategori " +
                            "   WHERE p.id_produk = aa.id_produk " +
                            ") " +
                            "ORDER BY vp.id_produk;";

            try{
                PreparedStatement ps = conn.prepareStatement(query);

                ps.setString(1, "%" + filterTF.getText().trim() + "%");
                ps.setString(2, kategoriCB.getSelectedItem().toString().trim());

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);

            } catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 7: Cari + Kategori + Merk
        if(!filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat " +
                            "FROM Varian_Produk vp " +
                            "JOIN Produk p ON vp.id_produk = p.id_produk " +
                            "JOIN Merk m ON p.id_merk = m.id_merk " +
                            "WHERE p.status = 'Tersedia' " +
                            "AND p.nama LIKE ? " +
                            "AND m.nama = ? " +
                            "AND ? IN ( " +
                            "   SELECT bb.nama_kategori " +
                            "   FROM Produk_Mempunyai_Kategori aa " +
                            "   JOIN Kategori bb ON aa.id_kategori = bb.id_kategori " +
                            "   WHERE p.id_produk = aa.id_produk " +
                            ") " +
                            "ORDER BY vp.id_produk;";

            try{
                PreparedStatement ps = conn.prepareStatement(query);

                ps.setString(1, "%" + filterTF.getText().trim() + "%");
                ps.setString(2, merkCB.getSelectedItem().toString().trim());
                ps.setString(3, kategoriCB.getSelectedItem().toString().trim());

                ResultSet rs = ps.executeQuery();
                isiTabelKatalog(rs, query2);

            } catch (Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void refreshHarga(){
        hargaSub = 0;
        ongkir = 0;
        double berat = 0;
        for(Object[] x : keranjangItem){
            hargaSub += ((int)x[4] * (int)x[5]);
            berat += (int)x[9] * (int)x[5];
        }

        ongkir = 10000 * (berat/1000);

        DecimalFormat df = new DecimalFormat("#,###");

        TFtotal.setText("RP. " + df.format(hargaSub));

        if(cbPengiriman.getSelectedItem().toString().equals("Collect")){
            lbOngkir.setText("RP. 0");
            ongkir = 0;
        } else {
            lbOngkir.setText("RP. " + df.format(ongkir));
        }
    }

    private void gantiInformasiAkun(int e){
        String informasi = JOptionPane.showInputDialog(this, "Masukan Data Pengganti : ");

        if(informasi == null)  return;;
        if(informasi.isEmpty()) return;

        String query;
        switch (e){
            case 1:
                query = "UPDATE Pelanggan SET id_pelanggan = ? WHERE id_pelanggan = ?";
                break;
            case 2:
                query = "UPDATE Pelanggan SET nama = ? WHERE id_pelanggan = ?";
                break;
            case 3:
                query = "UPDATE Pelanggan SET email = ? WHERE id_pelanggan = ?";
                break;
            case 4:
                query = "UPDATE Pelanggan SET no_telp = ? WHERE id_pelanggan = ?";
                break;
            case 5:
                query = "UPDATE Pelanggan SET alamat_utama = ? WHERE id_pelanggan = ?";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Something went wrong");
                return;
        }

        try{
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, informasi);
            st.setString(2, loggedinUserID);
            st.executeUpdate();

            if(e == 1) loggedinUserID = informasi;
            if(e == 2) loggedinuserNama = informasi;

            refreshDataPengguna();
            st.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshKatalog(){
        String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian, vp.berat FROM Varian_Produk vp\n" +
                "JOIN Produk p ON vp.id_produk = p.id_produk\n" +
                "JOIN Merk m ON p.id_merk = m.id_merk\n" +
                "WHERE p.status = 'Tersedia'\n" +
                "ORDER BY vp.id_produk; ";

        String query2 = "SELECT k.nama_kategori FROM Produk_Mempunyai_Kategori pmk\n" +
                "JOIN Kategori k ON k.id_kategori = pmk.id_kategori\n" +
                "WHERE pmk.id_produk = ?;";

        String query3 = "SELECT nama_kategori FROM Kategori ORDER BY nama_kategori;";

        String query4 = "SELECT nama FROM Merk ORDER BY nama;";

        kategoriCB.removeAllItems();
        merkCB.removeAllItems();
        // Data Tabel
        try{
            PreparedStatement st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            isiTabelKatalog(rs, query2);

            st.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        // Refresh JCombobox
        try {
            PreparedStatement ps = conn.prepareStatement(query3);
            PreparedStatement ps2 = conn.prepareStatement(query4);
            ResultSet rs = ps.executeQuery();
            ResultSet rs2 = ps2.executeQuery();

            kategoriCB.addItem("-");
            merkCB.addItem("-");
            while(rs.next()){
                kategoriCB.addItem(rs.getString(1));
            }

            while(rs2.next()){
                merkCB.addItem(rs2.getString(1));
            }

            ps.close(); ps2.close();
            rs.close(); rs2.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }

    private void isiTabelKatalog(ResultSet rs, String query2) throws SQLException{
        tb2.setRowCount(0);
        katalogItem.clear();
        while(rs.next()){
            String id = rs.getString(1);

            PreparedStatement ps2 = conn.prepareStatement(query2);
            ps2.setString(1, id);

            ResultSet rs2 = ps2.executeQuery();

            StringBuilder kategori = new StringBuilder();
            while(rs2.next()){
                rs2.getString(1);
                kategori.append(rs2.getString(1)).append(" ");
            }
            String kategoriFull = kategori.toString().trim();

            tb2.addRow(new Object[]{kategoriFull, rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6), rs.getInt(7)});
            katalogItem.add(new Object[]{kategoriFull, rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getInt(10)});

            ps2.close();;
            rs2.close();
        }
    }

    private void refreshDataPengguna(){
        int index = tabbedPane1.getSelectedIndex();

        if(index == 0){
            try{
                refreshKatalog();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        if (index == 1){
            tb3.setRowCount(0);
            for(int i=0; i<keranjangItem.size(); i++){
                tb3.addRow(keranjangItem.get(i));
            }

            loadDataPelanggan();

            TierInfo.setText(akunBenefit.getText());
            refreshEkspedisi();
            refreshHarga();
        }

        if(index == 3){
            try{
                String query = "SELECT tanggal, id_transaksi, perubahan_point\n" +
                        "FROM Poin_History\n" +
                        "WHERE id_pelanggan = ?\n" +
                        "ORDER BY tanggal DESC;";
                PreparedStatement ps = conn.prepareStatement(query);

                ps.setString(1, loggedinUserID);
                ResultSet rs = ps.executeQuery();

                tb1.setRowCount(0);
                while(rs.next()){
                    Date tangg = rs.getDate(1);
                    String tang = tangg.toString();
                    tb1.addRow(new Object[]{tang, rs.getString(2), String.valueOf(rs.getInt(3))});
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        if(index == 4){
            loadDataPelanggan();
        }
    }

    private void loadDataPelanggan(){
        try{
            String query = "SELECT * FROM Pelanggan WHERE id_pelanggan = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, loggedinUserID);

            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                akunID.setText(rs.getString(1));
                akunNama.setText(rs.getString(2));
                akunEmail.setText(rs.getString(3));
                akunTelp.setText(rs.getString(4));

                Date tanggal = rs.getDate(5);
                akunCreated.setText(tanggal.toString());

                akunAlamat.setText(rs.getString(6));

                String query2 = "SELECT nama_tier, benefit FROM Tier_Loyalitas WHERE id_tier = ?";

                PreparedStatement ps2 = conn.prepareStatement(query2);
                ps2.setString(1, rs.getString(7));

                ResultSet rs2 = ps2.executeQuery();

                while(rs2.next()){
                    String info = rs.getString(7) + " - " + rs2.getString(1);
                    akunTier.setText(info);
                    akunBenefit.setText(rs2.getString(2));
                }

                rs2.close();
                ps2.close();
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void registPembeli(){
        String id = IDRegist.getText().trim();
        String nama = namaRegist.getText().trim();
        String email = emailRegist.getText().trim();
        String telp = telpRegist.getText().trim();
        String alamat = alamatRegist.getText().trim();
        LocalDate tanggal = LocalDate.now();

        if(id.isEmpty() || nama.isEmpty() || email.isEmpty() || telp.isEmpty() || alamat.isEmpty()){
            JOptionPane.showMessageDialog(this, "Data tidak boleh kosong");
        }

        String query = "INSERT INTO Pelanggan VALUES (?, ?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setString(3, email);
            ps.setString(4, telp);
            ps.setDate(5, Date.valueOf(tanggal));
            ps.setString(6, alamat);
            ps.setString(7, "TR01");

            ps.executeUpdate();

            ps.close();

            JOptionPane.showMessageDialog(this, "Akun berhasil ditambahkan!");
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loginFrontend(){
        String data = textFieldFront.getText().trim();
        if(data.isEmpty()){
            JOptionPane.showMessageDialog(this, "ID Kosong");
            return;
        }

        try{
            String query = "Select id_pelanggan, nama From Pelanggan Where id_pelanggan = ?";
            PreparedStatement st = conn.prepareStatement(query);

            st.setString(1, data);
            ResultSet rs = st.executeQuery();

            if(!rs.next()){
                JOptionPane.showMessageDialog(this, "Data Pelanggan tidak Ditemukan!", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            loggedinUserID = rs.getString(1);
            loggedinuserNama = rs.getString(2);

            welcome.setText("Hai, " + loggedinuserNama);
            refreshDataPengguna();

            st.close();
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        c1.show(MainPanel, "Front");
    }

    private JPanel buildTransactionsPanel(String filter, String order) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel kontrol di atas
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Filter & Urutkan"));

        // Filter dropdown
        JLabel filterLabel = new JLabel("Filter Waktu:");
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Last Week", "Last Month", "Last Year"});
        filterCombo.setSelectedItem(filter.equals("ALL") ? "All" :
                filter.equals("WEEK") ? "Last Week" :
                        filter.equals("MONTH") ? "Last Month" : "Last Year");

        // Sort dropdown
        JLabel sortLabel = new JLabel("Urutkan:");
        JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Terbaru (DESC)", "Terlama (ASC)"});
        sortCombo.setSelectedItem(order.equals("DESC") ? "Terbaru (DESC)" : "Terlama (ASC)");

        JButton applyButton = new JButton("Terapkan");

        controlPanel.add(filterLabel);
        controlPanel.add(filterCombo);
        controlPanel.add(sortLabel);
        controlPanel.add(sortCombo);
        controlPanel.add(applyButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Container untuk daftar kartu transaksi
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));

        // Query dinamis
        String selectedFilter = (String) filterCombo.getSelectedItem();
        String filterCondition = "";
        if ("Last Week".equals(selectedFilter)) {
            filterCondition = "AND t.tanggal >= DATEADD(day, -7, GETDATE())";
        } else if ("Last Month".equals(selectedFilter)) {
            filterCondition = "AND t.tanggal >= DATEADD(month, -1, GETDATE())";
        } else if ("Last Year".equals(selectedFilter)) {
            filterCondition = "AND t.tanggal >= DATEADD(year, -1, GETDATE())";
        }
        // All -> tidak ada tambahan kondisi

        String orderBy = sortCombo.getSelectedItem().equals("Terbaru (DESC)") ? "DESC" : "ASC";

        String query =
                "SELECT t.id_transaksi, t.tanggal, t.total_harga, t.status AS status_transaksi, " +
                        "       p.status AS status_pengiriman, " +
                        "       COALESCE(cd.alamat, cc.alamat_gerai) AS alamat_pengiriman, " +
                        "       CASE WHEN cd.id_pengiriman IS NOT NULL THEN 'Delivery' ELSE 'Collect' END AS jenis_pengiriman " +
                        "FROM Transaksi t " +
                        "JOIN Pengiriman p ON t.id_pengiriman = p.id_pengiriman " +
                        "LEFT JOIN Click_and_Deliver cd ON p.id_pengiriman = cd.id_pengiriman " +
                        "LEFT JOIN Click_and_Collect cc ON p.id_pengiriman = cc.id_pengiriman " +
                        "WHERE t.id_pelanggan = ? " + filterCondition +
                        " ORDER BY t.tanggal " + orderBy;

        // Isi containerPanel dengan data
        loadTransactionsToContainer(containerPanel, query);

        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Listener untuk tombol apply
        applyButton.addActionListener(e -> {
            String newFilter = (String) filterCombo.getSelectedItem();
            String filterCode = "ALL";
            if ("Last Week".equals(newFilter)) filterCode = "WEEK";
            else if ("Last Month".equals(newFilter)) filterCode = "MONTH";
            else if ("Last Year".equals(newFilter)) filterCode = "YEAR";

            String newOrder = sortCombo.getSelectedItem().equals("Terbaru (DESC)") ? "DESC" : "ASC";
            JPanel newPanel = buildTransactionsPanel(filterCode, newOrder);
            tabbedPane1.setComponentAt(2, newPanel);
            tabbedPane1.setSelectedIndex(2); // refresh tampilan
        });

        return mainPanel;
    }

    private void loadTransactionsToContainer(JPanel container, String query) {
        container.removeAll();
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, loggedinUserID);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    String idTx = rs.getString("id_transaksi");
                    Date tanggal = rs.getDate("tanggal");
                    double total = rs.getDouble("total_harga");
                    String statusTransaksi = rs.getString("status_transaksi");
                    String statusPengiriman = rs.getString("status_pengiriman");
                    String alamat = rs.getString("alamat_pengiriman");
                    String jenis = rs.getString("jenis_pengiriman");

                    JPanel card = createTransactionsCard(idTx, tanggal, total, statusTransaksi, statusPengiriman, jenis, alamat);
                    container.add(card);
                    container.add(Box.createRigidArea(new Dimension(0, 10)));
                }
                if (!hasData) {
                    JLabel emptyLabel = new JLabel("Belum ada riwayat transaksi.", SwingConstants.CENTER);
                    emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                    container.add(emptyLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Gagal memuat data transaksi: " + e.getMessage(), SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            container.add(errorLabel);
        }
        container.revalidate();
        container.repaint();
    }

    private JPanel createTransactionsCard(String idTransaksi, Date tanggal, double totalHarga, String statusTransaksi, String statusPengiriman, String jenis, String alamat) {
        JPanel cardPanel = new JPanel(new BorderLayout(15, 10));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Info kiri
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.add(new JLabel("ID Transaksi: " + idTransaksi));
        infoPanel.add(new JLabel("Tanggal: " + (tanggal != null ? tanggal.toString() : "-")));
        infoPanel.add(new JLabel("Status Transaksi: " + statusTransaksi));
        infoPanel.add(new JLabel("Pengiriman: " + jenis + " | " + statusPengiriman + " | " + alamat));

        // Info kanan
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        DecimalFormat df = new DecimalFormat("#,###");
        JLabel priceLabel = new JLabel("Total: Rp " + df.format(totalHarga), SwingConstants.RIGHT);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton viewDetailButton = new JButton("Lihat Detail Barang");
        viewDetailButton.addActionListener(e -> showTransactionDetail(idTransaksi));

        actionPanel.add(priceLabel);
        actionPanel.add(viewDetailButton);

        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(actionPanel, BorderLayout.EAST);
        return cardPanel;
    }

    private void showTransactionDetail(String idTransaksi) {
        JDialog detailDialog = new JDialog(this, "Detail Barang - Transaksi " + idTransaksi, true);
        detailDialog.setSize(700, 400);
        detailDialog.setLayout(new BorderLayout());

        DefaultTableModel detailTableModel = new DefaultTableModel(
                new Object[]{"Nama Produk", "Ukuran", "Warna", "Jumlah", "Harga Satuan", "Subtotal"}, 0
        );
        JTable detailTable = new JTable(detailTableModel);
        detailDialog.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        String detailQuery =
                "SELECT p.nama, vp.ukuran, vp.warna, dt.jumlah, vp.harga, (dt.jumlah * vp.harga) AS subtotal " +
                        "FROM Detail_Transaksi dt " +
                        "JOIN Varian_Produk vp ON dt.id_produk = vp.id_produk AND dt.id_varian = vp.id_varian " +
                        "JOIN Produk p ON vp.id_produk = p.id_produk " +
                        "WHERE dt.id_transaksi = ?";

        try (PreparedStatement ps = conn.prepareStatement(detailQuery)) {
            ps.setString(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detailTableModel.addRow(new Object[]{
                            rs.getString("nama"),
                            rs.getString("ukuran"),
                            rs.getString("warna"),
                            rs.getInt("jumlah"),
                            rs.getDouble("harga"),
                            rs.getDouble("subtotal")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat detail barang: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }

    public static void main(String[] args){
        new App();
    }
}