import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    // User
    String loggedinUserID;
    String loggedinuserNama;

    // Komponen
    private JPanel MainPanel;
    private JPanel Front;

    private JTable productTable;
    private JTextArea subtotalTextArea;
    private JButton calculateButton;
    private JButton checkoutButton;
    private JLabel userInfoNameLabel;
    private JLabel userInfoEmailLabel;
    private JLabel userInfoTelpLabel;
    private JLabel userInfoTierLabel;
    private DefaultTableModel productTableModel;
    private JComboBox<String> kategoriComboBox;

    private CardLayout c1;

    private DatabaseHandler dbHandler;

    public App(){
        dbHandler = new DatabaseHandler();
        createUIComponents();

        boolean loggedIn = false;
        while (!loggedIn) {
            String data = JOptionPane.showInputDialog(null, "Masukkan ID Pelanggan:", "Login", JOptionPane.PLAIN_MESSAGE);
            if (data == null) {
                dbHandler.closeConnection();
                System.exit(0);
                return; 
            }
            if (data.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                continue; 
            }

            try {
                String[] userData = dbHandler.login(data.trim());
                if (userData == null) {
                    JOptionPane.showMessageDialog(null, "Data Pelanggan tidak Ditemukan!", "Login Gagal", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    loggedinUserID = userData[0];
                    loggedinuserNama = userData[1];
                    loggedIn = true; 
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                dbHandler.closeConnection();
                System.exit(1);
                return;
            }
        }

        setContentPane(MainPanel);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database - Selamat Datang, " + loggedinuserNama);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        c1 = (CardLayout) MainPanel.getLayout();
        c1.show(MainPanel, "Front"); 

        calculateButton.addActionListener(e -> calculateSubtotal());
        kategoriComboBox.addActionListener(e -> filterByCategory());

        setVisible(true);

        refreshDataPengguna();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dbHandler.closeConnection();
            }
        });
    }

    private void createUIComponents() {
        MainPanel = new JPanel(new CardLayout());
        Front = new JPanel(new BorderLayout(10, 10));
        Front.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Initialize components that don't depend on others first
        userInfoNameLabel = new JLabel("Nama: ");
        userInfoEmailLabel = new JLabel("Email: ");
        userInfoTelpLabel = new JLabel("Telp: ");
        userInfoTierLabel = new JLabel("Tier: ");
        subtotalTextArea = new JTextArea(3, 20);
        subtotalTextArea.setEditable(false);
        subtotalTextArea.setText("Subtotal: Rp 0.00");
        calculateButton = new JButton("Hitung Subtotal");
        checkoutButton = new JButton("Checkout");

        try {
            kategoriComboBox = new JComboBox<>(dbHandler.getKategori());
            kategoriComboBox.insertItemAt("Show all", 0);
            kategoriComboBox.setSelectedIndex(0);
        } catch (SQLException e) {
            e.printStackTrace();
            kategoriComboBox = new JComboBox<>();
            JOptionPane.showMessageDialog(this, "Gagal memuat kategori: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(userInfoNameLabel);
        topPanel.add(userInfoEmailLabel);
        topPanel.add(userInfoTelpLabel);
        topPanel.add(userInfoTierLabel);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(topPanel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Filter Kategori:"));
        filterPanel.add(kategoriComboBox);
        topContainer.add(filterPanel, BorderLayout.EAST);

        Front.add(topContainer, BorderLayout.NORTH);

        // 4. Set up the table model and table
        productTableModel = new DefaultTableModel(new String[]{"Nama", "Ukuran", "Warna", "Kategori", "Merk", "Stok", "Harga", "Checkout"}, 0){
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 7;
            }
        };

        productTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 6) { // This should probably be 7 for the checkout column
                    Object data = productTableModel.getValueAt(row, column);
                    System.out.println("Checkout value for '" + productTableModel.getValueAt(row, 0) + "' changed to: " + data);
                }
            }
        });

        productTable = new JTable(productTableModel);
        productTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        Front.add(tableScrollPane, BorderLayout.CENTER);

        // 5. Populate the table
        try {
            getCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat katalog: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(calculateButton);
        buttonPanel.add(checkoutButton);
        bottomPanel.add(new JScrollPane(subtotalTextArea), BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        Front.add(bottomPanel, BorderLayout.SOUTH);


        MainPanel.add(Front, "Front");
    }

    private void calculateSubtotal() {
        double subtotal = 0;
        if (productTable.isEditing()) {
            productTable.getCellEditor().stopCellEditing();
        }

        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            Object priceObj = productTableModel.getValueAt(i, 6);
            Object quantityObj = productTableModel.getValueAt(i, 7);
            if(quantityObj == null){
                quantityObj = "0";
            }
            Object stockObj = productTableModel.getValueAt(i, 5);

            if (priceObj != null) {
                try {
                    double price = Double.parseDouble(priceObj.toString());
                    int quantity;
                    if(Integer.parseInt(quantityObj.toString()) > Integer.parseInt(stockObj.toString())){
                        JOptionPane.showMessageDialog(this, "Jumlah checkout melebihi stok", "Error", JOptionPane.ERROR_MESSAGE);
                        productTableModel.setValueAt(stockObj, i, 7);
                        quantity = Integer.parseInt(stockObj.toString());
                    }else{
                        quantity = Integer.parseInt(quantityObj.toString());
                    }

                    if (quantity > 0) {
                        subtotal += price * quantity;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in row " + i + ": " + e.getMessage());
                }
            }
        }
        // Update the text area with the formatted subtotal
        subtotalTextArea.setText(String.format("Subtotal: Rp %,.2f", subtotal));
    }

    private void gantiInformasiAkun(int e){
    }

    private void refreshDataPengguna(){
        try{
            Map<String, String> userData = dbHandler.getPelangganData(loggedinUserID);
            if (userData != null && !userData.isEmpty()) {
                userInfoNameLabel.setText("Nama: " + userData.get("nama"));
                userInfoEmailLabel.setText("Email: " + userData.get("email"));
                userInfoTelpLabel.setText("Telp: " + userData.get("telp"));
                userInfoTierLabel.setText("Tier: " + userData.get("tierInfo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void getCatalog() throws SQLException {
        productTableModel.setRowCount(0);
        String query = "select p.nama, vp.ukuran, vp.warna, k.nama_kategori, mk.nama, vp.stok, vp.harga from varian_produk vp "+
                        "join produk p on vp.id_produk = p.id_produk "+
                        "join produk_mempunyai_kategori pmk on p.id_produk = pmk.id_produk "+
                        "join kategori k on pmk.id_kategori = k.id_kategori "+
                        "join merk mk on p.id_merk = mk.id_merk";
        try(ResultSet rs = dbHandler.conn.createStatement().executeQuery(query)){
            while(rs.next()){
                productTableModel.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), rs.getDouble(7), 0});
            }
        }
    }

    public void filterByCategory(){
        String selectedCategory = (String) kategoriComboBox.getSelectedItem();

        if ("Show all".equals(selectedCategory)) {
            try {
                getCatalog();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memuat katalog: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        productTableModel.setRowCount(0);
        String query = "select p.nama, vp.ukuran, vp.warna, k.nama_kategori, mk.nama, vp.stok, vp.harga from varian_produk vp "+
                        "join produk p on vp.id_produk = p.id_produk "+
                        "join produk_mempunyai_kategori pmk on p.id_produk = pmk.id_produk "+
                        "join kategori k on pmk.id_kategori = k.id_kategori "+
                        "join merk mk on p.id_merk = mk.id_merk "+
                        "where k.nama_kategori = ?";
        try(java.sql.PreparedStatement ps = dbHandler.conn.prepareStatement(query)){
            ps.setString(1, selectedCategory);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    productTableModel.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), rs.getDouble(7), 0});
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memfilter produk: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public static void main(String[] args){
        new App();
    }
        
    }