package src.backend.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OngkirDAO {
    private Connection conn;

    public OngkirDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public OngkirDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idVoucher, int persenDiskon, int maksDiskon) {
        String query = "INSERT INTO Ongkir (id_voucher, persen_diskon, maks_diskon) VALUES (?, ?, ?)";
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
        String query = "SELECT * FROM Ongkir WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> ongkir = new HashMap<>();
                ongkir.put("id_voucher", rs.getString("id_voucher"));
                ongkir.put("persen_diskon", rs.getInt("persen_diskon"));
                ongkir.put("maks_diskon", rs.getInt("maks_diskon"));
                return ongkir;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean update(String idVoucher, int persenDiskon, int maksDiskon) {
        String query = "UPDATE Ongkir SET persen_diskon = ?, maks_diskon = ? WHERE id_voucher = ?";
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
        String query = "DELETE FROM Ongkir WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}