package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
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

    // Arraylist Katalog
    ArrayList<Object[]> katalogItem = new ArrayList<>();
    ArrayList<Object[]> keranjangItem = new ArrayList<>();

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

        tb1 = new DefaultTableModel(); tb1.addColumn("Tanggal");
        tb1.addColumn("ID Transaksi"); tb1.addColumn("Perubahan Poin");

        tb2 = new DefaultTableModel();
        tb2.addColumn("Kategori");
        tb2.addColumn("Nama"); tb2.addColumn("Merk");
        tb2.addColumn("Ukuran"); tb2.addColumn("Warna");
        tb2.addColumn("Stok"); tb2.addColumn("Harga");
        table2.setModel(tb2);

        tb3 = new DefaultTableModel();
        tb3.addColumn("Nama"); tb3.addColumn("Merk"); tb3.addColumn("Ukuran");
        tb3.addColumn("Warna"); tb3.addColumn("Harga"); tb3.addColumn("Jumlah");
        tableKeranjang.setModel(tb3);

        table2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table1.setModel(tb1);
        setVisible(true);
    }

    private void tambahKeKeranjang(){
        if((int)jumlahSelected.getValue() == 0){
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0");
            return;
        }

        int index = table2.getSelectedRow();

        if(index == -1) {
            JOptionPane.showMessageDialog(this, "Pilih Produk yang ingin dibeli!");
            return;
        }

        Object[] isi = katalogItem.get(index);
        keranjangItem.add(new Object[]{isi[1],isi[2], isi[3], isi[4], isi[6], (int)jumlahSelected.getValue(), isi[7], isi[8]});

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
            String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga FROM Varian_Produk vp\n" +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 2: Kategori
        if(filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && merkCB.getSelectedItem().toString().equals("-")){
            String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga FROM Varian_Produk vp\n" +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 3: Merk
        if(filterTF.getText().isEmpty() && kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga FROM Varian_Produk vp\n" +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 4 : Kategori + Merk
        if(filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga " +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 5: Cari + Merk
        if(!filterTF.getText().isEmpty() && kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga " +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 6: Cari + Kategori
        if(!filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga " +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        // Possibility 7: Cari + Kategori + Merk
        if(!filterTF.getText().isEmpty() && !kategoriCB.getSelectedItem().toString().equals("-") && !merkCB.getSelectedItem().toString().equals("-")){
            String query =
                    "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga " +
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
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
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshKatalog(){
        String query = "SELECT vp.id_produk, p.nama, m.nama, vp.ukuran, vp.warna, vp.stok, vp.harga, p.id_produk, vp.id_varian FROM Varian_Produk vp\n" +
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

            while(rs.next()){
                isiTabelKatalog(rs, query2);
            }
            st.close();
            rs.close();
        } catch (Exception e) {
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
                    rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getString(8), rs.getString(9)});

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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        if (index == 1){
            for(int i=0; i<keranjangItem.size(); i++){
                tb3.addRow(keranjangItem.get(i));
            }
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }

        if(index == 4){
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
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
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
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        c1.show(MainPanel, "Front");
    }

    public static void main(String[] args){
        new App();
    }
}