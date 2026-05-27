package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class BackEndPanel extends JPanel {
    private DatabaseHelper dbHelper;
    private JTabbedPane tabbedPane;

    // Tables
    private DefaultTableModel produkTableModel;
    private DefaultTableModel transaksiTableModel;
    private DefaultTableModel pelangganTableModel;
    private DefaultTableModel voucherTableModel;
    private DefaultTableModel ekspedisiTableModel;

    public BackEndPanel(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard - Matahari Store");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        // PERBAIKAN: Gunakan SwingUtilities untuk mendapatkan parent frame
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Manajemen Produk", createProdukPanel());
        tabbedPane.addTab("Manajemen Voucher", createVoucherPanel());
        tabbedPane.addTab("Manajemen Ekspedisi", createEkspedisiPanel());
        tabbedPane.addTab("Manajemen Transaksi", createTransaksiPanel());
        tabbedPane.addTab("Manajemen Pelanggan", createPelangganPanel());
        tabbedPane.addTab("Laporan Penjualan", createLaporanPanel());

        add(tabbedPane, BorderLayout.CENTER);
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

    private JPanel createProdukPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Tambah Produk");
        JButton editBtn = new JButton("Edit Produk");
        JButton deleteBtn = new JButton("Hapus Produk");
        JButton refreshBtn = new JButton("Refresh");

        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID Produk", "Nama", "Deskripsi", "Status", "Merk", "ID Pemasok"};
        produkTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(produkTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshProdukTable();

        // Actions
        refreshBtn.addActionListener(e -> refreshProdukTable());

        addBtn.addActionListener(e -> showAddProdukDialog());

        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String idProduk = (String) produkTableModel.getValueAt(selectedRow, 0);
                showEditProdukDialog(idProduk);
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih produk yang akan diedit!");
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Yakin ingin menghapus produk ini?", "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String idProduk = (String) produkTableModel.getValueAt(selectedRow, 0);
                    if (dbHelper.deleteProduk(idProduk)) {
                        JOptionPane.showMessageDialog(panel, "Produk berhasil dihapus!");
                        refreshProdukTable();
                    } else {
                        JOptionPane.showMessageDialog(panel, "Gagal menghapus produk!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih produk yang akan dihapus!");
            }
        });

        return panel;
    }

    private void refreshProdukTable() {
        produkTableModel.setRowCount(0);
        String query = "SELECT p.id_produk, p.nama, p.deskripsi, p.status, m.nama as merk, p.id_pemasok " +
                "FROM Produk p JOIN Merk m ON p.id_merk = m.id_merk";

        try (java.sql.Statement stmt = dbHelper.getConn().createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                produkTableModel.addRow(new Object[]{
                        rs.getString("id_produk"),
                        rs.getString("nama"),
                        rs.getString("deskripsi"),
                        rs.getString("status"),
                        rs.getString("merk"),
                        rs.getString("id_pemasok")
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddProdukDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Produk", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID Produk:"), gbc);
        JTextField idField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Nama:"), gbc);
        JTextField namaField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(namaField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Deskripsi:"), gbc);
        JTextArea descArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID Merk:"), gbc);
        JTextField merkField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(merkField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID Pemasok:"), gbc);
        JTextField pemasokField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(pemasokField, gbc);
        row++;

        JButton saveBtn = new JButton("Simpan");
        JButton cancelBtn = new JButton("Batal");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            String desc = descArea.getText().trim();
            String merk = merkField.getText().trim();
            String pemasok = pemasokField.getText().trim();

            if (id.isEmpty() || nama.isEmpty() || merk.isEmpty() || pemasok.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Data tidak boleh kosong!");
                return;
            }

            if (dbHelper.createProduk(id, nama, desc, merk, pemasok)) {
                JOptionPane.showMessageDialog(dialog, "Produk berhasil ditambahkan!");
                refreshProdukTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal menambahkan produk!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditProdukDialog(String idProduk) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Produk", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID Produk:"), gbc);
        JLabel idLabel = new JLabel(idProduk);
        gbc.gridx = 1;
        panel.add(idLabel, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Nama:"), gbc);
        JTextField namaField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(namaField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Deskripsi:"), gbc);
        JTextArea descArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descArea);
        gbc.gridx = 1;
        panel.add(scrollPane, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Status:"), gbc);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tersedia", "Tidak Tersedia"});
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        // Load current data
        String query = "SELECT nama, deskripsi, status FROM Produk WHERE id_produk = ?";
        try (java.sql.PreparedStatement ps = dbHelper.getConn().prepareStatement(query)) {
            ps.setString(1, idProduk);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                namaField.setText(rs.getString("nama"));
                descArea.setText(rs.getString("deskripsi"));
                statusCombo.setSelectedItem(rs.getString("status"));
            }
            rs.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        JButton saveBtn = new JButton("Simpan");
        JButton cancelBtn = new JButton("Batal");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String nama = namaField.getText().trim();
            String desc = descArea.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            if (nama.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Data tidak boleh kosong!");
                return;
            }

            if (dbHelper.updateProduk(idProduk, nama, desc, status)) {
                JOptionPane.showMessageDialog(dialog, "Produk berhasil diupdate!");
                refreshProdukTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal mengupdate produk!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createTransaksiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton updateResiBtn = new JButton("Update No Resi");

        toolbar.add(refreshBtn);
        toolbar.add(updateStatusBtn);
        toolbar.add(updateResiBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID Transaksi", "Tanggal", "Total Harga", "Status", "Pelanggan", "ID Pengiriman"};
        transaksiTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(transaksiTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshTransaksiTable();

        refreshBtn.addActionListener(e -> refreshTransaksiTable());

        updateStatusBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String idTransaksi = (String) transaksiTableModel.getValueAt(selectedRow, 0);
                String currentStatus = (String) transaksiTableModel.getValueAt(selectedRow, 3);

                String[] statuses = {"Pending", "Berhasil", "Gagal"};
                String newStatus = (String) JOptionPane.showInputDialog(panel,
                        "Pilih status baru:", "Update Status",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, currentStatus);

                if (newStatus != null && !newStatus.equals(currentStatus)) {
                    if (dbHelper.updateStatusTransaksi(idTransaksi, newStatus)) {
                        JOptionPane.showMessageDialog(panel, "Status berhasil diupdate!");
                        refreshTransaksiTable();
                    } else {
                        JOptionPane.showMessageDialog(panel, "Gagal mengupdate status!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih transaksi yang akan diupdate!");
            }
        });

        updateResiBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String idPengiriman = (String) transaksiTableModel.getValueAt(selectedRow, 5);
                String newResi = JOptionPane.showInputDialog(panel, "Masukkan nomor resi baru:");

                if (newResi != null && !newResi.trim().isEmpty()) {
                    try {
                        int noResi = Integer.parseInt(newResi.trim());
                        if (dbHelper.updateNoResi(idPengiriman, noResi)) {
                            JOptionPane.showMessageDialog(panel, "Nomor resi berhasil diupdate!");
                            refreshTransaksiTable();
                        } else {
                            JOptionPane.showMessageDialog(panel, "Gagal mengupdate nomor resi!");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel, "Nomor resi harus berupa angka!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih transaksi terlebih dahulu!");
            }
        });

        return panel;
    }

    private void refreshTransaksiTable() {
        transaksiTableModel.setRowCount(0);
        List<Map<String, Object>> transaksiList = dbHelper.getAllTransaksi();

        for (Map<String, Object> trans : transaksiList) {
            transaksiTableModel.addRow(new Object[]{
                    trans.get("id_transaksi"),
                    trans.get("tanggal"),
                    "Rp " + String.format("%,d", trans.get("total_harga")),
                    trans.get("status"),
                    trans.get("pelanggan"),
                    trans.get("id_pengiriman")
            });
        }
    }

    private JPanel createPelangganPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshBtn = new JButton("Refresh");
        panel.add(refreshBtn, BorderLayout.NORTH);

        String[] columns = {"ID Pelanggan", "Nama", "Email", "No Telp", "Tanggal Daftar", "Alamat", "Tier", "Benefit"};
        pelangganTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(pelangganTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshPelangganTable();

        refreshBtn.addActionListener(e -> refreshPelangganTable());

        return panel;
    }

    private void refreshPelangganTable() {
        pelangganTableModel.setRowCount(0);
        List<Map<String, Object>> pelangganList = dbHelper.getAllPelanggan();

        for (Map<String, Object> pel : pelangganList) {
            pelangganTableModel.addRow(new Object[]{
                    pel.get("id_pelanggan"),
                    pel.get("nama"),
                    pel.get("email"),
                    pel.get("no_telp"),
                    pel.get("tgl_daftar"),
                    pel.get("alamat_utama"),
                    pel.get("nama_tier"),
                    pel.get("benefit")
            });
        }
    }

    private JPanel createVoucherPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Tambah Voucher");
        JButton refreshBtn = new JButton("Refresh");

        toolbar.add(addBtn);
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID Voucher", "Kode", "Min Belanja", "Tgl Mulai", "Tgl Berakhir", "Kuota"};
        voucherTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(voucherTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshVoucherTable();

        refreshBtn.addActionListener(e -> refreshVoucherTable());

        addBtn.addActionListener(e -> showAddVoucherDialog());

        return panel;
    }

    private void refreshVoucherTable() {
        voucherTableModel.setRowCount(0);
        String query = "SELECT id_voucher, kode, min_belanja, tgl_mulai, tgl_berakhir, kuota FROM Voucher";

        try (java.sql.Statement stmt = dbHelper.getConn().createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                voucherTableModel.addRow(new Object[]{
                        rs.getString("id_voucher"),
                        rs.getString("kode"),
                        "Rp " + String.format("%,d", rs.getInt("min_belanja")),
                        rs.getDate("tgl_mulai"),
                        rs.getDate("tgl_berakhir"),
                        rs.getInt("kuota")
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddVoucherDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tambah Voucher", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID Voucher:"), gbc);
        JTextField idField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(idField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Kode Voucher:"), gbc);
        JTextField kodeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(kodeField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Tipe Voucher:"), gbc);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Diskon", "Potongan", "Ongkir"});
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Minimal Belanja:"), gbc);
        JTextField minField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(minField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Tanggal Mulai (YYYY-MM-DD):"), gbc);
        JTextField mulaiField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(mulaiField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Tanggal Berakhir (YYYY-MM-DD):"), gbc);
        JTextField berakhirField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(berakhirField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Kuota:"), gbc);
        JTextField kuotaField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(kuotaField, gbc);
        row++;

        // Dynamic fields based on type
        JPanel dynamicPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(dynamicPanel, gbc);

        typeCombo.addActionListener(e -> updateDynamicFields(typeCombo, dynamicPanel));
        updateDynamicFields(typeCombo, dynamicPanel);

        JButton saveBtn = new JButton("Simpan");
        JButton cancelBtn = new JButton("Batal");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String kode = kodeField.getText().trim();
            String minBelanjaStr = minField.getText().trim();
            String tglMulaiStr = mulaiField.getText().trim();
            String tglBerakhirStr = berakhirField.getText().trim();
            String kuotaStr = kuotaField.getText().trim();

            if (id.isEmpty() || kode.isEmpty() || minBelanjaStr.isEmpty() ||
                    tglMulaiStr.isEmpty() || tglBerakhirStr.isEmpty() || kuotaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Data tidak boleh kosong!");
                return;
            }

            try {
                int minBelanja = Integer.parseInt(minBelanjaStr);
                int kuota = Integer.parseInt(kuotaStr);
                Date tglMulai = Date.valueOf(tglMulaiStr);
                Date tglBerakhir = Date.valueOf(tglBerakhirStr);

                String type = (String) typeCombo.getSelectedItem();
                boolean success = false;

                if (type.equals("Diskon")) {
                    String persenStr = ((JTextField) dynamicPanel.getClientProperty("persenField")).getText().trim();
                    String maksStr = ((JTextField) dynamicPanel.getClientProperty("maksField")).getText().trim();
                    int persen = Integer.parseInt(persenStr);
                    int maks = Integer.parseInt(maksStr);
                    success = dbHelper.createVoucherDiskon(id, kode, minBelanja, tglMulai, tglBerakhir, kuota, persen, maks);
                } else if (type.equals("Potongan")) {
                    String nominalStr = ((JTextField) dynamicPanel.getClientProperty("nominalField")).getText().trim();
                    int nominal = Integer.parseInt(nominalStr);
                    success = dbHelper.createVoucherPotongan(id, kode, minBelanja, tglMulai, tglBerakhir, kuota, nominal);
                } else {
                    String persenStr = ((JTextField) dynamicPanel.getClientProperty("persenField")).getText().trim();
                    String maksStr = ((JTextField) dynamicPanel.getClientProperty("maksField")).getText().trim();
                    int persen = Integer.parseInt(persenStr);
                    int maks = Integer.parseInt(maksStr);
                    success = dbHelper.createVoucherOngkir(id, kode, minBelanja, tglMulai, tglBerakhir, kuota, persen, maks);
                }

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Voucher berhasil ditambahkan!");
                    refreshVoucherTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Gagal menambahkan voucher!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateDynamicFields(JComboBox<String> typeCombo, JPanel dynamicPanel) {
        dynamicPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        String type = (String) typeCombo.getSelectedItem();

        if (type.equals("Diskon") || type.equals("Ongkir")) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            dynamicPanel.add(new JLabel("Persen Diskon:"), gbc);
            JTextField persenField = new JTextField(20);
            gbc.gridx = 1;
            dynamicPanel.add(persenField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            dynamicPanel.add(new JLabel("Maks Diskon:"), gbc);
            JTextField maksField = new JTextField(20);
            gbc.gridx = 1;
            dynamicPanel.add(maksField, gbc);

            dynamicPanel.putClientProperty("persenField", persenField);
            dynamicPanel.putClientProperty("maksField", maksField);
        } else if (type.equals("Potongan")) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            dynamicPanel.add(new JLabel("Nominal Potongan:"), gbc);
            JTextField nominalField = new JTextField(20);
            gbc.gridx = 1;
            dynamicPanel.add(nominalField, gbc);

            dynamicPanel.putClientProperty("nominalField", nominalField);
        }

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private JPanel createEkspedisiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton updateStatusBtn = new JButton("Update Status");

        toolbar.add(refreshBtn);
        toolbar.add(updateStatusBtn);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] columns = {"ID Ekspedisi", "Nama", "Kode", "Status"};
        ekspedisiTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(ekspedisiTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshEkspedisiTable();

        refreshBtn.addActionListener(e -> refreshEkspedisiTable());

        updateStatusBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String idEkspedisi = (String) ekspedisiTableModel.getValueAt(selectedRow, 0);
                String currentStatus = (String) ekspedisiTableModel.getValueAt(selectedRow, 3);

                String[] statuses = {"Aktif", "Nonaktif"};
                String newStatus = (String) JOptionPane.showInputDialog(panel,
                        "Pilih status baru:", "Update Status",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, currentStatus);

                if (newStatus != null && !newStatus.equals(currentStatus)) {
                    if (dbHelper.updateEkspedisiStatus(idEkspedisi, newStatus)) {
                        JOptionPane.showMessageDialog(panel, "Status ekspedisi berhasil diupdate!");
                        refreshEkspedisiTable();
                    } else {
                        JOptionPane.showMessageDialog(panel, "Gagal mengupdate status!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Pilih ekspedisi yang akan diupdate!");
            }
        });

        return panel;
    }

    private void refreshEkspedisiTable() {
        ekspedisiTableModel.setRowCount(0);
        List<Map<String, String>> ekspedisiList = dbHelper.getAllEkspedisiForAdmin();

        for (Map<String, String> eks : ekspedisiList) {
            ekspedisiTableModel.addRow(new Object[]{
                    eks.get("id_ekspedisi"),
                    eks.get("nama"),
                    eks.get("kode"),
                    eks.get("status")
            });
        }
    }

    private JPanel createLaporanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshBtn = new JButton("Refresh");
        panel.add(refreshBtn, BorderLayout.NORTH);

        JTextArea reportArea = new JTextArea(20, 50);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("=== LAPORAN PENJUALAN ===\n\n");

            sb.append("TOP 5 PRODUK TERLARIS\n");
            sb.append("=======================\n");
            List<Map<String, Object>> topProduk = dbHelper.getTop5ProdukTerlaris();
            int rank = 1;
            for (Map<String, Object> produk : topProduk) {
                sb.append(rank++).append(". ");
                sb.append(produk.get("produk")).append(" - ");
                sb.append(produk.get("total_terjual")).append(" terjual - ");
                sb.append("Rp ").append(String.format("%,d", produk.get("total_penjualan")));
                sb.append("\n");
            }

            sb.append("\n\nSTATUS TRANSAKSI\n");
            sb.append("================\n");
            String statusQuery = "SELECT status, COUNT(*) as jumlah, SUM(total_harga) as total " +
                    "FROM Transaksi GROUP BY status";
            try (java.sql.Statement stmt = dbHelper.getConn().createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(statusQuery)) {
                while (rs.next()) {
                    sb.append(rs.getString("status")).append(": ");
                    sb.append(rs.getInt("jumlah")).append(" transaksi - ");
                    sb.append("Rp ").append(String.format("%,d", rs.getInt("total")));
                    sb.append("\n");
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }

            sb.append("\n\nJUMLAH PELANGGAN PER TIER\n");
            sb.append("========================\n");
            String tierQuery = "SELECT t.nama_tier, COUNT(p.id_pelanggan) as jumlah " +
                    "FROM Tier_Loyalitas t " +
                    "LEFT JOIN Pelanggan p ON t.id_tier = p.id_tier " +
                    "GROUP BY t.nama_tier";
            try (java.sql.Statement stmt = dbHelper.getConn().createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(tierQuery)) {
                while (rs.next()) {
                    sb.append(rs.getString("nama_tier")).append(": ");
                    sb.append(rs.getInt("jumlah")).append(" pelanggan\n");
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }

            reportArea.setText(sb.toString());
        });

        // Initial load
        refreshBtn.doClick();

        return panel;
    }
}