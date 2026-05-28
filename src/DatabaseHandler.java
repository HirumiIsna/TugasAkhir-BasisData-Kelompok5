import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {
    Connection conn;

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
    public String[] getKategori() throws SQLException {
        String query = "SELECT nama_kategori FROM Kategori";
        List<String> kategoriList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                kategoriList.add(rs.getString(1));
            }
        }
        return kategoriList.toArray(new String[0]);
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