package com.elgg.monedamico.models.currency;

import java.util.Map;

public record CurrencyDTO(Map<String, Double> conversion_rates) {
}
