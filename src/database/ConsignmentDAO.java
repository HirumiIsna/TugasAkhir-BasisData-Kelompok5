package src.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsignmentDAO {
    private Connection conn;

    public ConsignmentDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public ConsignmentDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean insert(String idPemasok, double persenBagiHasil, int periodeSettlement, String ketentuanRetur, int stokTitipan) {
        String query = "INSERT INTO Consignment (id_pemasok, Persen_bagi_hasil, Periode_Settlement, Ketentuan_retur, Stok_titipan) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            ps.setDouble(2, persenBagiHasil);
            ps.setInt(3, periodeSettlement);
            ps.setString(4, ketentuanRetur);
            ps.setInt(5, stokTitipan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idPemasok) {
        String query = "SELECT c.*, p.nama as pemasok_nama FROM Consignment c JOIN Pemasok p ON c.id_pemasok = p.id_pemasok WHERE c.id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> consignment = new HashMap<>();
                consignment.put("id_pemasok", rs.getString("id_pemasok"));
                consignment.put("pemasok_nama", rs.getString("pemasok_nama"));
                consignment.put("persen_bagi_hasil", rs.getDouble("Persen_bagi_hasil"));
                consignment.put("periode_settlement", rs.getInt("Periode_Settlement"));
                consignment.put("ketentuan_retur", rs.getString("Ketentuan_retur"));
                consignment.put("stok_titipan", rs.getInt("Stok_titipan"));
                return consignment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT c.*, p.nama as pemasok_nama FROM Consignment c JOIN Pemasok p ON c.id_pemasok = p.id_pemasok";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> consignment = new HashMap<>();
                consignment.put("id_pemasok", rs.getString("id_pemasok"));
                consignment.put("pemasok_nama", rs.getString("pemasok_nama"));
                consignment.put("persen_bagi_hasil", rs.getDouble("Persen_bagi_hasil"));
                consignment.put("periode_settlement", rs.getInt("Periode_Settlement"));
                consignment.put("ketentuan_retur", rs.getString("Ketentuan_retur"));
                consignment.put("stok_titipan", rs.getInt("Stok_titipan"));
                list.add(consignment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public boolean update(String idPemasok, double persenBagiHasil, int periodeSettlement, String ketentuanRetur, int stokTitipan) {
        String query = "UPDATE Consignment SET Persen_bagi_hasil = ?, Periode_Settlement = ?, Ketentuan_retur = ?, Stok_titipan = ? WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, persenBagiHasil);
            ps.setInt(2, periodeSettlement);
            ps.setString(3, ketentuanRetur);
            ps.setInt(4, stokTitipan);
            ps.setString(5, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idPemasok) {
        String query = "DELETE FROM Consignment WHERE id_pemasok = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idPemasok);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}