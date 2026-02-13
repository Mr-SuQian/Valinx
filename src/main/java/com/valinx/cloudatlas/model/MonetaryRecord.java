package com.valinx.cloudatlas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 对应 CA_monetary 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonetaryRecord {
    private long uid;
    private String currency; // 'default' (credits), 'gems', 'energy'
    private double value;

    public static final String CURRENCY_CREDITS = "default";
    public static final String CURRENCY_GEMS = "gems";
    public static final String CURRENCY_ENERGY = "energy";
}
