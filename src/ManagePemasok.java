package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManagePemasok {

    private Connection conn;

    public ManagePemasok(Connection conn){
        this.conn = conn;
    }

    public void loadDataPemasok(JTable table){

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID Pemasok");
        model.addColumn("Nama");
        model.addColumn("Email");
        model.addColumn("No Telp");
        model.addColumn("Alamat");

        try{

            String query = "SELECT * FROM Pemasok";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                model.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                });
            }

            table.setModel(model);

            rs.close();
            ps.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public void insertPemasok(JTable tabel, String idPemasok, String namaPemasok, String emailPemasok, String telpPemasok, String alamatPemasok){
        try{
            String query = "INSERT INTO Pemasok VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idPemasok);
            ps.setString(2, namaPemasok);
            ps.setString(3, emailPemasok);
            ps.setString(4, telpPemasok);
            ps.setString(5, alamatPemasok);

            ps.executeUpdate();

            ps.close();

            JOptionPane.showMessageDialog(null, "Pemasok berhasil ditambahkan");

            loadDataPemasok(tabel);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    public void updatePemasok(JTable tabel, String idPemasok, String namaPemasok, String emailPemasok, String telpPemasok, String alamatPemasok){
        try{
            String query = "UPDATE Pemasok SET nama = ?, email = ?, no_telp = ?, alamat = ? WHERE id_pemasok = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, namaPemasok);
            ps.setString(2, emailPemasok);
            ps.setString(3, telpPemasok);
            ps.setString(4, alamatPemasok);
            ps.setString(5, idPemasok);

            ps.executeUpdate();

            ps.close();

            JOptionPane.showMessageDialog(null, "Pemasok berhasil diupdate");

            loadDataPemasok(tabel);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }
    public void deletePemasok(JTable tabel){
        try{
            int row = tabel.getSelectedRow();

            if(row == -1){
                JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tabel.getModel();

            String idKategori = model.getValueAt(row, 0).toString();

            String query = "DELETE FROM Pemasok WHERE id_pemasok = ?";
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, idKategori);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(null, "Kategori berhasil dihapus");

            loadDataPemasok(tabel);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

}