package src.FrontEnd;

import src.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class CartManager {
    private final App app;
    private ArrayList<Object[]> keranjangItem;
    private DefaultTableModel tb3;

    public CartManager(App app, ArrayList<Object[]> keranjangItem, DefaultTableModel tb3) {
        this.app = app;
        this.keranjangItem = keranjangItem;
        this.tb3 = tb3;
    }

    public void ubahSelectedKeranjang(int selectedRow, int newJumlah) {
        if (selectedRow == -1) return;
        Object[] data = keranjangItem.get(selectedRow);
        data[5] = newJumlah;
        tb3.setValueAt(data[5], selectedRow, 5);
        app.refreshHarga();
    }

    public void deleteSelected(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(app, "Pilih item terlebih dahulu!");
            return;
        }
        keranjangItem.remove(selectedRow);
        app.refreshDataPengguna();
        app.refreshHarga();
    }

    public void deleteAll() {
        keranjangItem.clear();
        app.refreshDataPengguna();
        app.refreshHarga();
    }
}