package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageProduk {

    private Connection conn;

    public ManageProduk(Connection conn){
        this.conn = conn;
    }

    public void initializeComboBox(JComboBox<String> cmbMerk, JComboBox<String> cmbKategori, JComboBox<String> cmbPemasok){
        loadComboMerk(cmbMerk);
        loadComboKategori(cmbKategori);
        loadComboPemasok(cmbPemasok);
    }

    public void loadComboMerk(JComboBox<String> comboBox){

        comboBox.removeAllItems();

        try{

            String query = "SELECT nama FROM Merk";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                comboBox.addItem(rs.getString("nama"));

            }

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void loadComboKategori(JComboBox<String> comboBox){

        comboBox.removeAllItems();

        try{

            String query = "SELECT nama_kategori FROM Kategori";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                comboBox.addItem(rs.getString("nama_kategori"));

            }

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void loadComboPemasok(JComboBox<String> comboBox){

        comboBox.removeAllItems();

        try{

            String query = "SELECT nama FROM Pemasok";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                comboBox.addItem(rs.getString("nama"));

            }

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // LOAD DATA
    public void loadDataProduk(JTable table){

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID Produk");
        model.addColumn("Nama Produk");
        model.addColumn("Deskripsi");
        model.addColumn("Merk");
        model.addColumn("Kategori");
        model.addColumn("Pemasok");

        try{

            String query =
                    "SELECT p.id_produk, p.nama, p.deskripsi, " +
                            "m.nama AS merk, " +
                            "STRING_AGG(k.nama_kategori, ', ') AS kategori, " +
                            "s.nama AS pemasok " +
                            "FROM Produk p " +
                            "JOIN Merk m ON p.id_merk = m.id_merk " +
                            "JOIN Pemasok s ON p.id_pemasok = s.id_pemasok " +
                            "JOIN Produk_Mempunyai_Kategori pk ON p.id_produk = pk.id_produk " +
                            "JOIN Kategori k ON pk.id_kategori = k.id_kategori " +
                            "GROUP BY p.id_produk, p.nama, p.deskripsi, m.nama, s.nama " +
                            "ORDER BY p.id_produk ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getString("id_produk"),
                        rs.getString("nama"),
                        rs.getString("deskripsi"),
                        rs.getString("merk"),
                        rs.getString("kategori"),
                        rs.getString("pemasok")
                });

            }

            table.setModel(model);

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // INSERT
    public void insertProduk(
            String idProduk,
            String status,
            String nama,
            String deskripsi,
            String idMerk,
            String idPemasok,
            String idKategori
    ){

        try{

            String queryProduk =
                    "INSERT INTO Produk VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps1 = conn.prepareStatement(queryProduk);

            ps1.setString(1, idProduk);
            ps1.setString(2, status);
            ps1.setString(3, nama);
            ps1.setString(4, deskripsi);
            ps1.setString(5, idMerk);
            ps1.setString(6, idPemasok);

            ps1.executeUpdate();

            String queryKategori =
                    "INSERT INTO Produk_Mempunyai_Kategori VALUES (?, ?)";

            PreparedStatement ps2 = conn.prepareStatement(queryKategori);

            ps2.setString(1, idKategori);
            ps2.setString(2, idProduk);

            ps2.executeUpdate();

            ps1.close();
            ps2.close();

            JOptionPane.showMessageDialog(null, "Produk berhasil ditambahkan");

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // UPDATE
    public void updateProduk(
            String idProduk,
            String nama,
            String deskripsi,
            String idMerk,
            String idPemasok,
            String idKategori
    ){

        try{

            String queryProduk =
                    "UPDATE Produk " +
                            "SET nama = ?, deskripsi = ?, id_merk = ?, id_pemasok = ? " +
                            "WHERE id_produk = ?";

            PreparedStatement ps1 = conn.prepareStatement(queryProduk);

            ps1.setString(1, nama);
            ps1.setString(2, deskripsi);
            ps1.setString(3, idMerk);
            ps1.setString(4, idPemasok);
            ps1.setString(5, idProduk);

            ps1.executeUpdate();

            String queryKategori =
                    "UPDATE Produk_Mempunyai_Kategori " +
                            "SET id_kategori = ? " +
                            "WHERE id_produk = ?";

            PreparedStatement ps2 = conn.prepareStatement(queryKategori);

            ps2.setString(1, idKategori);
            ps2.setString(2, idProduk);

            ps2.executeUpdate();

            ps1.close();
            ps2.close();

            JOptionPane.showMessageDialog(null, "Produk berhasil diupdate");

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    // DELETE
    public void deleteProduk(String idProduk){

        try{

            String q1 =
                    "DELETE FROM Produk_Mempunyai_Kategori " +
                            "WHERE id_produk = ?";

            PreparedStatement ps1 = conn.prepareStatement(q1);

            ps1.setString(1, idProduk);
            ps1.executeUpdate();

            String q2 =
                    "DELETE FROM Detail_Transaksi " +
                            "WHERE id_produk = ?";

            PreparedStatement ps2 = conn.prepareStatement(q2);

            ps2.setString(1, idProduk);
            ps2.executeUpdate();

            String q3 =
                    "DELETE FROM Varian_Produk " +
                            "WHERE id_produk = ?";

            PreparedStatement ps3 = conn.prepareStatement(q3);

            ps3.setString(1, idProduk);
            ps3.executeUpdate();

            String q4 =
                    "DELETE FROM Produk " +
                            "WHERE id_produk = ?";

            PreparedStatement ps4 = conn.prepareStatement(q4);

            ps4.setString(1, idProduk);
            ps4.executeUpdate();

            ps1.close();
            ps2.close();
            ps3.close();
            ps4.close();

            JOptionPane.showMessageDialog(null, "Produk berhasil dihapus");

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}