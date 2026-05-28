package src;

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

    public String[] getEkspedisi() throws SQLException {
        String query = "SELECT nama FROM Ekspedisi";
        List<String> ekspedisiList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                ekspedisiList.add(rs.getString(1));
            }
        }
        return ekspedisiList.toArray(new String[0]);
    }


    public double hargaAfterVoucher(double harga, String kode) throws SQLException {
        if (kode == null || kode.isEmpty()) {
            return harga;
        }

        String idQuery = "SELECT id_voucher, min_belanja FROM voucher WHERE kode = ?";
        String id_voucher = null;
        float minBelanja = 0;

        try (PreparedStatement ps = conn.prepareStatement(idQuery)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id_voucher = rs.getString(1);
                    minBelanja = rs.getFloat(2);
                } else {
                    // No voucher found with this code
                    return harga;
                }
            }
        }

        if (harga < minBelanja) {
            return harga;
        }

        boolean isDiskon = false;
        float persenDiskon=0;
        double maxDiskon = 0;

        boolean isOngkir = false;
        double persenOngkir=0;
        double maxOngkir = 0;
        
        boolean isPotongan = false;
        double potonganValue = 0;

        String checkDiskonQuery = "SELECT * FROM diskon WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkDiskonQuery)) {
            ps.setString(1, id_voucher);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isDiskon = true;
                    persenDiskon = rs.getFloat(2);
                    maxDiskon = rs.getDouble(3);

                }
            }
        }

        String checkOngkirQuery = "SELECT * FROM ongkir WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkOngkirQuery)) {
            ps.setString(1, id_voucher);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isOngkir = true;
                    persenOngkir = rs.getFloat(2);
                    maxOngkir = rs.getDouble(3);
                }
            }
        }

        String checkPotonganQuery = "SELECT * FROM potongan WHERE id_voucher = ?";
        try (PreparedStatement ps = conn.prepareStatement(checkPotonganQuery)) {
            ps.setString(1, id_voucher);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isPotongan = true;
                    potonganValue = rs.getFloat(2);
                }
            }
        }

        if (isDiskon) {
            if (harga*persenDiskon/100 > maxDiskon) {
                return harga - maxDiskon;
            } else {
                return harga - (harga * persenDiskon / 100);
            }
        } else if (isOngkir) {
            if (harga*persenOngkir/100 > maxOngkir) {
                return harga - maxOngkir;
            } else {
                return harga - (harga * persenOngkir / 100);
            }
        } else if (isPotongan) {
            return harga - potonganValue;
        }

        return harga;
    }

    public String[] getBank() throws SQLException {
        String query = "SELECT nama_bank FROM Transfer_bank";
        List<String> bankList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                bankList.add(rs.getString(1));
            }
        }
        return bankList.toArray(new String[0]);
    }

    public String[] getEmoney() throws SQLException {
        String query = "SELECT jenis_dompet FROM Dompet_digital";
        List<String> emoneyList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                emoneyList.add(rs.getString(1));
            }
        }
        return emoneyList.toArray(new String[0]);
    }
    public String[] getKredit() throws SQLException {
        String query = "SELECT nama_bank FROM kredit";
        List<String> kreditList = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(query)) {
            while (rs.next()) {
                kreditList.add(rs.getString(1));
            }
        }
        return kreditList.toArray(new String[0]);
    }

    public void createTransaction(String loggedinUserID, List<App.CartItem> cartItems, String jenisPengiriman, String ekspedisi, String alamat, String voucher, String metodePembayaran, Map<String, String> paymentDetails) throws SQLException {
        String newTransactionId = null;
        String newShipmentId = null;
        String newPaymentId = null;
        int originalIsolationLevel = conn.getTransactionIsolation();

        try {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // 1. Generate new Transaksi ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(id_transaksi, 2) AS UNSIGNED)) FROM transaksi")) {
                int maxId = 0;
                if (rs.next()) {
                    maxId = rs.getInt(1);
                }
                newTransactionId = "T" + String.format("%02d", maxId + 1);
            }

            // 2. Handle Pengiriman
            if (jenisPengiriman.equals("Delivery")) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(id_pengiriman, 2) AS UNSIGNED)) FROM pengiriman")) {
                    int maxId = 0;
                    if (rs.next()) {
                        maxId = rs.getInt(1);
                    }
                    newShipmentId = "N" + String.format("%02d", maxId + 1);
                }

                String insertPengirimanQuery = "INSERT INTO pengiriman (id_pengiriman, id_ekspedisi, alamat_pengiriman, status, tanggal_pengiriman) VALUES (?, ?, ?, 'Diproses', CURDATE())";
                try (PreparedStatement ps = conn.prepareStatement(insertPengirimanQuery)) {
                    ps.setString(1, newShipmentId);
                    ps.setString(2, ekspedisi); // Assuming ekspedisi is the ID
                    ps.setString(3, alamat);
                    ps.executeUpdate();
                }
            }

            // 3. Generate new Pembayaran ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(id_pembayaran, 2) AS UNSIGNED)) FROM pembayaran")) {
                int maxId = 0;
                if (rs.next()) {
                    maxId = rs.getInt(1);
                }
                newPaymentId = "P" + String.format("%02d", maxId + 1);
            }

            // 4. Insert into Pembayaran
            String insertPembayaranQuery = "INSERT INTO pembayaran (id_pembayaran, metode_pembayaran, tanggal_pembayaran, status) VALUES (?, ?, CURDATE(), 'Berhasil')";
            try (PreparedStatement ps = conn.prepareStatement(insertPembayaranQuery)) {
                ps.setString(1, newPaymentId);
                ps.setString(2, metodePembayaran);
                ps.executeUpdate();
            }

            // 5. Insert into specific payment method table
            switch (metodePembayaran) {
                case "Transfer Bank":
                    String insertTransferQuery = "INSERT INTO transfer_bank (id_pembayaran, nama_bank, no_rekening) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertTransferQuery)) {
                        ps.setString(1, newPaymentId);
                        ps.setString(2, paymentDetails.get("nama_bank"));
                        ps.setString(3, paymentDetails.get("no_rek"));
                        ps.executeUpdate();
                    }
                    break;
                case "E-Money":
                    String insertEmoneyQuery = "INSERT INTO dompet_digital (id_pembayaran, jenis_dompet, no_telp) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertEmoneyQuery)) {
                        ps.setString(1, newPaymentId);
                        ps.setString(2, paymentDetails.get("jenis_dompet"));
                        ps.setString(3, paymentDetails.get("no_telp"));
                        ps.executeUpdate();
                    }
                    break;
                case "Kredit":
                    String insertKreditQuery = "INSERT INTO kredit (id_pembayaran, nama_bank, masa_berlaku, no_kartu) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertKreditQuery)) {
                        ps.setString(1, newPaymentId);
                        ps.setString(2, paymentDetails.get("nama_bank"));
                        ps.setDate(3, java.sql.Date.valueOf(paymentDetails.get("masa_berlaku")));
                        ps.setString(4, paymentDetails.get("no_kartu"));
                        ps.executeUpdate();
                    }
                    break;
            }

            // 6. Insert into Transaksi
            String insertTransaksiQuery = "INSERT INTO transaksi (id_transaksi, id_pelanggan, id_pengiriman, id_pembayaran, id_voucher, tanggal_transaksi) VALUES (?, ?, ?, ?, ?, CURDATE())";
            try (PreparedStatement ps = conn.prepareStatement(insertTransaksiQuery)) {
                ps.setString(1, newTransactionId);
                ps.setString(2, loggedinUserID);
                ps.setString(3, newShipmentId); // Can be null
                ps.setString(4, newPaymentId);
                ps.setString(5, voucher); // Assuming voucher is the ID
                ps.executeUpdate();
            }

            // 7. Insert into transaksi_memuat_varian and update stock
            String insertJunctionQuery = "INSERT INTO transaksi_memuat_varian (id_transaksi, id_varian, jumlah) VALUES (?, ?, ?)";
            String updateStockQuery = "UPDATE varian_produk SET stok = stok - ? WHERE id_varian = ?";
            try (PreparedStatement psJunction = conn.prepareStatement(insertJunctionQuery);
                 PreparedStatement psUpdateStock = conn.prepareStatement(updateStockQuery)) {

                for (App.CartItem item : cartItems) {
                    psJunction.setString(1, newTransactionId);
                    psJunction.setString(2, item.getIdVarian());
                    psJunction.setInt(3, item.getAmount());
                    psJunction.addBatch();

                    psUpdateStock.setInt(1, item.getAmount());
                    psUpdateStock.setString(2, item.getIdVarian());
                    psUpdateStock.addBatch();
                }
                psJunction.executeBatch();
                psUpdateStock.executeBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.setTransactionIsolation(originalIsolationLevel); // Reset to default
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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