package com.valinx.cloudatlas.service;

import com.valinx.cloudatlas.database.DatabaseManager;
import com.valinx.cloudatlas.model.MonetaryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EconomyService {
    private static final Logger logger = LoggerFactory.getLogger(EconomyService.class);

    /**
     * Get user balance (with implicit creation if not exists)
     */
    public double getBalance(long uid, String currency) {
        String sql = "SELECT value FROM CA_monetary WHERE uid = ? AND currency = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, uid);
            stmt.setString(2, currency);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("value");
            } else {
                // Initialize if not exists
                initializeAccount(uid, currency);
                return 0.0;
            }
        } catch (SQLException e) {
            logger.error("Failed to get balance for user {}", uid, e);
            return 0.0;
        }
    }

    private void initializeAccount(long uid, String currency) throws SQLException {
        String sql = "INSERT INTO CA_monetary (uid, currency, value) VALUES (?, ?, 0) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, uid);
            stmt.setString(2, currency);
            stmt.executeUpdate();
        }
    }

    /**
     * Transaction: Gain currency
     */
    public void gain(long uid, double amount, String currency) {
        updateBalance(uid, amount, currency);
    }

    /**
     * Transaction: Cost currency (returns false if insufficient)
     */
    public boolean cost(long uid, double amount, String currency) {
        double current = getBalance(uid, currency);
        if (current >= amount) {
            updateBalance(uid, -amount, currency);
            return true;
        }
        return false;
    }

    private void updateBalance(long uid, double delta, String currency) {
        String sql = "INSERT INTO CA_monetary (uid, currency, value) VALUES (?, ?, ?) " +
                "ON CONFLICT (uid, currency) DO UPDATE SET value = CA_monetary.value + ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, uid);
            stmt.setString(2, currency);
            stmt.setDouble(3, delta); // For insert
            stmt.setDouble(4, delta); // For update

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to update balance for user {}", uid, e);
        }
    }
}
