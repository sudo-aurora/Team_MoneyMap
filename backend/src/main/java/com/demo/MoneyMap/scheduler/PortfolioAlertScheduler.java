package com.demo.MoneyMap.scheduler;

import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioAlertScheduler {

    private final PortfolioRepository portfolioRepository;
    private final EmailService emailService;

    // Runs every 30 minutes
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional(readOnly = true)
    public void alertLowValuePortfolios() {

        log.info("Running low-value portfolio alert job");

        List<Portfolio> lowValuePortfolios =
                portfolioRepository.findTop5ByOrderByTotalValueAsc();

        for (Portfolio portfolio : lowValuePortfolios) {

            // Example assumption
            String email = portfolio.getClient().getEmail();

            emailService.sendLowValueAlert(
                    email,
                    portfolio.getName(),
                    portfolio.getTotalValue()
            );
        }

        log.info("Low-value portfolio alert job completed");
    }
}
