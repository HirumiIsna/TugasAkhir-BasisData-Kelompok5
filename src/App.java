package src;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class App extends JFrame {
    // Sql
    static String url = "jdbc:sqlserver://localhost:1433;databaseName=matahari2;encrypt=true;trustServerCertificate=true";
    static String userName = "testhdr";
    static String password = "pass123";
    Connection conn = null;

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
    private JTextArea ID;

    // Card Layout
    private CardLayout c1;

    // Constrcutor
    public App(){
        setConnection();
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

        tabbedPane1.addChangeListener((e) -> refreshDataPengguna());
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

        if(index == 1){
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

    private void setConnection(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error Sql", "Error", JOptionPane.WARNING_MESSAGE);
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

    public static void main(String[] args){
        new App();
    }
}
