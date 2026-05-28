package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DompetDigitalDAO {
    private Connection conn;

    public DompetDigitalDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public DompetDigitalDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idTransaksi, String jenisDompet, String noTelp) {
        String query = "INSERT INTO Dompet_Digital (id_transaksi, jenis_dompet, no_telp) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, jenisDompet);
            ps.setString(3, noTelp);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by transaksi ID)
    public Map<String, Object> getById(String idTransaksi) {
        String query = "SELECT * FROM Dompet_Digital WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> dompet = new HashMap<>();
                dompet.put("id_transaksi", rs.getString("id_transaksi"));
                dompet.put("jenis_dompet", rs.getString("jenis_dompet"));
                dompet.put("no_telp", rs.getString("no_telp"));
                return dompet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}