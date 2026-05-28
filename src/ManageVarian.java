package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageVarian {

    private Connection conn;

    public ManageVarian(Connection conn){
        this.conn = conn;
    }

    public void initializeComboBox(JComboBox<String> cmbIDProduk){
        loadComboIDProduk(cmbIDProduk);
    }

    public void loadComboIDProduk(JComboBox<String> comboBox){
        comboBox.removeAllItems();
        try{
            String query = "SELECT id_produk FROM Produk ORDER BY id_produk ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                comboBox.addItem(rs.getString("id_produk"));
            }
            rs.close();
            ps.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void loadDataVarian(JTable table){

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID Produk");
        model.addColumn("ID Varian");
        model.addColumn("Ukuran");
        model.addColumn("Warna");
        model.addColumn("Berat");
        model.addColumn("Stok");
        model.addColumn("Harga");
        model.addColumn("Barcode");

        try{
            String query = "SELECT * FROM Varian_Produk";

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString("id_produk"),
                        rs.getString("id_varian"),
                        rs.getString("ukuran"),
                        rs.getString("warna"),
                        rs.getString("berat"),
                        rs.getInt("stok"),
                        rs.getInt("harga"),
                        rs.getString("barcode")
                });
            }

            table.setModel(model);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void insertVarian(JTable tabel, String idProduk, String idVarian, String ukuran, String warna, int berat, int stok, int harga, String barcode) {
        try{
            String query = "INSERT INTO Varian_Produk VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idProduk);
            ps.setString(2, idVarian);
            ps.setString(3, ukuran);
            ps.setString(4, warna);
            ps.setInt(5, berat);
            ps.setInt(6, stok);
            ps.setInt(7, harga);
            ps.setString(8, barcode);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Varian berhasil ditambahkan");

            loadDataVarian(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void updateVarian(JTable tabel, String idProduk, String idVarian, String ukuran, String warna, int berat, int stok, int harga, String barcode) {
        try{
            String query = "UPDATE Varian_Produk SET ukuran = ?, warna = ?, berat = ?, stok = ?, harga = ?, barcode = ? WHERE id_produk = ? AND id_varian = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, ukuran);
            ps.setString(2, warna);
            ps.setInt(3, berat);
            ps.setInt(4, stok);
            ps.setInt(5, harga);
            ps.setString(6, barcode);
            ps.setString(7, idProduk);
            ps.setString(8, idVarian);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Varian berhasil diupdate");

            loadDataVarian(tabel);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void deleteVarian(JTable tabel){
        try{
            int row = tabel.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tabel.getModel();

            String idProduk = model.getValueAt(row, 0).toString();
            String idVarian = model.getValueAt(row, 1).toString();

            String query = "DELETE FROM Varian_Produk WHERE id_produk = ? AND id_varian = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idProduk);
            ps.setString(2, idVarian);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Varian berhasil dihapus");

            loadDataVarian(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}