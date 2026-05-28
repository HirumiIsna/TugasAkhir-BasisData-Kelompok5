package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PotonganDAO {
    private Connection conn;

    public PotonganDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public PotonganDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idVoucher, int nominal) {
        String query = "INSERT INTO Potongan (id_voucher, nominal) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ps.setInt(2, nominal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by voucher ID)
    public Map<String, Object> getById(String idVoucher) {
        String query = "SELECT * FROM Potongan WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> potongan = new HashMap<>();
                potongan.put("id_voucher", rs.getString("id_voucher"));
                potongan.put("nominal", rs.getInt("nominal"));
                return potongan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean update(String idVoucher, int nominal) {
        String query = "UPDATE Potongan SET nominal = ? WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, nominal);
            ps.setString(2, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idVoucher) {
        String query = "DELETE FROM Potongan WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}