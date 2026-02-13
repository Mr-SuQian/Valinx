package com.valinx.cloudatlas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.sql.Timestamp;

/**
 * 对应 CA_bank 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankRecord {
    private long uid;
    private double deposit; // 活期存款
    private double fixedDeposit; // 定期存款
    private Timestamp lastInterestDate;
    private Timestamp fixedStartDate; // 定期开始时间
    private int fixedDayCount; // 定期天数
}
