package com.CryptoTracker.CryptoController;


import com.CryptoTracker.model.CryptoCoin;
import com.CryptoTracker.model.PriceAlert;
import com.CryptoTracker.CryptoService.CryptoService;
import com.CryptoTracker.CryptoService.AlertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
public class CryptoController {

    private final CryptoService cryptoService;
    private final AlertService alertService;

    public CryptoController(CryptoService cryptoService, AlertService alertService) {
        this.cryptoService = cryptoService;
        this.alertService = alertService;
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "usd") String currency, Model model) {
        List<CryptoCoin> topCoins = cryptoService.getTop10Coins(currency);
        model.addAttribute("topCoins", topCoins);
        model.addAttribute("currency", currency);
        return "index";
    }

    @GetMapping("/market")
    public String market(@RequestParam(defaultValue = "usd") String currency, Model model) {
        List<CryptoCoin> marketCoins = cryptoService.getMarketCoins(currency);
        model.addAttribute("marketCoins", marketCoins);
        model.addAttribute("currency", currency);
        return "market";
    }

    @GetMapping("/alerts")
    public String alertsPage(@RequestParam(required = false) String success, Model model) {
        model.addAttribute("alertList", alertService.getAlertList());
        model.addAttribute("success", success);
        return "alerts";
    }

    @PostMapping("/alerts")
    public String addAlert(@RequestParam String coinId,
                           @RequestParam String coinName,
                           @RequestParam double targetPrice,
                           @RequestParam String condition,
                           @RequestParam(defaultValue = "usd") String currency,
                           @RequestParam(required = false, defaultValue = "") String email,
                           RedirectAttributes redirectAttributes) {
        PriceAlert alert = new PriceAlert(
            coinId.trim().toLowerCase(),
            coinName.trim(),
            targetPrice,
            condition,
            currency,
            email
        );
        alertService.addAlert(alert);
        redirectAttributes.addAttribute("success", "true");
        return "redirect:/alerts";
    }

    @PostMapping("/alerts/delete")
    public String deleteAlert(@RequestParam int index) {
        alertService.removeAlert(index);
        return "redirect:/alerts";
    }

    @PostMapping("/track")
    public String track(@RequestParam String coins, @RequestParam(defaultValue = "usd") String currency, Model model) {
        List<String> coinList = Arrays.stream(coins.split(","))
                                      .map(String::trim)
                                      .map(String::toLowerCase)
                                      .toList();
        List<CryptoCoin> result = cryptoService.getCryptoPrices(coinList, currency);
        model.addAttribute("coins", result);
        model.addAttribute("currency", currency);
        return "result";
    }
}