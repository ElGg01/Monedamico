package com.elgg.monedamico.models.currency;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class CurrencyConverter {
    private final String apiKey;
    private final Gson gson = new Gson();

    public CurrencyConverter(String apiKey) {
        this.apiKey = apiKey;
    }

    public void convertCurrency(String currencyOrigin, double amount, String currencyDestiny) {

        double result;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + currencyOrigin))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();
            CurrencyDTO currencyDTO = gson.fromJson(json, CurrencyDTO.class);
            Currency currency = new Currency(currencyDTO);

            result = amount * currency.getConversionRates().get(currencyDestiny);

            System.out.println(amount + " " + currencyOrigin + " es igual a: " + result + " " + currencyDestiny);

        } catch (Exception e) {
            System.out.println("Ocurrió un error al convertir la moneda: " + e.getMessage());
        }
    }
}
