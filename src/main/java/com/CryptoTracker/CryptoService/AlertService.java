package com.CryptoTracker.CryptoService;

import com.CryptoTracker.model.PriceAlert;
import com.CryptoTracker.model.CryptoCoin;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class AlertService {

    private final JavaMailSender mailSender;
    private final CryptoService cryptoService;
    private final List<PriceAlert> alertList;

    public AlertService(JavaMailSender mailSender, CryptoService cryptoService) {
        this.mailSender = mailSender;
        this.cryptoService = cryptoService;
        this.alertList = new ArrayList<>();
    }

    public List<PriceAlert> getAlertList() {
        return alertList;
    }

    public void addAlert(PriceAlert alert) {
        alertList.add(alert);
    }

    public void removeAlert(int index) {
        if (index >= 0 && index < alertList.size()) {
            alertList.remove(index);
        }
    }

    /**
     * Runs every 5 minutes to check if any alert conditions are met.
     * If met, sends an email and removes the alert.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes = 300000ms
    public void checkAlerts() {
        if (alertList.isEmpty()) return;

        System.out.println("[AlertService] Checking " + alertList.size() + " active alerts...");

        Iterator<PriceAlert> iterator = alertList.iterator();
        while (iterator.hasNext()) {
            PriceAlert alert = iterator.next();

            try {
                List<String> coinIds = List.of(alert.getCoinId());
                List<CryptoCoin> prices = cryptoService.getCryptoPrices(coinIds, alert.getCurrency());

                if (prices.isEmpty()) continue;

                double currentPrice = prices.get(0).getPrice();
                boolean triggered = false;

                if ("above".equals(alert.getCondition()) && currentPrice >= alert.getTargetPrice()) {
                    triggered = true;
                } else if ("below".equals(alert.getCondition()) && currentPrice <= alert.getTargetPrice()) {
                    triggered = true;
                }

                if (triggered) {
                    System.out.println("[ALERT TRIGGERED] " + alert.getCoinName() + " is now " + currentPrice + " " + alert.getCurrency().toUpperCase());

                    if (alert.getEmail() != null && !alert.getEmail().isEmpty()) {
                        sendAlertEmail(alert, currentPrice);
                    }

                    iterator.remove(); // Remove triggered alert
                }

                // Sleep briefly to avoid rate limits
                Thread.sleep(2000);

            } catch (Exception e) {
                System.err.println("[AlertService] Error checking alert for " + alert.getCoinId() + ": " + e.getMessage());
            }
        }
    }

    private void sendAlertEmail(PriceAlert alert, double currentPrice) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alert.getEmail());
            message.setSubject("🚨 CryptoTracker Alert: " + alert.getCoinName() + " Price Alert!");
            message.setText(
                "Hello!\n\n" +
                "Your price alert has been triggered!\n\n" +
                "📌 Coin: " + alert.getCoinName() + "\n" +
                "📊 Current Price: " + currentPrice + " " + alert.getCurrency().toUpperCase() + "\n" +
                "🎯 Your Target: " + alert.getTargetPrice() + " " + alert.getCurrency().toUpperCase() + "\n" +
                "📈 Condition: Price went " + alert.getCondition().toUpperCase() + " target\n\n" +
                "---\n" +
                "This alert has been automatically removed.\n" +
                "Visit CryptoTracker to set new alerts!\n\n" +
                "- CryptoTracker Team"
            );

            mailSender.send(message);
            System.out.println("[EMAIL SENT] Alert email sent to: " + alert.getEmail());

        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Could not send email to " + alert.getEmail() + ": " + e.getMessage());
        }
    }
}
