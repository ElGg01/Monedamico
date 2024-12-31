package com.elgg.monedamico.models.converter;

import com.elgg.monedamico.models.converter.dto.ConverterDTO;
import com.elgg.monedamico.models.converter.dto.HistoryDTO;
import com.elgg.monedamico.models.exchange.Exchange;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Converter extends Exchange {
    private final String API_PAIR_URL = API_BASE_URL.concat("pair").concat("/%s").concat("/%s");
    private final String dbFileName = "history.json";
    private List<List<String>> history = null;

    public void loadHistory() {
        try (FileReader reader = new FileReader(dbFileName)) {
            JsonObject historyJSON = JsonParser.parseReader(reader).getAsJsonObject();

            // Verificar si el atributo "history" existe en el JSON
            if (historyJSON.has("history")) {
                HistoryDTO historyDTO = gson.fromJson(historyJSON, HistoryDTO.class);
                history = historyDTO.history();
            } else {
                System.out.println("El archivo existe, pero no contiene el atributo 'history'. Inicializando una lista vacía.");
                history = new java.util.ArrayList<>();
            }

        } catch (IOException e) {
            // Si el archivo no existe, inicializar una lista vacía
            System.out.println("El archivo no existe. Inicializando una lista vacía para el historial.");
            history = new java.util.ArrayList<>();
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Mensaje de error no disponible";
            System.out.println("Ha ocurrido un error al intentar cargar el historial: ".concat(errorMessage));
            history = new java.util.ArrayList<>();
        }
    }


    private void saveHistory(String fromCurrency, double amount, String toCurrency, double result) {
        String date = java.time.LocalDate.now().toString(); // Obtener la fecha actual

        // Crear una nueva entrada
        List<String> entry = List.of(date, fromCurrency, String.valueOf(amount), toCurrency, String.format("%.2f", result));

        // Agregar la nueva entrada al historial
        if (history == null) {
            history = new java.util.ArrayList<>(); // Inicializar si el historial es nulo
        }
        history.add(entry);

        try (FileWriter writer = new FileWriter(dbFileName)) {
            // Crear un objeto JSON con el historial actualizado
            JsonObject historyJSON = new JsonObject();
            historyJSON.add("history", gson.toJsonTree(history));

            // Guardar el JSON en el archivo
            gson.toJson(historyJSON, writer);
            System.out.println("El historial se ha guardado correctamente en el archivo.");
        } catch (IOException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Mensaje de error no disponible";
            System.out.println("Ha ocurrido un error al intentar guardar el historial: ".concat(errorMessage));
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Mensaje de error no disponible";
            System.out.println("Ha ocurrido un error inesperado: ".concat(errorMessage));
        }
    }

    public void showHistory() {

        if (history == null) {
            loadHistory();
        }

        if (history.isEmpty()) {
            System.out.println("No hay historial disponible para mostrar.");
            return;
        }

        System.out.println("Historial de conversiones:");
        for (List<String> entry : history) {
            if (entry.size() == 5) {
                String date = entry.get(0);
                String fromCurrency = entry.get(1);
                String amount = entry.get(2);
                String toCurrency = entry.get(3);
                String result = entry.get(4);

                System.out.printf("Fecha: %s | De: %s %s | A: %s %s%n", date, amount, fromCurrency, result, toCurrency);
            } else {
                System.out.println("Entrada de historial inválida: " + entry);
            }
        }
    }


    private double getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            String api_currency_pair_url = String.format(API_PAIR_URL, fromCurrency, toCurrency);
            HttpRequest request = createRequest(api_currency_pair_url);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String json = response.body();

            if (json == null || json.isEmpty()) { throw new IllegalStateException("La respuesta del cuerpo es nula o está vacía"); }

            ConverterDTO converterDTO = gson.fromJson(json, ConverterDTO.class);

            if (converterDTO == null) { throw new IllegalStateException("El objeto CodesDTO es nulo"); }

            return converterDTO.conversion_rate();

        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage(): "Mensaje de error no disponible";
            System.out.println("Ha ocurrido un error al intentar obtener la tasa de cambio: ".concat(errorMessage));
            return -1;
        }
    }

    public void calculateExchange(String fromCurrency, double amount, String toCurrency) {
        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);

        if (exchangeRate == -1) { return; }

        double result = amount * exchangeRate;
        System.out.printf("%.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);
        saveHistory(fromCurrency, amount, toCurrency, result);
    }
}
