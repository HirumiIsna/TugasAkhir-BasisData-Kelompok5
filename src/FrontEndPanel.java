package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class FrontEndPanel extends JPanel {
    private DatabaseHelper dbHelper;
    private String loggedinUserID;
    private String loggedinUserName;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Komponen Katalog
    private JTable produkTable;
    private DefaultTableModel produkTableModel;
    private JComboBox<String> kategoriFilter;
    private JTextField searchField;
    private List<Map<String, Object>> currentProdukList;

    // Komponen Keranjang
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private List<Map<String, Object>> cartItems;
    private JLabel totalHargaLabel;

    // Komponen Checkout
    private JRadioButton deliveryRadio, collectRadio;
    private JComboBox<String> ekspedisiCombo;
    private JTextField alamatField, alamatGeraiField;
    private JTextField voucherField;
    private JLabel diskonLabel;
    private int currentTotalHarga;
    private int currentTotalBerat;
    private String currentVoucherId;
    private int currentPotongan;

    // Komponen Pembayaran
    private JRadioButton transferRadio, kreditRadio, dompetRadio;
    private JPanel paymentDetailPanel;

    public FrontEndPanel(DatabaseHelper dbHelper, String userID, String userName) {
        this.dbHelper = dbHelper;
        this.loggedinUserID = userID;
        this.loggedinUserName = userName;
        this.cartItems = new java.util.ArrayList<>();

        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Matahari Online Store - " + userName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main content with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        mainPanel.add(createKatalogPanel(), "katalog");
        mainPanel.add(createCartPanel(), "cart");
        mainPanel.add(createCheckoutPanel(), "checkout");
        mainPanel.add(createPaymentPanel(), "payment");
        mainPanel.add(createOrderHistoryPanel(), "history");
        mainPanel.add(createTrackingPanel(), "tracking");

        add(mainPanel, BorderLayout.CENTER);

        // Bottom navigation
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(240, 240, 240));

        JButton katalogBtn = new JButton("Katalog");
        JButton cartBtn = new JButton("Keranjang");
        JButton historyBtn = new JButton("Riwayat Pesanan");
        JButton trackingBtn = new JButton("Lacak Pesanan");

        katalogBtn.addActionListener(e -> cardLayout.show(mainPanel, "katalog"));
        cartBtn.addActionListener(e -> {
            refreshCartTable();
            cardLayout.show(mainPanel, "cart");
        });
        historyBtn.addActionListener(e -> {
            refreshOrderHistory();
            cardLayout.show(mainPanel, "history");
        });
        trackingBtn.addActionListener(e -> cardLayout.show(mainPanel, "tracking"));

        navPanel.add(katalogBtn);
        navPanel.add(cartBtn);
        navPanel.add(historyBtn);
        navPanel.add(trackingBtn);
        add(navPanel, BorderLayout.SOUTH);

        cardLayout.show(mainPanel, "katalog");
    }

    /**
     * Method logout - kembali ke halaman utama dengan aman
     * Menghindari ClassCastException
     */
    private void logout() {
        // Dapatkan parent frame dari panel ini
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        if (frame != null) {
            // Tutup window saat ini
            frame.dispose();

            // Buat instance App baru di Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                new App();
            });
        } else {
            // Alternatif: cari parent frame melalui hirarki container
            Container parent = getParent();
            while (parent != null && !(parent instanceof JFrame)) {
                parent = parent.getParent();
            }

            if (parent instanceof JFrame) {
                ((JFrame) parent).dispose();
                SwingUtilities.invokeLater(() -> new App());
            } else {
                System.err.println("Cannot find parent frame");
            }
        }
    }

    private JPanel createKatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Kategori:"));
        kategoriFilter = new JComboBox<>();
        kategoriFilter.addItem("Semua");

        // Load categories
        List<Map<String, String>> kategoriList = dbHelper.getAllKategori();
        for (Map<String, String> kat : kategoriList) {
            kategoriFilter.addItem(kat.get("nama_kategori"));
        }

        kategoriFilter.addActionListener(e -> refreshProdukTable());

        filterPanel.add(new JLabel("Cari:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> refreshProdukTable());
        filterPanel.add(searchField);

        JButton searchBtn = new JButton("Cari");
        searchBtn.addActionListener(e -> refreshProdukTable());
        filterPanel.add(searchBtn);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Product table
        String[] columns = {"ID Produk", "Nama", "Merk", "Varian", "Ukuran", "Warna", "Harga", "Stok"};
        produkTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        produkTable = new JTable(produkTableModel);
        JScrollPane scrollPane = new JScrollPane(produkTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add to cart panel
        JPanel addPanel = new JPanel(new FlowLayout());
        JLabel qtyLabel = new JLabel("Jumlah:");
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JButton addToCartBtn = new JButton("Tambah ke Keranjang");

        addToCartBtn.addActionListener(e -> {
            int selectedRow = produkTable.getSelectedRow();
            if (selectedRow >= 0) {
                int qty = (Integer) qtySpinner.getValue();
                Map<String, Object> produk = currentProdukList.get(selectedRow);

                // Check stock
                int stok = (int) produk.get("stok");
                if (qty > stok) {
                    JOptionPane.showMessageDialog(this, "Stok tidak mencukupi! Stok tersedia: " + stok);
                    return;
                }

                // Add to cart
                Map<String, Object> cartItem = new java.util.HashMap<>();
                cartItem.put("id_produk", produk.get("id_produk"));
                cartItem.put("id_varian", produk.get("id_varian"));
                cartItem.put("nama", produk.get("nama"));
                cartItem.put("merk", produk.get("merk"));
                cartItem.put("ukuran", produk.get("ukuran"));
                cartItem.put("warna", produk.get("warna"));
                cartItem.put("harga", produk.get("harga"));
                cartItem.put("berat", produk.get("berat"));
                cartItem.put("jumlah", qty);

                cartItems.add(cartItem);
                JOptionPane.showMessageDialog(this, "Produk ditambahkan ke keranjang!");
            } else {
                JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!");
            }
        });

        addPanel.add(qtyLabel);
        addPanel.add(qtySpinner);
        addPanel.add(addToCartBtn);
        panel.add(addPanel, BorderLayout.SOUTH);

        refreshProdukTable();

        return panel;
    }

    private void refreshProdukTable() {
        produkTableModel.setRowCount(0);

        String selectedKategori = (String) kategoriFilter.getSelectedItem();
        String searchText = searchField.getText().toLowerCase();

        if (selectedKategori != null && !selectedKategori.equals("Semua")) {
            // Get kategori ID
            String kategoriId = null;
            List<Map<String, String>> kategoriList = dbHelper.getAllKategori();
            for (Map<String, String> kat : kategoriList) {
                if (kat.get("nama_kategori").equals(selectedKategori)) {
                    kategoriId = kat.get("id_kategori");
                    break;
                }
            }
            if (kategoriId != null) {
                currentProdukList = dbHelper.getProdukByKategori(kategoriId);
            } else {
                currentProdukList = dbHelper.getAllProduk();
            }
        } else {
            currentProdukList = dbHelper.getAllProduk();
        }

        // Filter by search text
        if (!searchText.isEmpty()) {
            currentProdukList.removeIf(p -> !((String)p.get("nama")).toLowerCase().contains(searchText) &&
                    !((String)p.get("merk")).toLowerCase().contains(searchText));
        }

        for (Map<String, Object> produk : currentProdukList) {
            produkTableModel.addRow(new Object[]{
                    produk.get("id_produk"),
                    produk.get("nama"),
                    produk.get("merk"),
                    produk.get("id_varian"),
                    produk.get("ukuran"),
                    produk.get("warna"),
                    "Rp " + String.format("%,d", produk.get("harga")),
                    produk.get("stok")
            });
        }
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Produk", "Merk", "Ukuran", "Warna", "Harga", "Jumlah", "Subtotal"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());

        totalHargaLabel = new JLabel("Total: Rp 0");
        totalHargaLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton removeBtn = new JButton("Hapus Item");
        JButton clearBtn = new JButton("Kosongkan Keranjang");
        JButton checkoutBtn = new JButton("Checkout");

        removeBtn.addActionListener(e -> {
            int selectedRow = cartTable.getSelectedRow();
            if (selectedRow >= 0) {
                cartItems.remove(selectedRow);
                refreshCartTable();
            }
        });

        clearBtn.addActionListener(e -> {
            cartItems.clear();
            refreshCartTable();
        });

        checkoutBtn.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Keranjang kosong!");
                return;
            }
            cardLayout.show(mainPanel, "checkout");
            prepareCheckout();
        });

        buttonPanel.add(removeBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(checkoutBtn);

        bottomPanel.add(totalHargaLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        int total = 0;

        for (Map<String, Object> item : cartItems) {
            int harga = (int) item.get("harga");
            int jumlah = (int) item.get("jumlah");
            int subtotal = harga * jumlah;
            total += subtotal;

            cartTableModel.addRow(new Object[]{
                    item.get("nama"),
                    item.get("merk"),
                    item.get("ukuran"),
                    item.get("warna"),
                    "Rp " + String.format("%,d", harga),
                    jumlah,
                    "Rp " + String.format("%,d", subtotal)
            });
        }

        totalHargaLabel.setText("Total: Rp " + String.format("%,d", total));
    }

    private void prepareCheckout() {
        // Calculate total
        currentTotalHarga = 0;
        currentTotalBerat = 0;
        for (Map<String, Object> item : cartItems) {
            currentTotalHarga += (int) item.get("harga") * (int) item.get("jumlah");
            currentTotalBerat += (int) item.get("berat") * (int) item.get("jumlah");
        }
    }

    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Order summary
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Ringkasan Pesanan"));
        JTextArea summaryArea = new JTextArea(10, 40);
        summaryArea.setEditable(false);

        // Fill summary
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : cartItems) {
            sb.append(item.get("nama")).append(" x ").append(item.get("jumlah"))
                    .append(" = Rp ").append(String.format("%,d", (int)item.get("harga") * (int)item.get("jumlah")))
                    .append("\n");
        }
        sb.append("\nTotal: Rp ").append(String.format("%,d", currentTotalHarga));
        summaryArea.setText(sb.toString());

        summaryPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(summaryPanel, gbc);
        row++;

        // Shipping method
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Metode Pengiriman:"), gbc);

        JPanel shippingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deliveryRadio = new JRadioButton("Click & Deliver (Dikirim ke Alamat)");
        collectRadio = new JRadioButton("Click & Collect (Ambil di Gerai)");
        ButtonGroup shippingGroup = new ButtonGroup();
        shippingGroup.add(deliveryRadio);
        shippingGroup.add(collectRadio);
        deliveryRadio.setSelected(true);

        shippingPanel.add(deliveryRadio);
        shippingPanel.add(collectRadio);

        gbc.gridx = 1;
        formPanel.add(shippingPanel, gbc);
        row++;

        // Address field (for delivery)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Alamat Pengiriman:"), gbc);
        alamatField = new JTextField(30);
        gbc.gridx = 1;
        formPanel.add(alamatField, gbc);
        row++;

        // Store address field (for collect)
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Alamat Gerai:"), gbc);
        alamatGeraiField = new JTextField(30);
        gbc.gridx = 1;
        formPanel.add(alamatGeraiField, gbc);
        row++;

        // Ekspedisi
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Ekspedisi:"), gbc);
        ekspedisiCombo = new JComboBox<>();
        List<Map<String, String>> ekspedisiList = dbHelper.getAllEkspedisi();
        for (Map<String, String> eks : ekspedisiList) {
            ekspedisiCombo.addItem(eks.get("nama"));
        }
        gbc.gridx = 1;
        formPanel.add(ekspedisiCombo, gbc);
        row++;

        // Voucher
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Kode Voucher:"), gbc);
        voucherField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(voucherField, gbc);

        JButton applyVoucherBtn = new JButton("Apply");
        gbc.gridx = 2;
        formPanel.add(applyVoucherBtn, gbc);
        row++;

        // Diskon info
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Diskon:"), gbc);
        diskonLabel = new JLabel("Rp 0");
        gbc.gridx = 1;
        formPanel.add(diskonLabel, gbc);
        row++;

        // Total after discount
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Total Setelah Diskon:"), gbc);
        JLabel totalAfterDiscountLabel = new JLabel("Rp " + String.format("%,d", currentTotalHarga));
        gbc.gridx = 1;
        formPanel.add(totalAfterDiscountLabel, gbc);

        applyVoucherBtn.addActionListener(e -> {
            String kode = voucherField.getText().trim();
            if (kode.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Masukkan kode voucher!");
                return;
            }

            Map<String, Object> voucher = dbHelper.getVoucherByCode(kode);
            if (voucher == null) {
                JOptionPane.showMessageDialog(panel, "Voucher tidak valid atau sudah kadaluarsa!");
                return;
            }

            int minBelanja = (int) voucher.get("min_belanja");
            if (currentTotalHarga < minBelanja) {
                JOptionPane.showMessageDialog(panel, "Minimal belanja Rp " + String.format("%,d", minBelanja) +
                        " untuk menggunakan voucher ini!");
                return;
            }

            currentVoucherId = (String) voucher.get("id_voucher");
            String type = (String) voucher.get("type");

            if (type.equals("DISKON")) {
                int persen = (int) voucher.get("persen_diskon");
                int maks = (int) voucher.get("maks_diskon");
                currentPotongan = Math.min(currentTotalHarga * persen / 100, maks);
                diskonLabel.setText("Rp " + String.format("%,d", currentPotongan) + " (" + persen + "%)");
            } else if (type.equals("POTONGAN")) {
                currentPotongan = (int) voucher.get("nominal");
                diskonLabel.setText("Rp " + String.format("%,d", currentPotongan));
            } else if (type.equals("ONGKIR")) {
                currentPotongan = 0;
                diskonLabel.setText("Gratis Ongkir");
            }

            int finalTotal = currentTotalHarga - currentPotongan;
            totalAfterDiscountLabel.setText("Rp " + String.format("%,d", finalTotal));
        });

        JButton proceedPaymentBtn = new JButton("Lanjut ke Pembayaran");
        proceedPaymentBtn.addActionListener(e -> {
            // Validate form
            if (deliveryRadio.isSelected() && alamatField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Masukkan alamat pengiriman!");
                return;
            }
            if (collectRadio.isSelected() && alamatGeraiField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Masukkan alamat gerai!");
                return;
            }

            cardLayout.show(mainPanel, "payment");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(proceedPaymentBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Update form visibility based on shipping method
        deliveryRadio.addActionListener(e -> {
            alamatField.setEnabled(true);
            alamatGeraiField.setEnabled(false);
            ekspedisiCombo.setEnabled(true);
        });

        collectRadio.addActionListener(e -> {
            alamatField.setEnabled(false);
            alamatGeraiField.setEnabled(true);
            ekspedisiCombo.setEnabled(false);
        });

        // Initial state
        alamatGeraiField.setEnabled(false);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Payment methods
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Metode Pembayaran:"), gbc);

        JPanel paymentMethodPanel = new JPanel(new GridLayout(3, 1));
        transferRadio = new JRadioButton("Transfer Bank");
        kreditRadio = new JRadioButton("Kartu Kredit");
        dompetRadio = new JRadioButton("Dompet Digital");
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(transferRadio);
        paymentGroup.add(kreditRadio);
        paymentGroup.add(dompetRadio);
        transferRadio.setSelected(true);

        paymentMethodPanel.add(transferRadio);
        paymentMethodPanel.add(kreditRadio);
        paymentMethodPanel.add(dompetRadio);

        gbc.gridx = 1;
        formPanel.add(paymentMethodPanel, gbc);
        row++;

        // Payment detail panel
        paymentDetailPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(paymentDetailPanel, gbc);

        updatePaymentDetailPanel();

        transferRadio.addActionListener(e -> updatePaymentDetailPanel());
        kreditRadio.addActionListener(e -> updatePaymentDetailPanel());
        dompetRadio.addActionListener(e -> updatePaymentDetailPanel());

        JButton confirmPaymentBtn = new JButton("Konfirmasi Pembayaran & Selesai");
        confirmPaymentBtn.addActionListener(e -> processPayment());

        JButton backBtn = new JButton("Kembali ke Checkout");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "checkout"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backBtn);
        buttonPanel.add(confirmPaymentBtn);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updatePaymentDetailPanel() {
        paymentDetailPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        if (transferRadio.isSelected()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Nama Bank:"), gbc);
            JTextField bankField = new JTextField(20);
            gbc.gridx = 1;
            paymentDetailPanel.add(bankField, gbc);
            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Nomor Rekening:"), gbc);
            JTextField rekField = new JTextField(20);
            gbc.gridx = 1;
            paymentDetailPanel.add(rekField, gbc);

            paymentDetailPanel.putClientProperty("bankField", bankField);
            paymentDetailPanel.putClientProperty("rekField", rekField);

        } else if (kreditRadio.isSelected()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Nama Bank:"), gbc);
            JTextField bankField = new JTextField(20);
            gbc.gridx = 1;
            paymentDetailPanel.add(bankField, gbc);
            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Nomor Kartu:"), gbc);
            JTextField kartuField = new JTextField(20);
            gbc.gridx = 1;
            paymentDetailPanel.add(kartuField, gbc);
            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Masa Berlaku (MM/YY):"), gbc);
            JTextField masaField = new JTextField(20);
            gbc.gridx = 1;
            paymentDetailPanel.add(masaField, gbc);

            paymentDetailPanel.putClientProperty("bankField", bankField);
            paymentDetailPanel.putClientProperty("kartuField", kartuField);
            paymentDetailPanel.putClientProperty("masaField", masaField);

        } else if (dompetRadio.isSelected()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Jenis Dompet:"), gbc);
            JComboBox<String> dompetCombo = new JComboBox<>(new String[]{"Dana", "GoPay", "ShopeePay", "LinkAja", "OVO"});
            gbc.gridx = 1;
            paymentDetailPanel.add(dompetCombo, gbc);
            row++;

            gbc.gridx = 0;
            gbc.gridy = row;
            paymentDetailPanel.add(new JLabel("Nomor Telepon:"), gbc);
            JTextField telpField = new JTextField(20);
            gbc.gridx = 1;
            paymentDetailPanel.add(telpField, gbc);

            paymentDetailPanel.putClientProperty("dompetCombo", dompetCombo);
            paymentDetailPanel.putClientProperty("telpField", telpField);
        }

        paymentDetailPanel.revalidate();
        paymentDetailPanel.repaint();
    }

    private void processPayment() {
        try {
            // Create pengiriman
            String idPengiriman = dbHelper.createPengiriman("Barang belum diambil");

            if (deliveryRadio.isSelected()) {
                String alamat = alamatField.getText().trim();
                String ekspedisiNama = (String) ekspedisiCombo.getSelectedItem();

                // Get ekspedisi ID
                String ekspedisiId = null;
                List<Map<String, String>> ekspedisiList = dbHelper.getAllEkspedisi();
                for (Map<String, String> eks : ekspedisiList) {
                    if (eks.get("nama").equals(ekspedisiNama)) {
                        ekspedisiId = eks.get("id_ekspedisi");
                        break;
                    }
                }

                dbHelper.createClickAndDeliver(idPengiriman, alamat, ekspedisiId);
            } else {
                String alamatGerai = alamatGeraiField.getText().trim();
                dbHelper.createClickAndCollect(idPengiriman, alamatGerai);
            }

            // Calculate final total
            int finalTotal = currentTotalHarga - currentPotongan;

            // Create transaksi
            String idTransaksi = dbHelper.createTransaksi(loggedinUserID, finalTotal, currentTotalBerat,
                    currentPotongan, idPengiriman, currentVoucherId);

            if (idTransaksi == null) {
                JOptionPane.showMessageDialog(this, "Gagal membuat transaksi!");
                return;
            }

            // Add detail transaksi
            for (Map<String, Object> item : cartItems) {
                String idProduk = (String) item.get("id_produk");
                String idVarian = (String) item.get("id_varian");
                int jumlah = (int) item.get("jumlah");
                dbHelper.addDetailTransaksi(idTransaksi, idProduk, idVarian, jumlah);
            }

            // Process payment based on selected method
            boolean paymentSuccess = false;

            if (transferRadio.isSelected()) {
                JTextField bankField = (JTextField) paymentDetailPanel.getClientProperty("bankField");
                JTextField rekField = (JTextField) paymentDetailPanel.getClientProperty("rekField");
                if (bankField != null && rekField != null) {
                    paymentSuccess = dbHelper.createPaymentTransfer(idTransaksi, bankField.getText(), rekField.getText());
                }
            } else if (kreditRadio.isSelected()) {
                JTextField bankField = (JTextField) paymentDetailPanel.getClientProperty("bankField");
                JTextField kartuField = (JTextField) paymentDetailPanel.getClientProperty("kartuField");
                JTextField masaField = (JTextField) paymentDetailPanel.getClientProperty("masaField");
                if (bankField != null && kartuField != null && masaField != null) {
                    paymentSuccess = dbHelper.createPaymentKredit(idTransaksi, bankField.getText(), masaField.getText(), kartuField.getText());
                }
            } else if (dompetRadio.isSelected()) {
                JComboBox<String> dompetCombo = (JComboBox<String>) paymentDetailPanel.getClientProperty("dompetCombo");
                JTextField telpField = (JTextField) paymentDetailPanel.getClientProperty("telpField");
                if (dompetCombo != null && telpField != null) {
                    paymentSuccess = dbHelper.createPaymentDompetDigital(idTransaksi, (String) dompetCombo.getSelectedItem(), telpField.getText());
                }
            }

            if (paymentSuccess) {
                // Update transaksi status to Berhasil
                dbHelper.updateStatusTransaksi(idTransaksi, "Berhasil");

                // Clear cart
                cartItems.clear();

                JOptionPane.showMessageDialog(this, "Pesanan berhasil dibuat!\nID Transaksi: " + idTransaksi);
                cardLayout.show(mainPanel, "history");
                refreshOrderHistory();
            } else {
                JOptionPane.showMessageDialog(this, "Pembayaran gagal!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID Transaksi", "Tanggal", "Total Harga", "Status"};
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.putClientProperty("historyModel", historyModel);

        return panel;
    }

    private void refreshOrderHistory() {
        JPanel panel = (JPanel) mainPanel.getComponent(4);
        DefaultTableModel historyModel = (DefaultTableModel) panel.getClientProperty("historyModel");

        if (historyModel != null) {
            historyModel.setRowCount(0);
            List<Map<String, Object>> transaksiList = dbHelper.getTransaksiByPelanggan(loggedinUserID);

            for (Map<String, Object> trans : transaksiList) {
                historyModel.addRow(new Object[]{
                        trans.get("id_transaksi"),
                        trans.get("tanggal"),
                        "Rp " + String.format("%,d", trans.get("total_harga")),
                        trans.get("status")
                });
            }
        }
    }

    private JPanel createTrackingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("ID Transaksi:"));
        JTextField trackIdField = new JTextField(20);
        inputPanel.add(trackIdField);

        JButton trackBtn = new JButton("Lacak");
        inputPanel.add(trackBtn);

        panel.add(inputPanel, BorderLayout.NORTH);

        JTextArea resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        trackBtn.addActionListener(e -> {
            String idTransaksi = trackIdField.getText().trim();
            if (idTransaksi.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Masukkan ID Transaksi!");
                return;
            }

            Map<String, Object> tracking = dbHelper.trackPengiriman(idTransaksi);
            if (tracking.isEmpty()) {
                resultArea.setText("Data pengiriman tidak ditemukan!");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("=== STATUS PENGIRIMAN ===\n\n");
                sb.append("Status Pengiriman: ").append(tracking.get("status_pengiriman")).append("\n");

                if (tracking.get("alamat") != null) {
                    sb.append("\n--- CLICK & DELIVER ---\n");
                    sb.append("Alamat: ").append(tracking.get("alamat")).append("\n");
                    sb.append("No Resi: ").append(tracking.get("no_resi")).append("\n");
                    sb.append("Ekspedisi: ").append(tracking.get("ekspedisi")).append("\n");
                    sb.append("Estimasi: ").append(tracking.get("estimasi")).append(" hari\n");
                }

                if (tracking.get("alamat_gerai") != null) {
                    sb.append("\n--- CLICK & COLLECT ---\n");
                    sb.append("Alamat Gerai: ").append(tracking.get("alamat_gerai")).append("\n");
                    sb.append("Kode Pengambilan: ").append(tracking.get("kode_pengambilan")).append("\n");
                    sb.append("Status Pengambilan: ").append(tracking.get("status_pengembalian")).append("\n");
                }

                resultArea.setText(sb.toString());
            }
        });

        return panel;
    }
}