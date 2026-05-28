package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DiskonDAO {
    private Connection conn;

    public DiskonDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public DiskonDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idVoucher, int persenDiskon, int maksDiskon) {
        String query = "INSERT INTO Diskon (id_voucher, persen_diskon, maks_diskon) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ps.setInt(2, persenDiskon);
            ps.setInt(3, maksDiskon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by voucher ID)
    public Map<String, Object> getById(String idVoucher) {
        String query = "SELECT * FROM Diskon WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> diskon = new HashMap<>();
                diskon.put("id_voucher", rs.getString("id_voucher"));
                diskon.put("persen_diskon", rs.getInt("persen_diskon"));
                diskon.put("maks_diskon", rs.getInt("maks_diskon"));
                return diskon;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean update(String idVoucher, int persenDiskon, int maksDiskon) {
        String query = "UPDATE Diskon SET persen_diskon = ?, maks_diskon = ? WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, persenDiskon);
            ps.setInt(2, maksDiskon);
            ps.setString(3, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idVoucher) {
        String query = "DELETE FROM Diskon WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}