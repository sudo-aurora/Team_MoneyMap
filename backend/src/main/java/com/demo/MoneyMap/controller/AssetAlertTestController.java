package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.scheduler.AssetPriceAlertScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class AssetAlertTestController {

    private final AssetPriceAlertScheduler scheduler;

    /**
     * Manually triggers asset price drop alerts
     */
    @GetMapping("/asset-alerts")
    public String triggerAssetAlerts() {

        scheduler.checkAllAssetsForPriceDrop();

        return "Asset price drop alert job triggered successfully";
    }
}
