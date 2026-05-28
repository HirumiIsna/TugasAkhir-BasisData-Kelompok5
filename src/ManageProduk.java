package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageProduk {

    private Connection conn;

    public ManageProduk(Connection conn){
        this.conn = conn;
    }

    public void initializeComboBox(JComboBox<String> cmbMerk, JComboBox<String> cmbKategori,JComboBox<String> cmbPemasok
    ){
        loadComboMerk(cmbMerk);
        loadComboKategori(cmbKategori);
        loadComboPemasok(cmbPemasok);
    }

    public void loadComboMerk(JComboBox<String> comboBox){

        comboBox.removeAllItems();

        try{

            String query =
                    "SELECT nama " +
                            "FROM Merk " +
                            "ORDER BY id_merk ASC";

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

            String query =
                    "SELECT nama_kategori " +
                            "FROM Kategori " +
                            "ORDER BY id_kategori ASC";

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

            String query =
                    "SELECT nama " +
                            "FROM Pemasok " +
                            "ORDER BY id_pemasok ASC";

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
                    "SELECT p.id_produk, " +
                            "p.nama, " +
                            "p.deskripsi, " +
                            "m.nama AS merk, " +
                            "STRING_AGG(k.nama_kategori, ', ') AS kategori, " +
                            "s.nama AS pemasok " +
                            "FROM Produk p " +
                            "JOIN Merk m " +
                            "ON p.id_merk = m.id_merk " +
                            "JOIN Pemasok s " +
                            "ON p.id_pemasok = s.id_pemasok " +
                            "JOIN Produk_Mempunyai_Kategori pk " +
                            "ON p.id_produk = pk.id_produk " +
                            "JOIN Kategori k " +
                            "ON pk.id_kategori = k.id_kategori " +
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

    public String getIdMerk(String namaMerk){

        String idMerk = "";

        try{

            String query =
                    "SELECT id_merk " +
                            "FROM Merk " +
                            "WHERE nama = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, namaMerk);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                idMerk = rs.getString("id_merk");

            }

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return idMerk;
    }

    public String getIdKategori(String namaKategori){

        String idKategori = "";

        try{

            String query =
                    "SELECT id_kategori " +
                            "FROM Kategori " +
                            "WHERE nama_kategori = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, namaKategori);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                idKategori = rs.getString("id_kategori");

            }

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return idKategori;
    }

    public String getIdPemasok(String namaPemasok){

        String idPemasok = "";

        try{

            String query =
                    "SELECT id_pemasok " +
                            "FROM Pemasok " +
                            "WHERE nama = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, namaPemasok);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                idPemasok = rs.getString("id_pemasok");

            }

            rs.close();
            ps.close();

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        return idPemasok;
    }

    public void insertProduk(JTable tabel, String idProduk, String status, String nama, String deskripsi, String namaMerk, String namaPemasok, String namaKategori)
    {
        try{
            String idMerk = getIdMerk(namaMerk);
            String idPemasok = getIdPemasok(namaPemasok);
            String idKategori = getIdKategori(namaKategori);

            String queryProduk =
                    "INSERT INTO Produk " +
                            "(id_produk, status, nama, deskripsi, id_merk, id_pemasok) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps1 = conn.prepareStatement(queryProduk);

            ps1.setString(1, idProduk);
            ps1.setString(2, status);
            ps1.setString(3, nama);
            ps1.setString(4, deskripsi);
            ps1.setString(5, idMerk);
            ps1.setString(6, idPemasok);

            ps1.executeUpdate();

            String queryKategori =
                    "INSERT INTO Produk_Mempunyai_Kategori " +
                            "(id_kategori, id_produk) " +
                            "VALUES (?, ?)";

            PreparedStatement ps2 = conn.prepareStatement(queryKategori);

            ps2.setString(1, idKategori);
            ps2.setString(2, idProduk);

            ps2.executeUpdate();

            ps1.close();
            ps2.close();

            JOptionPane.showMessageDialog(null, "Produk berhasil ditambahkan");
            loadDataProduk(tabel);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void updateProduk(JTable tabel, String idProduk, String nama, String deskripsi, String namaMerk, String namaPemasok, String namaKategori)
    {
        try{
            String idMerk = getIdMerk(namaMerk);
            String idPemasok = getIdPemasok(namaPemasok);
            String idKategori = getIdKategori(namaKategori);

            String queryProduk =
                    "UPDATE Produk " +
                            "SET nama = ?, " +
                            "deskripsi = ?, " +
                            "id_merk = ?, " +
                            "id_pemasok = ? " +
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

            JOptionPane.showMessageDialog(null,"Produk berhasil diupdate");
            loadDataProduk(tabel);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void deleteProduk(JTable tabel){
        try{
            int row = tabel.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tabel.getModel();

            String idProduk = model.getValueAt(row, 0).toString();

            String q1 = "DELETE FROM Produk_Mempunyai_Kategori WHERE id_produk = ?";
            PreparedStatement ps1 = conn.prepareStatement(q1);
            ps1.setString(1, idProduk);
            ps1.executeUpdate();
            ps1.close();

            String q2 = "DELETE FROM Detail_Transaksi WHERE id_produk = ?";
            PreparedStatement ps2 = conn.prepareStatement(q2);
            ps2.setString(1, idProduk);
            ps2.executeUpdate();
            ps2.close();

            String q3 = "DELETE FROM Varian_Produk WHERE id_produk = ?";
            PreparedStatement ps3 = conn.prepareStatement(q3);
            ps3.setString(1, idProduk);
            ps3.executeUpdate();
            ps3.close();

            String q4 = "DELETE FROM Produk WHERE id_produk = ?";
            PreparedStatement ps4 = conn.prepareStatement(q4);
            ps4.setString(1, idProduk);
            ps4.executeUpdate();
            ps4.close();
            JOptionPane.showMessageDialog(null,"Produk berhasil dihapus");
            loadDataProduk(tabel);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }
}