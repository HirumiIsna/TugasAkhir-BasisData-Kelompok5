package src.backend.database;

import java.sql.*;
import java.util.Map;

public class PaymentDAO {
    private Connection conn;

    public PaymentDAO() {
        this.conn = DatabaseConnection.getConnection();
    }

    public PaymentDAO(Connection conn) {
        this.conn = conn;
    }

    // READ payment by transaksi ID (check all payment types)
    public Map<String, Object> getPaymentByTransaksiId(String idTransaksi) {
        TransferBankDAO transferDAO = new TransferBankDAO(conn);
        KreditDAO kreditDAO = new KreditDAO(conn);
        DompetDigitalDAO dompetDAO = new DompetDigitalDAO(conn);

        Map<String, Object> transfer = transferDAO.getById(idTransaksi);
        if (transfer != null) {
            transfer.put("type", "TRANSFER_BANK");
            return transfer;
        }

        Map<String, Object> kredit = kreditDAO.getById(idTransaksi);
        if (kredit != null) {
            kredit.put("type", "KREDIT");
            return kredit;
        }

        Map<String, Object> dompet = dompetDAO.getById(idTransaksi);
        if (dompet != null) {
            dompet.put("type", "DOMPET_DIGITAL");
            return dompet;
        }

        return null;
    }
}