import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    private Connection conn;

    public DatabaseHandler() {
        conn = DBConfig.getConnection();
    }

    public String[] login(String userId) throws SQLException {
        String query = "SELECT id_pelanggan, nama FROM Pelanggan WHERE id_pelanggan = ?";
        try (PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, userId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString(1), rs.getString(2)};
                }
            }
        }
        return null;
    }

    public void registerPembeli(String id, String nama, String email, String telp, String alamat) throws SQLException {
        String query = "INSERT INTO Pelanggan VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setString(3, email);
            ps.setString(4, telp);
            ps.setDate(5, Date.valueOf(LocalDate.now()));
            ps.setString(6, alamat);
            ps.setString(7, "TR01");
            ps.executeUpdate();
        }
    }

    public Map<String, String> getPelangganData(String userId) throws SQLException {
        Map<String, String> userData = new HashMap<>();
        String query = "SELECT * FROM Pelanggan WHERE id_pelanggan = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userData.put("id", rs.getString(1));
                    userData.put("nama", rs.getString(2));
                    userData.put("email", rs.getString(3));
                    userData.put("telp", rs.getString(4));
                    userData.put("created", rs.getDate(5).toString());
                    userData.put("alamat", rs.getString(6));
                    String tierId = rs.getString(7);

                    String query2 = "SELECT nama_tier, benefit FROM Tier_Loyalitas WHERE id_tier = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(query2)) {
                        ps2.setString(1, tierId);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            if (rs2.next()) {
                                userData.put("tierInfo", tierId + " - " + rs2.getString(1));
                                userData.put("benefit", rs2.getString(2));
                            }
                        }
                    }
                }
            }
        }
        return userData;
    }

    public void updatePelanggan(int field, String informasi, String userId) throws SQLException {
        String query;
        switch (field) {
            case 1:
                query = "UPDATE Pelanggan SET id_pelanggan = ? WHERE id_pelanggan = ?";
                break;
            case 2:
                query = "UPDATE Pelanggan SET nama = ? WHERE id_pelanggan = ?";
                break;
            case 3:
                query = "UPDATE Pelanggan SET email = ? WHERE id_pelanggan = ?";
                break;
            case 4:
                query = "UPDATE Pelanggan SET no_telp = ? WHERE id_pelanggan = ?";
                break;
            case 5:
                query = "UPDATE Pelanggan SET alamat_utama = ? WHERE id_pelanggan = ?";
                break;
            default:
                throw new SQLException("Invalid field index for update");
        }
        try (PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, informasi);
            st.setString(2, userId);
            st.executeUpdate();
        }
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}