package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManagePemasok {

    private Connection conn;

    public ManagePemasok(Connection conn){
        this.conn = conn;
    }

    // LOAD
    public void loadPemasok(JTable table){

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
                        rs.getString("id_pemasok"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("no_telp"),
                        rs.getString("alamat")
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
    public void insertPemasok(
            String id,
            String nama,
            String email,
            String telp,
            String alamat
    ){

        try{

            String query =
                    "INSERT INTO Pemasok VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setString(3, email);
            ps.setString(4, telp);
            ps.setString(5, alamat);

            ps.executeUpdate();

            ps.close();

            JOptionPane.showMessageDialog(null, "Pemasok berhasil ditambahkan");

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    // UPDATE
    public void updatePemasok(
            String id,
            String nama,
            String email,
            String telp,
            String alamat
    ){

        try{

            String query =
                    "UPDATE Pemasok " +
                            "SET nama = ?, email = ?, no_telp = ?, alamat = ? " +
                            "WHERE id_pemasok = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, nama);
            ps.setString(2, email);
            ps.setString(3, telp);
            ps.setString(4, alamat);
            ps.setString(5, id);

            ps.executeUpdate();

            ps.close();

            JOptionPane.showMessageDialog(null, "Pemasok berhasil diupdate");

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    // DELETE
    public void deletePemasok(String id){

        try{

            String query =
                    "DELETE FROM Pemasok WHERE id_pemasok = ?";

            PreparedStatement ps = conn.prepareStatement(query);

            ps.setString(1, id);

            ps.executeUpdate();

            ps.close();

            JOptionPane.showMessageDialog(null, "Pemasok berhasil dihapus");

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

}