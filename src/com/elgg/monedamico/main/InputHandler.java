package com.elgg.monedamico.main;

import com.elgg.monedamico.models.codes.Codes;
import com.elgg.monedamico.models.converter.Converter;

import java.util.List;
import java.util.Scanner;

public class InputHandler {

    private final Scanner scanner = new Scanner(System.in);
    private final Converter converter = new Converter();
    private final Codes codes = new Codes();

    private void showMenu() {
        System.out.println("""
                 __  __                      _                 _          \s
                |  \\/  | ___  _ __   ___  __| | __ _ _ __ ___ (_) ___ ___ \s
                | |\\/| |/ _ \\| '_ \\ / _ \\/ _` |/ _` | '_ ` _ \\| |/ __/ _ \\\s
                | |  | | (_) | | | |  __/ (_| | (_| | | | | | | | (_| (_) |
                |_|  |_|\\___/|_|_|_|\\___|\\__,_|\\__,_|_| |_| |_|_|\\___\\___/\s
                | |__  _   _  | ____| |/ ___| __ _                        \s
                | '_ \\| | | | |  _| | | |  _ / _` |                       \s
                | |_) | |_| | | |___| | |_| | (_| |                       \s
                |_.__/ \\__, | |_____|_|\\____|\\__, |                       \s
                       |___/                 |___/                        \s
                """);
        System.out.println("Software para convertir monedas.\n");
        System.out.println("Selecciona una opción:");
        System.out.println("0. Salir.");
        System.out.println("1. Convertir moneda.");
        System.out.println("2. Ver monedas disponibles.");
        System.out.println("3. Ver historial de conversiones.\n");
    }

    protected void initializeCodes() {
        codes.loadCodes();

        if (codes.getSupported_codes() == null) {
            System.out.println("Error, no se han podido obtener los códigos de moneda. Comprueba tu conexión a internet o los permisos de creación de archivos.");
            System.exit(-1);
        }
    }

    private boolean existCode(String code) {

        for (List<String> listCode : codes.getSupported_codes()) {
            if (listCode.getFirst().equals(code)) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidAmount(double amount) {
        return amount > 0;
    }

    protected void getMenuOption() {
            exitProgram:
            while (true) {
                showMenu();

                System.out.print("Tu elección: ");
                String option = scanner.nextLine();
                System.out.println();

                if (option.isEmpty()) {
                    System.out.println("Por favor, ingresa un número de la lista.");
                    continue;
                }

                int optionNumber;

                try {
                    optionNumber = Integer.parseInt(option);
                } catch (NumberFormatException e) {
                    System.out.println("Por favor, ingresa un número válido.");
                    continue;
                }

                switch (optionNumber) {
                    case 0:
                        break exitProgram;
                    case 1:
                        System.out.println("¡Has seleccionado convertir monedas!");

                        try {
                            System.out.print("Ingresa la moneda de origen: ");
                            String fromCurrency = scanner.nextLine().toUpperCase().trim();

                            if (!existCode(fromCurrency)) {
                                System.out.println("La moneda ingresada no es válida. Escriba '2' para ver las monedas disponibles.");
                                break;
                            }

                            System.out.print("Ingresa la cantidad a convertir: ");
                            double amount = Double.parseDouble(scanner.nextLine());

                            if (!isValidAmount(amount)) {
                                System.out.println("Por favor, ingresa una cantidad válida. Debe ser mayor a 0.");
                                break;
                            }

                            System.out.print("Ingresa la moneda de destino: ");
                            String toCurrency = scanner.nextLine().toUpperCase().trim();

                            if (!existCode(toCurrency)) {
                                System.out.println("La moneda ingresada no es válida. Escriba '2' para ver las monedas disponibles.");
                                break;
                            }

                            converter.calculateExchange(fromCurrency, amount, toCurrency);
                        } catch (NumberFormatException e) {
                            System.out.println("Por favor, ingresa un número válido.");
                            break;
                        } catch (Exception e) {
                            System.out.println("Ha ocurrido un error descontrolado al intentar convertir las monedas.");
                            break;
                        }

                        break;
                    case 2:
                        System.out.println("¡Has seleccionado ver las monedas disponibles!");
                        codes.showAvailableCodes();
                        break;
                    case 3:
                        converter.showHistory();
                        break;
                    default:
                        System.out.println("Por favor, ingresa un número de la lista.");
                }
            }
            scanner.close();
            System.out.println("Gracias por usar Monedamico. ¡Hasta luego!");
    }
}
