package src.FrontEnd;

import src.App;

import javax.swing.*;
import java.sql.*;

public class UserManager {
    private final App app;
    private Connection conn;

    public UserManager(App app, Connection conn) {
        this.app = app;
        this.conn = conn;
    }

    public void loadDataPelanggan(String loggedinUserID) {
        try {
            String query = "SELECT * FROM Pelanggan WHERE id_pelanggan = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, loggedinUserID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                app.setAkunID(rs.getString(1));
                app.setAkunNama(rs.getString(2));
                app.setAkunEmail(rs.getString(3));
                app.setAkunTelp(rs.getString(4));

                Date tanggal = rs.getDate(5);
                app.setAkunCreated(tanggal.toString());
                app.setAkunAlamat(rs.getString(6));

                String query2 = "SELECT nama_tier, benefit FROM Tier_Loyalitas WHERE id_tier = ?";
                PreparedStatement ps2 = conn.prepareStatement(query2);
                ps2.setString(1, rs.getString(7));
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    String info = rs.getString(7) + " - " + rs2.getString(1);
                    app.setAkunTier(info);
                    app.setAkunBenefit(rs2.getString(2));
                }
                rs2.close();
                ps2.close();
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(app, e.getMessage());
        }
    }

    public void gantiInformasiAkun(int field, String loggedinUserID) {
        String informasi = JOptionPane.showInputDialog(app, "Masukan Data Pengganti : ");
        if (informasi == null || informasi.isEmpty()) return;

        String query;
        switch (field) {
            case 1: query = "UPDATE Pelanggan SET id_pelanggan = ? WHERE id_pelanggan = ?"; break;
            case 2: query = "UPDATE Pelanggan SET nama = ? WHERE id_pelanggan = ?"; break;
            case 3: query = "UPDATE Pelanggan SET email = ? WHERE id_pelanggan = ?"; break;
            case 4: query = "UPDATE Pelanggan SET no_telp = ? WHERE id_pelanggan = ?"; break;
            case 5: query = "UPDATE Pelanggan SET alamat_utama = ? WHERE id_pelanggan = ?"; break;
            default: JOptionPane.showMessageDialog(app, "Something went wrong"); return;
        }

        try {
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, informasi);
            st.setString(2, loggedinUserID);
            st.executeUpdate();
            if (field == 1) app.setLoggedinUserID(informasi);
            if (field == 2) app.setLoggedinuserNama(informasi);
            app.refreshDataPengguna();
            st.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(app, ex.getMessage());
        }
    }
}