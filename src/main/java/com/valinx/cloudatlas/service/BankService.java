package com.valinx.cloudatlas.service;

import com.valinx.cloudatlas.database.DatabaseManager;
import com.valinx.cloudatlas.model.BankRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class BankService {
    private static final Logger logger = LoggerFactory.getLogger(BankService.class);

    // Interest Rates (Can be dynamic later)
    private static final double DEMAND_RATE = 0.005; // 0.5% daily
    private static final double FIXED_RATE = 0.02; // 2.0% daily

    public BankService() {
        // Create table if not exists (Migration helper)
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS CA_bank (" +
                "uid BIGINT PRIMARY KEY, " +
                "deposit DOUBLE PRECISION DEFAULT 0, " +
                "fixed_deposit DOUBLE PRECISION DEFAULT 0, " +
                "last_interest_date TIMESTAMP, " +
                "fixed_start_date TIMESTAMP, " +
                "fixed_day_count INTEGER DEFAULT 0)";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.error("Failed to init CA_bank table", e);
        }
    }

    public BankRecord getBankRecord(long uid) {
        String sql = "SELECT * FROM CA_bank WHERE uid = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, uid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new BankRecord(
                        uid,
                        rs.getDouble("deposit"),
                        rs.getDouble("fixed_deposit"),
                        rs.getTimestamp("last_interest_date"),
                        rs.getTimestamp("fixed_start_date"),
                        rs.getInt("fixed_day_count"));
            } else {
                return createAccount(uid);
            }
        } catch (SQLException e) {
            logger.error("Error fetching bank record for {}", uid, e);
            return null;
        }
    }

    private BankRecord createAccount(long uid) throws SQLException {
        String sql = "INSERT INTO CA_bank (uid, last_interest_date) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, uid);
            stmt.setTimestamp(2, Timestamp.from(Instant.now()));
            stmt.executeUpdate();
            return new BankRecord(uid, 0, 0, Timestamp.from(Instant.now()), null, 0);
        }
    }

    /**
     * Settle interest for the user.
     * Logic: Calculate days since last settlement * (balance * rate)
     */
    public double settleInterest(long uid) {
        BankRecord record = getBankRecord(uid);
        if (record == null)
            return 0;

        Instant last = record.getLastInterestDate().toInstant();
        Instant now = Instant.now();

        long days = ChronoUnit.DAYS.between(last, now);
        if (days < 1)
            return 0;

        double demandInterest = record.getDeposit() * DEMAND_RATE * days;
        double totalInterest = demandInterest;

        // Update DB
        String sql = "UPDATE CA_bank SET deposit = deposit + ?, last_interest_date = ? WHERE uid = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, totalInterest);
            stmt.setTimestamp(2, Timestamp.from(now));
            stmt.setLong(3, uid);
            stmt.executeUpdate();

            logger.info("Settled interest for user {}: {} credits ({} days)", uid, totalInterest, days);
        } catch (SQLException e) {
            logger.error("Failed to settle interest", e);
        }

        return totalInterest;
    }
}
