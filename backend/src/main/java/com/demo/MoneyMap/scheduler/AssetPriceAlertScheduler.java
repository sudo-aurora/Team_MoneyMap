package com.demo.MoneyMap.scheduler;

import com.demo.MoneyMap.dto.response.FinnhubQuoteResponse;
import com.demo.MoneyMap.entity.Asset;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.repository.AssetRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.EmailService;
import com.demo.MoneyMap.service.MarketQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetPriceAlertScheduler {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final MarketQuoteService quoteService;
    private final EmailService emailService;

    @Value("${alerts.price-drop.percent-threshold}")
    private double dropThreshold;

    @Value("${alerts.price-drop.cooldown-minutes}")
    private long cooldownMinutes;

    @Scheduled(cron = "0 */30 * * * *") // every 30 min
    @Transactional
    public void checkAllAssetsForPriceDrop() {

        log.info("Running asset price drop alert job");

        Duration cooldown = Duration.ofMinutes(cooldownMinutes);

        List<Portfolio> portfolios = portfolioRepository.findAll();

        for (Portfolio portfolio : portfolios) {

            String email = portfolio.getClient().getEmail();

            for (Asset asset : portfolio.getAssets()) {

                FinnhubQuoteResponse quote =
                        quoteService.getQuote(asset.getSymbol());

                if (quote.getChangePercent() <= dropThreshold &&
                        asset.canSendAlert(cooldown)) {

                    emailService.sendAssetDropAlert(
                            email,
                            portfolio.getName(),
                            asset.getSymbol(),
                            BigDecimal.valueOf(quote.getCurrentPrice()),
                            quote.getChangePercent()
                    );

                    asset.markAlertSent();
                    assetRepository.save(asset);
                }
            }
        }

        log.info("Asset price drop alert job completed");
    }
}
