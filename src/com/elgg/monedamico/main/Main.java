package com.elgg.monedamico.main;

import com.elgg.monedamico.models.currency.CurrencyConverter;
import com.elgg.monedamico.models.currency.CurrencyInputHandler;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String API_KEY = dotenv.get("API_KEY");

        CurrencyConverter converter = new CurrencyConverter(API_KEY);
        CurrencyInputHandler inputHandler = new CurrencyInputHandler();

        while (true) {
            inputHandler.displayMenu();
            String currencyOrigin = inputHandler.getCurrencyChoice("Escribe el tipo de divisa a convertir o escribe 'salir' para terminar: ");

            if (inputHandler.isUserWantsToExit(currencyOrigin)) {
                break;
            }

            double amount = inputHandler.getAmount(currencyOrigin);
            String destinationCurrency = inputHandler.getCurrencyChoice("Escribe la divisa a la que quieres convertir: ");

            if (inputHandler.isUserWantsToExit(destinationCurrency)) {
                break;
            }

            converter.convertCurrency(currencyOrigin, amount, destinationCurrency);
        }

        inputHandler.closeScanner();
    }
}