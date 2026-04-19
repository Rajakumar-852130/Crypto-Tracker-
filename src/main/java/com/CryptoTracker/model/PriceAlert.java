package com.CryptoTracker.model;

public class PriceAlert {
    private String coinId;
    private String coinName;
    private double targetPrice;
    private String condition; // "above" or "below"
    private String currency;
    private String email;

    public PriceAlert() {}

    public PriceAlert(String coinId, String coinName, double targetPrice, String condition, String currency, String email) {
        this.coinId = coinId;
        this.coinName = coinName;
        this.targetPrice = targetPrice;
        this.condition = condition;
        this.currency = currency;
        this.email = email;
    }

    // Getters & Setters
    public String getCoinId() { return coinId; }
    public void setCoinId(String coinId) { this.coinId = coinId; }
    public String getCoinName() { return coinName; }
    public void setCoinName(String coinName) { this.coinName = coinName; }
    public double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(double targetPrice) { this.targetPrice = targetPrice; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
