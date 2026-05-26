import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class App extends JFrame {
    // User
    String loggedinUserID;
    String loggedinuserNama;

    // Komponen
    private JPanel MainPanel;
    private JPanel Front;
    private JPanel Back;
    private JButton registAkunButton;
    private JPanel RegistP;
    private JTextField namaRegist;
    private JTextField emailRegist;
    private JTextField telpRegist;
    private JTextField alamatRegist;
    private JTextField IDRegist;
    private JButton backButton;
    private JButton daftarButton;

    // --- Main POS UI Components ---
    private JTable productTable;
    private JTextArea subtotalTextArea;
    private JButton calculateButton;
    private JButton checkoutButton;
    private JLabel userInfoNameLabel;
    private JLabel userInfoTierLabel;
    private ProductTableModel productTableModel;


    // Card Layout
    private CardLayout c1;

    // Database Handler
    private DatabaseHandler dbHandler;

    // Constrcutor
    public App(){
        createUIComponents();
        dbHandler = new DatabaseHandler();

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
                    loggedIn = true; // Success, exit loop
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                dbHandler.closeConnection();
                System.exit(1);
                return;
            }
        }

        // --- Main Window Setup (runs only after successful login) ---
        setContentPane(MainPanel);
        setSize(1280, 720);
        setTitle("Aplikasi Pengurus Database - Selamat Datang, " + loggedinuserNama);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        c1 = (CardLayout) MainPanel.getLayout();
        c1.show(MainPanel, "Front"); 

        registAkunButton.addActionListener((e) -> c1.show(MainPanel, "regist"));
        backButton.addActionListener((e) -> c1.show(MainPanel, "Front")); // Back from registration goes to main
        daftarButton.addActionListener((e) -> registPembeli());

        calculateButton.addActionListener(e -> calculateSubtotal());

        setVisible(true);

        // Refresh data once on startup
        refreshDataPengguna();

        // Close the database connection when the window is closed
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
        Back = new JPanel();
        RegistP = new JPanel(new GridLayout(0, 2, 5, 5));

        registAkunButton = new JButton("Registrasi Akun Baru");
        namaRegist = new JTextField();
        emailRegist = new JTextField();
        telpRegist = new JTextField();
        alamatRegist = new JTextField();
        IDRegist = new JTextField();
        backButton = new JButton("Back");
        daftarButton = new JButton("Daftar");

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        userInfoNameLabel = new JLabel("Nama: ");
        userInfoTierLabel = new JLabel("Tier: ");
        topPanel.add(userInfoNameLabel);
        topPanel.add(userInfoTierLabel);
        topPanel.add(registAkunButton);
        Front.add(topPanel, BorderLayout.NORTH);

        Object[][] placeholderData = {
                {"P001-M", "Kemeja Flanel", "M", 150000.0, 0},
                {"P001-L", "Kemeja Flanel", "L", 150000.0, 0},
                {"P001-XL", "Kemeja Flanel", "XL", 150000.0, 0},
                {"P002-30", "Celana Jeans", "30", 250000.0, 0},
                {"P002-32", "Celana Jeans", "32", 250000.0, 0},
                {"P003-AS", "Topi", "All Size", 75000.0, 0}
        };
        productTableModel = new ProductTableModel(placeholderData);
        productTable = new JTable(productTableModel);
        productTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        Front.add(tableScrollPane, BorderLayout.CENTER);

        // --- BOTTOM PANEL: Subtotal and Checkout ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        subtotalTextArea = new JTextArea(3, 20);
        subtotalTextArea.setEditable(false);
        subtotalTextArea.setText("Subtotal: Rp 0.00");
        calculateButton = new JButton("Hitung Subtotal");
        checkoutButton = new JButton("Checkout");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(calculateButton);
        buttonPanel.add(checkoutButton);

        bottomPanel.add(new JScrollPane(subtotalTextArea), BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        Front.add(bottomPanel, BorderLayout.SOUTH);


        // --- Registration Page Layout (RegistP) ---
        RegistP.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        RegistP.add(new JLabel("ID:"));
        RegistP.add(IDRegist);
        RegistP.add(new JLabel("Nama:"));
        RegistP.add(namaRegist);
        RegistP.add(new JLabel("Email:"));
        RegistP.add(emailRegist);
        RegistP.add(new JLabel("No. Telepon:"));
        RegistP.add(telpRegist);
        RegistP.add(new JLabel("Alamat:"));
        RegistP.add(alamatRegist);
        RegistP.add(backButton);
        RegistP.add(daftarButton);

        // Add all panels (cards) to the main panel with CardLayout
        MainPanel.add(Front, "Front");
        MainPanel.add(RegistP, "regist");
    }

    private void calculateSubtotal() {
        double subtotal = 0;
        
        if (productTable.isEditing()) {
            productTable.getCellEditor().stopCellEditing();
        }
        for (int i = 0; i < productTableModel.getRowCount(); i++) {
            double price = (double) productTableModel.getValueAt(i, 3);
            int quantity = 0;
            try {
                quantity = Integer.parseInt(productTableModel.getValueAt(i, 4).toString());
            } catch (NumberFormatException e) {
                quantity = 0; 
            }
            subtotal += price * quantity;
        }
        subtotalTextArea.setText(String.format("Subtotal: Rp %,.2f", subtotal));
    }

    private void gantiInformasiAkun(int e){
    }

    private void refreshDataPengguna(){
        try{
            Map<String, String> userData = dbHandler.getPelangganData(loggedinUserID);
            if (userData != null && !userData.isEmpty()) {
                userInfoNameLabel.setText("Nama: " + userData.get("nama"));
                userInfoTierLabel.setText("Tier: " + userData.get("tierInfo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void registPembeli(){
        String id = IDRegist.getText().trim();
        String nama = namaRegist.getText().trim();
        String email = emailRegist.getText().trim();
        String telp = telpRegist.getText().trim();
        String alamat = alamatRegist.getText().trim();

        if(id.isEmpty() || nama.isEmpty() || email.isEmpty() || telp.isEmpty() || alamat.isEmpty()){
            JOptionPane.showMessageDialog(this, "Data tidak boleh kosong");
            return;
        }

        try{
            dbHandler.registerPembeli(id, nama, email, telp, alamat);
            JOptionPane.showMessageDialog(this, "Akun berhasil ditambahkan!");
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    // This method is no longer needed as login is handled by a dialog in the constructor
    private void loginFrontend(){}

    public static void main(String[] args){
        new App();
    }

    // Custom TableModel for the Product Table
    class ProductTableModel extends AbstractTableModel {
        private final String[] columnNames = {"ID Varian", "Nama Produk", "Varian", "Harga", "Jumlah"};
        private Object[][] data;

        public ProductTableModel(Object[][] data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int c) {
            if (c == 3) return Double.class; 
            if (c == 4) return Integer.class; 
            return String.class; 
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Only the "Jumlah" column (index 4) is editable
            return columnIndex == 4;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            data[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}