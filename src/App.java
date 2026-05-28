package src;

import java.awt.*;
import java.sql.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame {
    // User
    String loggedinUserID;
    String loggedinuserNama;

    // Komponen
    private JTabbedPane tabbedPane;

    private JTable productTable;
    private JTextArea subtotalTextArea;
    private JButton calculateButton;
    private JButton cartButton;
    private JLabel userInfoNameLabel;
    private JLabel userInfoEmailLabel;
    private JLabel userInfoTelpLabel;
    private JLabel userInfoTierLabel;
    private DefaultTableModel productTableModel;
    private JComboBox<String> kategoriComboBox;

    // Komponen Keranjang
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JLabel subtotalCartLabel;
    private JLabel discountLabel;
    private JLabel totalLabel;
    private JLabel ppnLabel;
    private JLabel totalBayarLabel;
    private JLabel tierBonusLabel;
    private JButton checkoutButton;

    // Komponen Checkout
    private JComboBox<String> jenisPengirimanComboBox;
    private JComboBox<String> ekspedisiComboBox;
    private JTextArea alamatTextArea;
    private JTextField voucherField;
    private JLabel subtotalCheckoutLabel;
    private JLabel bonusTierCheckoutLabel;
    private JComboBox<String> metodePembayaranComboBox;
    private JPanel paymentDetailsPanel;
    private CardLayout paymentDetailsLayout;

    // Komponen Transfer Bank
    private JTextField transferBankNamaField;
    private JTextField transferBankNorekField;

    // Komponen E-Money
    private JTextField eMoneyJenisDompetField;
    private JTextField eMoneyNoTelpField;

    // Komponen Kredit
    private JTextField kreditNamaBankField;
    private JTextField kreditMasaBerlakuField;
    private JTextField kreditNoKartuField;

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

        setContentPane(tabbedPane);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database - Selamat Datang, " + loggedinuserNama);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        calculateButton.addActionListener(e -> calculateSubtotal());
        kategoriComboBox.addActionListener(e -> filterByCategory());
        cartButton.addActionListener(e -> addToCart());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) { // Keranjang tab
                calculateCartSubtotal();
            }
        });

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
        tabbedPane = new JTabbedPane();

        JPanel katalogPanel = createKatalogPanel();
        tabbedPane.addTab("Katalog", katalogPanel);

        JPanel keranjangPanel = createKeranjangPanel();
        tabbedPane.addTab("Keranjang", keranjangPanel);
    }

    private JPanel createKeranjangPanel() {
        JPanel keranjangPanel = new JPanel(new BorderLayout(10, 10));
        keranjangPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left Panel: Cart Table
        cartTableModel = new DefaultTableModel(new String[]{"Pilih", "Nama", "Ukuran", "Warna", "Harga", "Jumlah"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == 0 || columnIndex == 5; // Allow editing for "Pilih" and "Jumlah"
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(25);
        JScrollPane cartTableScrollPane = new JScrollPane(cartTable);

        cartTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                if (e.getColumn() == 0 || e.getColumn() == 5) {
                    calculateCartSubtotal();
                }
            }
        });

        // Right Panel: Transaction Details
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Detail Transaksi"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Jenis Pengiriman
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Jenis Pengiriman:"), gbc);
        gbc.gridx = 1;
        jenisPengirimanComboBox = new JComboBox<>(new String[]{"Delivery", "Self service"});
        detailsPanel.add(jenisPengirimanComboBox, gbc);

        jenisPengirimanComboBox.addActionListener(e -> {
            boolean isDelivery = "Delivery".equals(jenisPengirimanComboBox.getSelectedItem());
            alamatTextArea.setEnabled(isDelivery);
            ekspedisiComboBox.setEnabled(isDelivery);
            calculateCartSubtotal();
        });

        // Ekspedisi
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Ekspedisi:"), gbc);
        gbc.gridx = 1;
        try{
            String[] ekspedisiList = dbHandler.getEkspedisi();
            ekspedisiComboBox = new JComboBox<>(ekspedisiList);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ekspedisiComboBox = new JComboBox<>(); 
        }
        detailsPanel.add(ekspedisiComboBox, gbc);
        ekspedisiComboBox.addActionListener(e -> calculateCartSubtotal());

        // Alamat
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        detailsPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        alamatTextArea = new JTextArea(5, 20);
        JScrollPane alamatScrollPane = new JScrollPane(alamatTextArea);
        detailsPanel.add(alamatScrollPane, gbc);

        // Voucher
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        detailsPanel.add(new JLabel("Voucher:"), gbc);
        gbc.gridx = 1;
        voucherField = new JTextField(20);
        detailsPanel.add(voucherField, gbc);
        voucherField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                calculateCartSubtotal();
            }
            public void removeUpdate(DocumentEvent e) {
                calculateCartSubtotal();
            }
            public void changedUpdate(DocumentEvent e) {
                calculateCartSubtotal();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx = 1;
        subtotalCheckoutLabel = new JLabel("Rp 0");
        detailsPanel.add(subtotalCheckoutLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        detailsPanel.add(new JLabel("Bonus Tier:"), gbc);
        gbc.gridx = 1;
        bonusTierCheckoutLabel = new JLabel("-");
        detailsPanel.add(bonusTierCheckoutLabel, gbc);

        // Metode Pembayaran
        gbc.gridx = 0;
        gbc.gridy = 6;
        detailsPanel.add(new JLabel("Metode Pembayaran:"), gbc);
        gbc.gridx = 1;
        metodePembayaranComboBox = new JComboBox<>(new String[]{"Transfer Bank", "E-Money", "Kredit"});
        detailsPanel.add(metodePembayaranComboBox, gbc);

        // Payment Details Panel (CardLayout)
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        paymentDetailsLayout = new CardLayout();
        paymentDetailsPanel = new JPanel(paymentDetailsLayout);

        // Create panels for each payment method
        paymentDetailsPanel.add(createTransferBankPanel(), "Transfer Bank");
        paymentDetailsPanel.add(createEMoneyPanel(), "E-Money");
        paymentDetailsPanel.add(createKreditPanel(), "Kredit");

        detailsPanel.add(paymentDetailsPanel, gbc);

        metodePembayaranComboBox.addActionListener(e -> {
            String selectedMethod = (String) metodePembayaranComboBox.getSelectedItem();
            paymentDetailsLayout.show(paymentDetailsPanel, selectedMethod);
        });
        // Show the default panel
        paymentDetailsLayout.show(paymentDetailsPanel, (String)metodePembayaranComboBox.getSelectedItem());


        // Checkout Button
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        checkoutButton = new JButton("Checkout");
        detailsPanel.add(checkoutButton, gbc);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cartTableScrollPane, detailsPanel);
        splitPane.setDividerLocation(800);
        keranjangPanel.add(splitPane, BorderLayout.CENTER);

        return keranjangPanel;
    }

    private JPanel createTransferBankPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Bank:"), gbc);
        gbc.gridx = 1;
        transferBankNamaField = new JTextField(15);
        panel.add(transferBankNamaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("No. Rekening:"), gbc);
        gbc.gridx = 1;
        transferBankNorekField = new JTextField(15);
        panel.add(transferBankNorekField, gbc);

        return panel;
    }

    private JPanel createEMoneyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Jenis Dompet:"), gbc);
        gbc.gridx = 1;
        eMoneyJenisDompetField = new JTextField(15);
        panel.add(eMoneyJenisDompetField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("No. Telp:"), gbc);
        gbc.gridx = 1;
        eMoneyNoTelpField = new JTextField(15);
        panel.add(eMoneyNoTelpField, gbc);

        return panel;
    }

    private JPanel createKreditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Bank:"), gbc);
        gbc.gridx = 1;
        kreditNamaBankField = new JTextField(15);
        panel.add(kreditNamaBankField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Masa Berlaku (DD-MM-YYYY):"), gbc);
        gbc.gridx = 1;
        kreditMasaBerlakuField = new JTextField(15);
        panel.add(kreditMasaBerlakuField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("No. Kartu:"), gbc);
        gbc.gridx = 1;
        kreditNoKartuField = new JTextField(15);
        panel.add(kreditNoKartuField, gbc);

        return panel;
    }


    private JPanel createKatalogPanel() {
        JPanel frontPanel = new JPanel(new BorderLayout(10, 10));
        frontPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userInfoNameLabel = new JLabel("Nama: ");
        userInfoEmailLabel = new JLabel("Email: ");
        userInfoTelpLabel = new JLabel("Telp: ");
        userInfoTierLabel = new JLabel("Tier: ");
        subtotalTextArea = new JTextArea(3, 20);
        subtotalTextArea.setEditable(false);
        subtotalTextArea.setText("Subtotal: Rp 0.00");
        calculateButton = new JButton("Hitung Subtotal");
        cartButton = new JButton("Cart");

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

        frontPanel.add(topContainer, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new String[]{"Nama", "Ukuran", "Warna", "Kategori", "Merk", "Stok", "Harga", "Cart"}, 0){
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
        frontPanel.add(tableScrollPane, BorderLayout.CENTER);

        try {
            getCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat katalog: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(calculateButton);
        buttonPanel.add(cartButton);
        bottomPanel.add(new JScrollPane(subtotalTextArea), BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        frontPanel.add(bottomPanel, BorderLayout.SOUTH);

        return frontPanel;
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
        subtotalTextArea.setText(String.format("Subtotal: Rp %,.2f", subtotal));
    }

    private void addToCart() {
        cartTableModel.setRowCount(0); 
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            Object quantityObj = productTableModel.getValueAt(i, 7);
            if (quantityObj != null) {
                try {
                    int quantity = Integer.parseInt(quantityObj.toString());
                    if (quantity > 0) {
                        Object[] rowData = new Object[]{
                                true,
                                productTableModel.getValueAt(i, 0), // Nama
                                productTableModel.getValueAt(i, 1), // Ukuran
                                productTableModel.getValueAt(i, 2), // Warna
                                productTableModel.getValueAt(i, 6), // Harga
                                quantity
                        };
                        cartTableModel.addRow(rowData);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        tabbedPane.setSelectedIndex(1);
    }

    private void calculateCartSubtotal() {
        double subtotal = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            boolean isSelected = (boolean) cartTableModel.getValueAt(i, 0);
            if (isSelected) {
                Object priceObj = cartTableModel.getValueAt(i, 4);
                Object quantityObj = cartTableModel.getValueAt(i, 5);
                if (priceObj != null && quantityObj != null) {
                    try {
                        double price = Double.parseDouble(priceObj.toString());
                        int quantity = Integer.parseInt(quantityObj.toString());
                        subtotal += price * quantity;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in cart table row " + i + ": " + e.getMessage());
                    }
                }
            }
        }
        try{
        subtotal = dbHandler.hargaAfterVoucher(subtotal, voucherField.getText());
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        subtotalCheckoutLabel.setText(String.format("Rp %,.2f", subtotal));
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