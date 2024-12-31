package com.elgg.monedamico.main;

public class Main {
    public static void main(String[] args) {
        // Inicializar el manejador de entrada:
        InputHandler inputHandler = new InputHandler();

        //Inicializar los códigos de monedas:
        inputHandler.initializeCodes();

        //Loop para seleccionar una opción:
        inputHandler.getMenuOption();
    }
}