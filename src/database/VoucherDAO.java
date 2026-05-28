package src.database;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherDAO {
    private Connection conn;

    public VoucherDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public VoucherDAO(Connection conn) {
        this.conn = conn;
    }

    // CREATE (base voucher)
    public boolean insertBase(String idVoucher, String kode, int minBelanja, Date tglMulai, Date tglBerakhir, int kuota) {
        String query = "INSERT INTO Voucher (id_voucher, kode, min_belanja, tgl_mulai, tgl_berakhir, kuota) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ps.setString(2, kode);
            ps.setInt(3, minBelanja);
            ps.setDate(4, tglMulai);
            ps.setDate(5, tglBerakhir);
            ps.setInt(6, kuota);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ (by ID)
    public Map<String, Object> getById(String idVoucher) {
        String query = "SELECT * FROM Voucher WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, idVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> voucher = new HashMap<>();
                voucher.put("id_voucher", rs.getString("id_voucher"));
                voucher.put("kode", rs.getString("kode"));
                voucher.put("min_belanja", rs.getInt("min_belanja"));
                voucher.put("tgl_mulai", rs.getDate("tgl_mulai"));
                voucher.put("tgl_berakhir", rs.getDate("tgl_berakhir"));
                voucher.put("kuota", rs.getInt("kuota"));
                return voucher;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (by code, valid)
    public Map<String, Object> getByCode(String kodeVoucher) {
        String query = "SELECT * FROM Voucher WHERE kode = ? AND tgl_mulai <= GETDATE() AND tgl_berakhir >= GETDATE() AND kuota > 0";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, kodeVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> voucher = new HashMap<>();
                voucher.put("id_voucher", rs.getString("id_voucher"));
                voucher.put("kode", rs.getString("kode"));
                voucher.put("min_belanja", rs.getInt("min_belanja"));
                voucher.put("tgl_mulai", rs.getDate("tgl_mulai"));
                voucher.put("tgl_berakhir", rs.getDate("tgl_berakhir"));
                voucher.put("kuota", rs.getInt("kuota"));
                return voucher;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ (all)
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT * FROM Voucher ORDER BY tgl_berakhir DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> voucher = new HashMap<>();
                voucher.put("id_voucher", rs.getString("id_voucher"));
                voucher.put("kode", rs.getString("kode"));
                voucher.put("min_belanja", rs.getInt("min_belanja"));
                voucher.put("tgl_mulai", rs.getDate("tgl_mulai"));
                voucher.put("tgl_berakhir", rs.getDate("tgl_berakhir"));
                voucher.put("kuota", rs.getInt("kuota"));
                list.add(voucher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // READ (all with type info)
    public List<Map<String, Object>> getAllWithType() {
        List<Map<String, Object>> list = new ArrayList<>();
        String query = "SELECT v.*, d.persen_diskon as diskon_persen, d.maks_diskon as diskon_maks, " +
                "p.nominal as potongan_nominal, o.persen_diskon as ongkir_persen, o.maks_diskon as ongkir_maks " +
                "FROM Voucher v " +
                "LEFT JOIN Diskon d ON v.id_voucher = d.id_voucher " +
                "LEFT JOIN Potongan p ON v.id_voucher = p.id_voucher " +
                "LEFT JOIN Ongkir o ON v.id_voucher = o.id_voucher";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, Object> voucher = new HashMap<>();
                voucher.put("id_voucher", rs.getString("id_voucher"));
                voucher.put("kode", rs.getString("kode"));
                voucher.put("min_belanja", rs.getInt("min_belanja"));
                voucher.put("tgl_mulai", rs.getDate("tgl_mulai"));
                voucher.put("tgl_berakhir", rs.getDate("tgl_berakhir"));
                voucher.put("kuota", rs.getInt("kuota"));

                if (rs.getObject("diskon_persen") != null) {
                    voucher.put("type", "DISKON");
                    voucher.put("persen_diskon", rs.getInt("diskon_persen"));
                    voucher.put("maks_diskon", rs.getInt("diskon_maks"));
                } else if (rs.getObject("potongan_nominal") != null) {
                    voucher.put("type", "POTONGAN");
                    voucher.put("nominal", rs.getInt("potongan_nominal"));
                } else if (rs.getObject("ongkir_persen") != null) {
                    voucher.put("type", "ONGKIR");
                    voucher.put("persen_diskon", rs.getInt("ongkir_persen"));
                    voucher.put("maks_diskon", rs.getInt("ongkir_maks"));
                }
                list.add(voucher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE kuota
    public boolean updateKuota(String idVoucher, int kuota) {
        String query = "UPDATE Voucher SET kuota = ? WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, kuota);
            ps.setString(2, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // UPDATE dates
    public boolean updateDates(String idVoucher, Date tglMulai, Date tglBerakhir) {
        String query = "UPDATE Voucher SET tgl_mulai = ?, tgl_berakhir = ? WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, tglMulai);
            ps.setDate(2, tglBerakhir);
            ps.setString(3, idVoucher);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // USE voucher (decrease kuota)
    public boolean useVoucher(String idVoucher) {
        String query = "UPDATE Voucher SET kuota = kuota - 1 WHERE id_voucher = ? AND kuota > 0";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean delete(String idVoucher) {
        String query = "DELETE FROM Voucher WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}