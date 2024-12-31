package com.elgg.monedamico.models.codes;

import com.elgg.monedamico.models.codes.dto.CodesDTO;
import com.elgg.monedamico.models.exchange.Exchange;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Codes extends Exchange {
    private final String API_CODES_URL = API_BASE_URL.concat("codes");
    private final HttpRequest request = createRequest(API_CODES_URL);

    private List<List<String>> supported_codes = null;

    public void getCodes() {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            if (json == null || json.isEmpty()) { throw new IllegalStateException("La respuesta del cuerpo es nula o está vacía"); }

            CodesDTO codesDTO = gson.fromJson(json, CodesDTO.class);

            if (codesDTO == null) { throw new IllegalStateException("El objeto CodesDTO es nulo"); }

            supported_codes = codesDTO.supported_codes();
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage(): "Mensaje de error no disponible";
            System.out.println("Ha ocurrido un error al consultar los codigos disponibles: ".concat(errorMessage));
        }
    }

    public void loadCodes() {
        String dbFileName = "codes.json";
        File file = new File(dbFileName);

        //SI EL ARCHIVO NO EXISTE, GUARDAR LOS CODIGOS
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(dbFileName)) {
                getCodes();

                JsonObject codesJSON = new JsonObject();
                codesJSON.add("supported_codes", gson.toJsonTree(supported_codes));

                gson.toJson(codesJSON, writer);

                System.out.println("Se han guardado los códigos en el archivo 'exchange.json'.");
                return;

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                String errorMessage = e.getMessage() != null ? e.getMessage(): "Mensaje de error no disponible";
                System.out.println("Ha ocurrido un error al intentar guardar los códigos en el archivo JSON: ".concat(errorMessage));
            }
        }

        //SI EL ARCHIVO EXISTE, LEER LOS CODIGOS
        try {
            FileReader reader = new FileReader(dbFileName);
            // Leer y deserializar el archivo JSON a un objeto JsonObject
            JsonObject codesJSON = JsonParser.parseReader(reader).getAsJsonObject();

            CodesDTO codesDTO = gson.fromJson(codesJSON, CodesDTO.class);

            supported_codes = codesDTO.supported_codes();

            System.out.println("Códigos cargados desde el archivo 'exchange.json'.");

        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage(): "Mensaje de error no disponible";
            System.out.println("Ha ocurrido un error al intentar leer el archivo JSON de los codigos: ".concat(errorMessage));
        }
    }

    public void showAvailableCodes() {
        if (supported_codes == null){
            System.out.println("Error, no se han podido obtener los códigos.");
            return;
        }

        supported_codes.forEach(code -> System.out.println("Código: " + code.getFirst() + " - Moneda: " + code.get(1)));
    }

    public List<List<String>> getSupported_codes() {
        return supported_codes;
    }
}
