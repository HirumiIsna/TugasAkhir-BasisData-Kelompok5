package src.backend.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TransferBankDAO {
    private Connection conn;

    public TransferBankDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public TransferBankDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idTransaksi, String namaBank, String noRek) {
        String query = "INSERT INTO Transfer_Bank (id_transaksi, nama_bank, no_rek) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ps.setString(2, namaBank);
            ps.setString(3, noRek);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by transaksi ID)
    public Map<String, Object> getById(String idTransaksi) {
        String query = "SELECT * FROM Transfer_Bank WHERE id_transaksi = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> transfer = new HashMap<>();
                transfer.put("id_transaksi", rs.getString("id_transaksi"));
                transfer.put("nama_bank", rs.getString("nama_bank"));
                transfer.put("no_rek", rs.getString("no_rek"));
                return transfer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}