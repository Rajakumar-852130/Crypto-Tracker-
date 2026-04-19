package com.CryptoTracker.CryptoService;

import com.CryptoTracker.model.CryptoCoin;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoService {

    private final String MARKETS_API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=%s&order=market_cap_desc&sparkline=false";

    public List<CryptoCoin> getTop10Coins(String currency) {
        String url = String.format(MARKETS_API_URL, currency.toLowerCase()) + "&per_page=10&page=1";
        return fetchCoinsFromUrl(url);
    }

    public List<CryptoCoin> getMarketCoins(String currency) {
        // Fetch top 100 coins
        String url = String.format(MARKETS_API_URL, currency.toLowerCase()) + "&per_page=100&page=1";
        return fetchCoinsFromUrl(url);
    }

    public List<CryptoCoin> getCryptoPrices(List<String> coins, String currency) {
        String ids = String.join(",", coins);
        String url = String.format(MARKETS_API_URL, currency.toLowerCase()) + "&ids=" + ids;
        return fetchCoinsFromUrl(url);
    }

    private List<CryptoCoin> fetchCoinsFromUrl(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            headers.set("Accept", "application/json");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>("parameters", headers);

            org.springframework.http.ResponseEntity<String> responseEntity = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            String response = responseEntity.getBody();

            JSONArray jsonArray = new JSONArray(response);
            List<CryptoCoin> coinList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject coinJson = jsonArray.getJSONObject(i);
                String id = coinJson.getString("id");
                String name = coinJson.getString("name");
                String image = coinJson.optString("image", "");
                double price = coinJson.optDouble("current_price", 0.0);
                double change24h = coinJson.optDouble("price_change_percentage_24h", 0.0);
                coinList.add(new CryptoCoin(id, name, image, price, change24h));
            }
            return coinList;

        } catch (org.springframework.web.client.HttpClientErrorException.TooManyRequests e) {
            System.err.println("[Rate Limit] CoinGecko API rate limit hit. Please wait a moment.");
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[Error] Failed to fetch data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}