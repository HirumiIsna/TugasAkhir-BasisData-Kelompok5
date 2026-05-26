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
        setVisible(true);
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

            while (rs.next()){
                loggedinUserID = rs.getString(1);
                loggedinuserNama = rs.getString(2);
            }

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
