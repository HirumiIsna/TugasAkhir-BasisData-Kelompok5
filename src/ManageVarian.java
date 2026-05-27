package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ManageVarian {

    private Connection conn;

    public ManageVarian(Connection conn){
        this.conn = conn;
    }

    public void loadDataVarian(JTable table){

        DefaultTableModel model =
                new DefaultTableModel();

        model.addColumn("ID Produk");
        model.addColumn("ID Varian");
        model.addColumn("Ukuran");
        model.addColumn("Warna");
        model.addColumn("Stok");
        model.addColumn("Harga");

        try{

            String query =
                    "SELECT * FROM Varian_Produk";

            PreparedStatement ps =
                    conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getString("id_produk"),
                        rs.getString("id_varian"),
                        rs.getString("ukuran"),
                        rs.getString("warna"),
                        rs.getInt("stok"),
                        rs.getInt("harga")
                });
            }

            table.setModel(model);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null,
                    e.getMessage());
        }
    }

    public void insertVarian(
            String idProduk,
            String idVarian,
            String ukuran,
            String warna,
            int berat,
            int stok,
            int harga,
            String barcode
    ){

        try{

            String query =
                    "INSERT INTO Varian_Produk " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps =
                    conn.prepareStatement(query);

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

        } catch (Exception e){
            JOptionPane.showMessageDialog(null,
                    e.getMessage());
        }
    }
}