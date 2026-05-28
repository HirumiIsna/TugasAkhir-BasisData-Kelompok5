package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageMerk {

    private Connection conn;

    public ManageMerk(Connection conn){
        this.conn = conn;
    }

    public void loadDataMerk(JTable table){

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID Merk");
        model.addColumn("Nama");
        model.addColumn("Deskripsi");

        try{
            String query = "SELECT * FROM Merk";

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

    public void insertMerk(JTable tabel, String idMerk, String namaMerk, String deskripsiMerk) {
        try{
            String query = "INSERT INTO Merk VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idMerk);
            ps.setString(2, namaMerk);
            ps.setString(3, deskripsiMerk);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Merk berhasil ditambahkan");

            loadDataMerk(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void updateMerk(JTable tabel, String idMerk, String namaMerk, String deskripsi) {
        try{
            String query = "UPDATE Kategori SET nama = ?, deskripsi = ? WHERE id_merk = ? ";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, namaMerk);
            ps.setString(2, deskripsi);
            ps.setString(3, idMerk);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Merk berhasil diupdate");

            loadDataMerk(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void deleteMerk(JTable tabel) {
        try {
            int row = tabel.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tabel.getModel();

            String idMerk = model.getValueAt(row, 0).toString();

            String query = "DELETE FROM Merk WHERE id_merk = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idMerk);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Merk berhasil dihapus");

            loadDataMerk(tabel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}