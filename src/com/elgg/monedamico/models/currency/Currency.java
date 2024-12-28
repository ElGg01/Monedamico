package com.elgg.monedamico.models.currency;

import java.util.Map;

public class Currency {
    private final Map<String, Double> conversion_rates;

    public Currency(CurrencyDTO currency) {
        this.conversion_rates = currency.conversion_rates();
    }

    public Map<String, Double> getConversionRates() {
        return conversion_rates;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "conversion_rates=" + conversion_rates +
                '}';
    }
}
