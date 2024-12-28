package com.elgg.monedamico.models.currency;

import java.util.Scanner;

public class CurrencyInputHandler {
    private final String[] currencies = {"USD", "MXN", "ARS", "COP", "CLP"};
    private final Scanner scanner = new Scanner(System.in);

    public void displayMenu() {
        System.out.println("""
                 __  __                      _                 _          \s
                |  \\/  | ___  _ __   ___  __| | __ _ _ __ ___ (_) ___ ___ \s
                | |\\/| |/ _ \\| '_ \\ / _ \\/ _` |/ _` | '_ ` _ \\| |/ __/ _ \\\s
                | |  | | (_) | | | |  __/ (_| | (_| | | | | | | | (_| (_) |
                |_|  |_|\\___/|_| |_|\\___|\\__,_|\\__,_|_| |_| |_|_|\\___\\___/\s
                """);
        System.out.println("Convierte tus monedas de forma rápida y sencilla.");
        System.out.println("Opciones de cambio disponibles:");

        for (int i = 0; i < currencies.length; i++) {
            System.out.print(currencies[i] + (i == currencies.length - 1 ? "\n" : ", "));
        }
    }

    public String getCurrencyChoice(String message) {
        System.out.println(message);

        while (true) {
            try {

                String choice = scanner.nextLine().trim().toUpperCase();

                if (isValidCurrency(choice) || choice.equalsIgnoreCase("salir")) {
                    return choice;
                } else {
                    throw new Exception("Por favor, ingresa una divisa valida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public boolean isValidCurrency(String choice) {
        for (String currency : currencies) {
            if (currency.equalsIgnoreCase(choice)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserWantsToExit(String choice) {
        return choice.equalsIgnoreCase("salir");
    }

    public double getAmount(String currency) {
        System.out.println("Ingresa el monto a convertir de " + currency + ":");
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingresa un número válido.");
            }
        }
    }

    public void closeScanner() {
        scanner.close();
    }
}
