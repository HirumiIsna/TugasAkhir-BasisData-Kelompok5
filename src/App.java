package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class App extends JFrame {
    // Table
    DefaultTableModel tb1;

    // Sql
    static String url = configLoginSql.url;
    static String userName = configLoginSql.userName;
    static String password = configLoginSql.password;
    Connection conn = configLoginSql.setConnection();

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
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextArea ID;
    private JTable TabelManagerProduk;
    private JPanel Panel_TFProduk;
    private JButton simpanButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshDataButton;

    // Card Layout
    private CardLayout c1;

    // Constrcutor
    public App(){
        setContentPane(MainPanel);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        refreshDataButton.addActionListener((e) -> loadDataProduk());
        deleteButton.addActionListener((e) -> deleteProduk());

        tabbedPane1.addChangeListener((e) -> refreshDataPengguna());

        tb1 = new DefaultTableModel();
        tb1.addColumn("Tanggal");
        tb1.addColumn("ID Transaksi");
        tb1.addColumn("Perubahan Poin");

        table1.setModel(tb1);

        loadDataProduk();

        setVisible(true);
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

    private void refreshDataPengguna(){
        int index = tabbedPane1.getSelectedIndex();

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

            st.close();
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        c1.show(MainPanel, "Front");
    }

    //backend
    private void loadDataProduk() {
        DefaultTableModel tbMP = new DefaultTableModel();

        tbMP.addColumn("ID Produk");
        tbMP.addColumn("ID Varian");
        tbMP.addColumn("Status");
        tbMP.addColumn("Nama Produk");
        tbMP.addColumn("Deskripsi");
        tbMP.addColumn("Nama Merk");
        tbMP.addColumn("Ukuran");
        tbMP.addColumn("Stok");

        try {
            String query = "SELECT p.id_produk, vp.id_varian, p.status, p.nama, p.deskripsi, m.nama AS nama_merk, vp.ukuran, vp.stok " +
                           "FROM Produk p JOIN Merk m ON p.id_merk = m.id_merk JOIN Varian_Produk vp ON p.id_produk = vp.id_produk";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tbMP.addRow(new Object[]{
                        rs.getString("id_produk"),
                        rs.getString("id_varian"),
                        rs.getString("status"),
                        rs.getString("nama"),
                        rs.getString("deskripsi"),
                        rs.getString("nama_merk"),
                        rs.getString("ukuran"),
                        rs.getInt("stok")
                });
            }

            TabelManagerProduk.setModel(tbMP);

            rs.close();
            ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void deleteProduk() {

        int selectedRow = TabelManagerProduk.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this,"Pilih data terlebih dahulu");
            return;
        }

        String idProduk = TabelManagerProduk.getValueAt(selectedRow, 0).toString();

        String idVarian = TabelManagerProduk.getValueAt(selectedRow, 1).toString();

        try {
            String queryDeleteDetail = "DELETE FROM Detail_Transaksi WHERE id_produk = ? AND id_varian = ?";

            PreparedStatement psCheck = conn.prepareStatement(queryDeleteDetail);

            psCheck.setString(1, idProduk);
            psCheck.setString(2, idVarian);

            psCheck.executeUpdate();

            psCheck.close();

            int confirm = JOptionPane.showConfirmDialog(this,"Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if(confirm != JOptionPane.YES_OPTION){
                return;
            }

            String queryDeleteVarian = "DELETE FROM Varian_Produk WHERE id_produk = ? AND id_varian = ?";
            PreparedStatement ps1 = conn.prepareStatement(queryDeleteVarian);

            ps1.setString(1, idProduk);
            ps1.setString(2, idVarian);

            ps1.executeUpdate();

            ps1.close();

            String queryCheckVarian = "SELECT COUNT(*) FROM Varian_Produk WHERE id_produk = ?";
            PreparedStatement ps2 = conn.prepareStatement(queryCheckVarian);
            ps2.setString(1, idProduk);
            ResultSet rs2 = ps2.executeQuery();
            int jumlahVarian = 0;
            if(rs2.next()){
                jumlahVarian = rs2.getInt(1);
            }

            rs2.close();
            ps2.close();

            if(jumlahVarian == 0){
                String queryDeleteProduk = "DELETE FROM Produk WHERE id_produk = ?";
                PreparedStatement ps3 = conn.prepareStatement(queryDeleteProduk);
                ps3.setString(1, idProduk);
                ps3.executeUpdate();
                ps3.close();
            }
            loadDataProduk();
            JOptionPane.showMessageDialog(this,"Data berhasil dihapus");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args){
        new App();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
