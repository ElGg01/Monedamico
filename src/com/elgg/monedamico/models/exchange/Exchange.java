package com.elgg.monedamico.models.exchange;

import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class Exchange {
    private final Dotenv dotenv = Dotenv.load();
    private final String API_KEY = dotenv.get("API_KEY");

    protected String API_BASE_URL = "https://v6.exchangerate-api.com/v6/".concat(API_KEY).concat("/");

    protected Gson gson = new Gson();

    protected HttpClient client = HttpClient.newHttpClient();

    protected HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
    }
}
