package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageKategori {

    private Connection conn;

    public ManageKategori(Connection conn){
        this.conn = conn;
    }

    public void loadDataKategori(JTable table){

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID Kategori");
        model.addColumn("Nama");
        model.addColumn("Deskripsi");

        try{
            String query = "SELECT * FROM Kategori";

            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                });
            }

            table.setModel(model);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void insertKategori(JTable tabel, String idKategori, String namaKategori, String deskripsiKategori) {
        try{
            String query = "INSERT INTO Kategori VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idKategori);
            ps.setString(2, namaKategori);
            ps.setString(3, deskripsiKategori);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Kategori berhasil ditambahkan");

            loadDataKategori(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void updateKategori(JTable tabel, String idKategori, String namaKategori, String deskripsi) {
        try{
            String query = "UPDATE Kategori SET nama_kategori = ?, deskripsi = ? WHERE id_kategori = ? ";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, namaKategori);
            ps.setString(2, deskripsi);
            ps.setString(3, idKategori);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Kategori berhasil diupdate");

            loadDataKategori(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void deleteKategori(JTable tabel){
        try{
            int row = tabel.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tabel.getModel();

            String idKategori = model.getValueAt(row, 0).toString();

            String query = "DELETE FROM Kategori WHERE id_kategori = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idKategori);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Kategori berhasil dihapus");

            loadDataKategori(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}