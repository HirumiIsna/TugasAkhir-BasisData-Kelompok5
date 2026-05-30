package src.FrontEnd;

import src.App;
import src.backend.database.DatabaseConnection;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class LoginManager {
    private final App app;
    private Connection conn;

    public LoginManager(App app) {
        this.app = app;
        this.conn = DatabaseConnection.getConnection();
    }

    public Connection getConnection() {
        return conn;
    }

    public void loginFrontend(String idFieldText) {
        String data = idFieldText.trim();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(app, "ID Kosong");
            return;
        }

        try {
            String query = "SELECT id_pelanggan, nama FROM Pelanggan WHERE id_pelanggan = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, data);
            ResultSet rs = st.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(app, "Data Pelanggan tidak Ditemukan!", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            app.setLoggedinUserID(rs.getString(1));
            app.setLoggedinuserNama(rs.getString(2));

            app.setWelcomeText("Hai, " + app.getLoggedinuserNama());
            app.refreshDataPengguna();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, e.getMessage());
            return;
        }
        app.showFrontPanel();
    }

    public void registPembeli(String id, String nama, String email, String telp, String alamat) {
        if (id.isEmpty() || nama.isEmpty() || email.isEmpty() || telp.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(app, "Data tidak boleh kosong");
            return;
        }
        LocalDate tanggal = LocalDate.now();
        String query = "INSERT INTO Pelanggan VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
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
            JOptionPane.showMessageDialog(app, "Akun berhasil ditambahkan!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
