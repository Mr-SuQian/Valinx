package com.valinx.cloudatlas.service;

import com.valinx.cloudatlas.model.MonetaryRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SigninService {
    private static final Logger logger = LoggerFactory.getLogger(SigninService.class);
    private final EconomyService economyService;

    public SigninService(EconomyService economyService) {
        this.economyService = economyService;
    }

    public SigninResult performSignin(long uid) {
        // TODO: Port the streak and bonus logic from TS
        double baseReward = 100.0;
        economyService.gain(uid, baseReward, MonetaryRecord.CURRENCY_CREDITS);

        return new SigninResult(uid, baseReward, 1, "今日签到成功！");
    }

    public record SigninResult(long uid, double reward, int streak, String message) {
    }
}
