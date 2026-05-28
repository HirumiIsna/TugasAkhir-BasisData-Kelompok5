package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class KreditDAO {
    private Connection conn;

    public KreditDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public KreditDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idTransaksi, String namaBank, String masa, String noKartu) {
        String query = "INSERT INTO Kredit (id_transaksi, nama_bank, masa, no_kartu) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, namaBank);
            ps.setString(3, masa);
            ps.setString(4, noKartu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by transaksi ID)
    public Map<String, Object> getById(String idTransaksi) {
        String query = "SELECT * FROM Kredit WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> kredit = new HashMap<>();
                kredit.put("id_transaksi", rs.getString("id_transaksi"));
                kredit.put("nama_bank", rs.getString("nama_bank"));
                kredit.put("masa", rs.getString("masa"));
                kredit.put("no_kartu", rs.getString("no_kartu"));
                return kredit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}