package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageMerk {

    private Connection conn;

    public ManageMerk(Connection conn){
        this.conn = conn;
    }

    public void loadMerk(JTable table){

        DefaultTableModel model =
                new DefaultTableModel();

        model.addColumn("ID Merk");
        model.addColumn("Nama");
        model.addColumn("Deskripsi");

        try{

            String query =
                    "SELECT * FROM Merk";

            PreparedStatement ps =
                    conn.prepareStatement(query);

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
            JOptionPane.showMessageDialog(null,
                    e.getMessage());
        }
    }
}